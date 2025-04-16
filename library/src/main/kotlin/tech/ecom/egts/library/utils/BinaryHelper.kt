package tech.ecom.egts.library.utils

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.math.roundToInt
import tech.ecom.egts.library.exception.EgtsAdapterException
import tech.ecom.egts.library.exception.EgtsExceptionErrorCode

fun Char.toBoolean() = this == '1'
fun Boolean.toBitCharValue() = if (this) '1' else '0'

fun ByteArrayInputStream.readByte() = readNBytes(1)[0]
fun ByteArrayInputStream.readInt() = readNBytes(4).toIntFromLittleEndian()
fun ByteArrayInputStream.readUInt() = readNBytes(4).toUIntFromLittleEndian()
fun ByteArrayInputStream.readShort() = readNBytes(2).toShortFromLittleEndian()
fun ByteArrayInputStream.readUShort() = readNBytes(2).toUShortFromLittleEndian()

fun ByteArrayInputStream.readByteToUnsignedInt() = java.lang.Byte.toUnsignedInt(readNBytes(1)[0])
fun ByteArrayInputStream.readThreeBytesToPositiveInt(): Int {
    return this.readNBytes(3).let { bytes ->
        if (bytes.size < 3) {
            throw EgtsAdapterException(
                code = EgtsExceptionErrorCode.EGTS_DECODE_EXCEPTION,
                errorMessage = "Less than three bytes available to read.",
            )
        }

        ((bytes[0].toInt() and 0xFF)) or
            ((bytes[1].toInt() and 0xFF) shl 8) or
            ((bytes[2].toInt() and 0xFF) shl 16)
    }
}

fun Int.toLittleEndianByteArray(): ByteArray {
    return ByteBuffer.allocate(4)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putInt(this)
        .array()
}

fun Int.toThreeByteLittleEndianByteArray(): ByteArray {
    if (this < 0 || this > 16777215) {
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.EGTS_ENCODE_EXCEPTION,
            errorMessage = "Value must be non-negative and less than 16,777,216.",
        )
    }
    return this.toLittleEndianByteArray().copyOfRange(0, 3)
}

fun Short.toLittleEndianByteArray(): ByteArray {
    return ByteBuffer.allocate(2)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putShort(this)
        .array()
}

fun UShort.toLittleEndianByteArray() = toShort().toLittleEndianByteArray()
fun UInt.toLittleEndianByteArray() = toInt().toLittleEndianByteArray()

const val INT_BYTE_LENGTH = 4
fun ByteArray.toIntFromLittleEndian(): Int {
    if (size != INT_BYTE_LENGTH) {
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.INCORRECT_BINARY_HELPER_USAGE,
            errorMessage = "bytearray should contain exactly 4 bytes to be properly converted to Int",
        )
    }
    return ByteBuffer.wrap(this)
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
}

const val SHORT_BYTE_LENGTH = 2
fun ByteArray.toShortFromLittleEndian(): Short {
    if (size != SHORT_BYTE_LENGTH) {
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.INCORRECT_BINARY_HELPER_USAGE,
            errorMessage = "bytearray should contain exactly 2 bytes to be properly converted to Short",
        )
    }
    return ByteBuffer.wrap(this)
        .order(ByteOrder.LITTLE_ENDIAN)
        .short
}

fun ByteArray.toUShortFromLittleEndian() = toShortFromLittleEndian().toUShort()
fun ByteArray.toUIntFromLittleEndian() = toIntFromLittleEndian().toUInt()

fun Double.roundToFirstDecimal(): Double = (this * 10).roundToInt() / 10.0

fun ByteArray.toHexString(): String {
    val hexString = StringBuilder()
    for (b in this) {
        hexString.append(String.format("x%02x", b))
    }
    return hexString.toString()
}

const val LATIN1_ENCODING_NAME = "ISO-8859-1"
fun String.toVdtByteArray() = toByteArray(Charset.forName(LATIN1_ENCODING_NAME))

fun ByteArray.toStringFromVdtByteArray() = toString(Charset.forName(LATIN1_ENCODING_NAME))

fun String.hexStringToByteArray(): ByteArray =
    filterNot { it == 'x' } // Removes all 'x' characters
        .chunked(2) // Splits into pairs of characters
        .map { it.toInt(16).toByte() } // Converts each pair to a byte
        .toByteArray()

fun ByteArray.calculateCrc8(): Int {
    var crc = 0xFF.toByte()
    for (b in this) {
        crc = (crc.toInt() xor b.toInt()).toByte()
        for (i in 0..7) {
            crc = if (crc.toInt() and 0x80 != 0) {
                (crc.toInt() shl 1 xor 0x31).toByte()
            } else {
                (crc.toInt() shl 1).toByte()
            }
        }
    }
    return crc.toInt()
}

fun ByteArray.calculateCrc16(): Short {
    var crc = 0xFFFF
    val polynomial = 0x1021 // 0001 0000 0010 0001  (0, 5, 12)
    for (b in this) {
        for (i in 0..7) {
            val bit = b.toInt() shr 7 - i and 1 == 1
            val c15 = crc shr 15 and 1 == 1
            crc = crc shl 1
            if (c15 xor bit) crc = crc xor polynomial
        }
    }
    return (crc and 0xffff).toShort()
}