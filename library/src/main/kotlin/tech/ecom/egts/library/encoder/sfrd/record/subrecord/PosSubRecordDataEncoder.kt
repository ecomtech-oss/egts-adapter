package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.sql.Timestamp
import java.time.Instant
import kotlin.math.roundToInt
import kotlin.math.sign
import tech.ecom.egts.library.constants.EgtsConstants
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.PosSubRecordData
import tech.ecom.egts.library.utils.readInt
import tech.ecom.egts.library.utils.readUInt
import tech.ecom.egts.library.utils.roundToFirstDecimal
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class PosSubRecordDataEncoder : AbstractSubRecordEncoder<PosSubRecordData>(
    subRecordTypeId = SubRecordType.EGTS_SR_POS_DATA.id,
    fieldName = SubRecordType.EGTS_SR_POS_DATA.fieldName,
) {
    private val MAX_UNSIGNED_INT_VALUE = UInt.MAX_VALUE.toLong() // 4294967295

    override fun performEncode(egtsEntity: PosSubRecordData): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                val navigationTimeSeconds = (
                    Instant.ofEpochMilli(egtsEntity.navigationTime.time).epochSecond - EgtsConstants.EGTS_START_SECONDS
                    ).toInt()
                write(navigationTimeSeconds.toLittleEndianByteArray())

                // with division we're normalizing double to fit -1.0..1.0 and then multiplying to max uint value
                // to get max precision available within uint 4 bytes range
                val normalizedLatitude = (egtsEntity.latitude.let { it * it.sign } / 90.0 * MAX_UNSIGNED_INT_VALUE).toUInt()
                write(normalizedLatitude.toLittleEndianByteArray())
                val normalizedLongitude = (longitude.let { it * it.sign } / 180.0 * MAX_UNSIGNED_INT_VALUE).toUInt()
                write(normalizedLongitude.toLittleEndianByteArray())

                val isEasternLongitude = longitude.sign == 1.0
                val isNorthernLatitude = latitude.sign == 1.0

                val flagBits =
                    ((if (isAltitudePresent) 1 else 0) shl 7) or
                        ((if (isEasternLongitude) 0 else 1) shl 6) or
                        ((if (isNorthernLatitude) 0 else 1) shl 5) or
                        ((if (isMoving) 1 else 0) shl 4) or
                        ((if (isSentFromBlackBox) 1 else 0) shl 3) or
                        ((if (coordinateSystem) 1 else 0) shl 2) or
                        ((if (fix) 1 else 0) shl 1) or
                        ((if (isValid) 1 else 0) shl 0)
                write(byteArrayOf(flagBits.toByte()))

                // process of combining speed and direction attributes parts to single 2 byte value looks wierd but this
                // conversion is defined by egts protocol itself - table Б.2 at protocol description
                val roundedSpeed = (speed * 10).roundToInt()
                val speedWithClearedTwoBits = (roundedSpeed and 0x3FFF).toUShort()
                val directionHighestBit = if (direction > 255) 1 else 0
                val directionAltSignSpeedCombinedValue =
                    speedWithClearedTwoBits or ((directionHighestBit shl 15) or (altitudeSign shl 14))
                        .toUShort()
                write(directionAltSignSpeedCombinedValue.toLittleEndianByteArray())

                val directionLeastSignificantByte = direction % 256
                write(directionLeastSignificantByte)

                write(encodeOdometer(odometer))

                write(byteArrayOf(digitalInputs.toInt(radix = 2).toByte()))
                write(byteArrayOf(source.toInt(radix = 2).toByte()))
            }
        }.toByteArray()

    private fun encodeOdometer(odometer: Double): ByteArray {
        val odometerUInt32 = (odometer.roundToFirstDecimal() * 10).toInt() // Multiply by 10 to handle 0.1 km precision
        return ByteArrayOutputStream().apply {
            write(odometerUInt32.toLittleEndianByteArray(), 0, 3)
        }.toByteArray()
    }

    override fun performDecode(byteArray: ByteArray): PosSubRecordData {
        ByteArrayInputStream(byteArray).apply {
            val navigationTime = Timestamp((readInt() + EgtsConstants.EGTS_START_SECONDS) * 1000L)

            val normalizedLatitude = readUInt().toLong()
            val latitude = (normalizedLatitude / MAX_UNSIGNED_INT_VALUE.toDouble()) * 90.0

            val normalizedLongitude = readUInt().toLong()
            val longitude = (normalizedLongitude / MAX_UNSIGNED_INT_VALUE.toDouble()) * 180.0

            val flagBits = read() // Read the byte directly

            val isAltitudePresent = (flagBits and 0b10000000) != 0
            val isEasternLongitude = (flagBits and 0b01000000) == 0
            val isNorthernLatitude = (flagBits and 0b00100000) == 0
            val isMoving = (flagBits and 0b00010000) != 0
            val isBlackBox = (flagBits and 0b00001000) != 0
            val coordinateSystem = (flagBits and 0b00000100) != 0
            val fix = (flagBits and 0b00000010) != 0
            val isValid = (flagBits and 0b00000001) != 0

            // Decode speed (14 bits) with DIRH and ALTS (2 bits)
            // first extract speed 8 lowest bits, then DIRH and ALTS bits concatenated with speed 6 highest bits
            val spd8lowestBits = read()
            val dirhAltsAndSpd6highestBits = read()
            val directionHighestBit = ((dirhAltsAndSpd6highestBits shr 7) and 1)
            val altitudeSign = ((dirhAltsAndSpd6highestBits shr 6) and 1)
            val spd6highestBits = dirhAltsAndSpd6highestBits and 0x3F
            val speed14Bits = (spd6highestBits shl 8) or spd8lowestBits
            val speed = (speed14Bits / 10.0).roundToFirstDecimal()

            val direction = (read() + if (directionHighestBit == 1) 256 else 0)

            val odometerBytes = readNBytes(3)
            val odometer = ByteBuffer.wrap(byteArrayOf(odometerBytes[0], odometerBytes[1], odometerBytes[2], 0))
                .order(ByteOrder.LITTLE_ENDIAN)
                .int / 10.0

            val digitalInputs = read().toString(2).padStart(8, '0')
            val source = read().toString(2).padStart(8, '0')

            return PosSubRecordData(
                navigationTime = navigationTime,
                latitude = if (isNorthernLatitude) latitude else -latitude,
                longitude = if (isEasternLongitude) longitude else -longitude,
                isAltitudePresent = isAltitudePresent,
                isMoving = isMoving,
                isSentFromBlackBox = isBlackBox,
                coordinateSystem = coordinateSystem,
                fix = fix,
                isValid = isValid,
                altitudeSign = altitudeSign,
                direction = direction,
                odometer = odometer,
                digitalInputs = digitalInputs,
                source = source,
                speed = speed,
            )
        }
    }
}