package tech.ecom.egts.library.encoder

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import tech.ecom.egts.library.constants.PacketHeaderConstants
import tech.ecom.egts.library.encoder.sfrd.AppServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ResponseServicesFrameDataEncoder
import tech.ecom.egts.library.exception.EgtsAdapterException
import tech.ecom.egts.library.exception.EgtsExceptionErrorCode
import tech.ecom.egts.library.model.EgtsPacket
import tech.ecom.egts.library.model.PacketType
import tech.ecom.egts.library.model.sfrd.AppServicesFrameData
import tech.ecom.egts.library.model.sfrd.ResponseServicesFrameData
import tech.ecom.egts.library.utils.calculateCrc16
import tech.ecom.egts.library.utils.calculateCrc8
import tech.ecom.egts.library.utils.readByteToUnsignedInt
import tech.ecom.egts.library.utils.readShort
import tech.ecom.egts.library.utils.readUShort
import tech.ecom.egts.library.utils.toHexString
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class EgtsPacketEncoder(
    private val appServicesFrameDataEncoder: AppServicesFrameDataEncoder,
    private val responseServicesFrameDataEncoder: ResponseServicesFrameDataEncoder,
) : AbstractEgtsEncoder<EgtsPacket>("EGTS_PACKET") {

    override fun performEncode(egtsEntity: EgtsPacket): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                val serviceDataByteArray = when (packetType) {
                    PacketType.APP_DATA ->
                        appServicesFrameDataEncoder.encode(servicesFrameData as AppServicesFrameData)
                    PacketType.RESPONSE ->
                        responseServicesFrameDataEncoder.encode(servicesFrameData as tech.ecom.egts.library.model.sfrd.ResponseServicesFrameData)
                }.also {
                    logger.trace("encoded SFRD as {}", it.toHexString())
                }
                val frameDataLength = serviceDataByteArray.size

                write(protocolVersion)
                write(securityKeyId)
                write((prefix + route + encryptionAlg + compression + priority).toInt(2))
                write(headerLength)
                write(headerEncoding)
                write(frameDataLength.toShort().toLittleEndianByteArray())
                write(packetIdentifier.toLittleEndianByteArray())
                write(packetType.code)
                val headerCheckSum = toByteArray().calculateCrc8().also {
                    logger.trace("calculated header checksum as {}", it)
                }
                write(headerCheckSum)

                if (frameDataLength > 0) {
                    write(serviceDataByteArray)
                    val sfrdCheckSum = serviceDataByteArray.calculateCrc16().also {
                        logger.trace("calculated SFRD checksum as {}", it)
                    }
                    write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(sfrdCheckSum).array())
                }
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): EgtsPacket {
        logger.debug("decoding EGTS packet from {}", byteArray.toHexString())
        ByteArrayInputStream(byteArray).apply {
            val protocolVersion = readByteToUnsignedInt().also {
                if (it != PacketHeaderConstants.DEFAULT_EGTS_PROTOCOL_VERSION) {
                    throw EgtsAdapterException(
                        code = EgtsExceptionErrorCode.UNSUPPORTED_EGTS_PROTOCOL_VERSION,
                        "Unsupported EGTS protocol version $it",
                    )
                }
            }
            logger.trace("protocol version is - {}", protocolVersion)

            val securityKeyId = readByteToUnsignedInt().also {
                logger.trace("security key id is - {}", it)
            }

            val flags = read().toString(2).padStart(8, '0').also {
                logger.trace("flags byte read as {}", it)
            }
            val prefix = flags.substring(0, 2) // flags << 7, flags << 6
            val route = flags[2].toString() // flags << 5
            val encryptionAlg = flags.substring(3, 5) // flags << 4, flags << 3
            val compression = flags[5].toString() // flags << 2
            val priority = flags.substring(6, 8) // flags << 1, flags << 0

            val headerLength = readByteToUnsignedInt().also { logger.trace("header length is {}", it) }
            val headerEncoding = readByteToUnsignedInt().also { logger.trace("header encoding is {}", it) }
            val frameDataLength = readShort().toInt().also { logger.trace("frame data length is {}", it) }
            val packetIdentifier = readUShort().also { logger.trace("packet identifier is {}", it) }
            val packetType = PacketType.fromCode(readByteToUnsignedInt()).also { logger.trace("packet type is {}", it) }
            readByteToUnsignedInt().also { logger.trace("header checksum is {}", it) }

            val dataFrameBytes = readNBytes(frameDataLength).also {
                logger.trace("service frame data sdrs bytes are {}", it.toHexString())
            }

            val servicesFrameData = when (packetType) {
                PacketType.RESPONSE -> responseServicesFrameDataEncoder.decode(dataFrameBytes)
                PacketType.APP_DATA -> appServicesFrameDataEncoder.decode(dataFrameBytes)
            }

            val servicesFrameDataCheckSum = readShort().also { logger.trace("service frame data sdrs checksum is {}", it) }
            if (servicesFrameDataCheckSum != dataFrameBytes.calculateCrc16()) {
                throw EgtsAdapterException(
                    code = EgtsExceptionErrorCode.WRONG_EGTS_DATA_CHECKSUM,
                    "wrong EGTS service frame data checksum",
                )
            }
            logger.debug("valid packet")

            return EgtsPacket(
                protocolVersion = protocolVersion,
                securityKeyId = securityKeyId,
                prefix = prefix,
                route = route,
                encryptionAlg = encryptionAlg,
                compression = compression,
                priority = priority,
                headerLength = headerLength,
                headerEncoding = headerEncoding,
                packetIdentifier = packetIdentifier,
                packetType = packetType,
                servicesFrameData = servicesFrameData,
            )
        }
    }
}