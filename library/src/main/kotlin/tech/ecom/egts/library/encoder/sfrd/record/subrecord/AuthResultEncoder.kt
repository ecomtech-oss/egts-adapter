package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.AuthResult
import tech.ecom.egts.library.utils.readByte

class AuthResultEncoder : AbstractSubRecordEncoder<AuthResult>(
    subRecordTypeId = SubRecordType.EGTS_SR_RESULT_CODE.id,
    fieldName = SubRecordType.EGTS_SR_RESULT_CODE.fieldName,
) {

    override fun performEncode(egtsEntity: AuthResult): ByteArray =
        ByteArrayOutputStream().apply {
            write(byteArrayOf(egtsEntity.resultCode))
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): AuthResult {
        val resultCode = ByteArrayInputStream(byteArray).readByte().also {
            logger.trace("AuthResult byteValue is {}", it)
        }

        return AuthResult(
            resultCode = resultCode,
        )
    }
}