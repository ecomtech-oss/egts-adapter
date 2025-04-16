package tech.ecom.egts.library.encoder.sfrd

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.encoder.AbstractEgtsEncoder
import tech.ecom.egts.library.model.sfrd.ResponseServicesFrameData
import tech.ecom.egts.library.model.sfrd.TransportLayerProcessingResult
import tech.ecom.egts.library.utils.readByte
import tech.ecom.egts.library.utils.readUShort
import tech.ecom.egts.library.utils.toHexString
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class ResponseServicesFrameDataEncoder(
    private val serviceDataRecordsEncoder: ServiceDataRecordsEncoder,
) : AbstractEgtsEncoder<ResponseServicesFrameData>("RESPONSE_SERVICES_FRAME_DATA") {
    override fun performEncode(egtsEntity: ResponseServicesFrameData): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                write(responsePacketId.toLittleEndianByteArray())
                write(processingResult.toByteArray())
                serviceDataRecords?.let { write(serviceDataRecordsEncoder.encode(it)) }
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): ResponseServicesFrameData {
        logger.debug("decoding ResponseServicesFrameData {}", byteArray.toHexString())
        ByteArrayInputStream(byteArray).apply {
            val responsePacketId = readUShort().also {
                logger.trace("responsePacketId is {}", it)
            }
            val processingResult = TransportLayerProcessingResult.fromByteCode(readByte()).also {
                logger.trace("processing result is {}", it)
            }

            val serviceDataRecords = takeIf { it.available() > 0 }
                ?.let { it.readNBytes(it.available()) }
                ?.let { serviceDataRecordsEncoder.decode(it) }

            return ResponseServicesFrameData(
                responsePacketId = responsePacketId,
                processingResult = processingResult,
                serviceDataRecords = serviceDataRecords,
            )
        }
    }
}