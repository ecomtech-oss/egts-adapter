package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.SubRecordResponse
import tech.ecom.egts.library.utils.readByte
import tech.ecom.egts.library.utils.readShort
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class SubRecordResponseEncoder : AbstractSubRecordEncoder<SubRecordResponse>(
    subRecordTypeId = SubRecordType.EGTS_SR_RECORD_RESPONSE.id,
    fieldName = SubRecordType.EGTS_SR_RECORD_RESPONSE.fieldName,
) {

    override fun performEncode(egtsEntity: SubRecordResponse): ByteArray =
        ByteArrayOutputStream().apply {
            write(egtsEntity.confirmedRecordNumber.toLittleEndianByteArray())
            write(byteArrayOf(egtsEntity.recordStatus))
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): SubRecordResponse {
        ByteArrayInputStream(byteArray).apply {
            val confirmedRecordNumber = readShort().also {
                logger.trace("confirmedRecordNumber is {}", it)
            }
            val recordStatus = readByte().also {
                logger.trace("record status is {}", it)
            }

            return SubRecordResponse(
                confirmedRecordNumber = confirmedRecordNumber,
                recordStatus = recordStatus,
            )
        }
    }
}