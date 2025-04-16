package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.DispatcherIdentity
import tech.ecom.egts.library.utils.readInt
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class DispatcherIdentityEncoder : AbstractSubRecordEncoder<DispatcherIdentity>(
    subRecordTypeId = SubRecordType.EGTS_SR_DISPATCHER_IDENTITY.id,
    fieldName = SubRecordType.EGTS_SR_DISPATCHER_IDENTITY.fieldName,
) {

    override fun performEncode(egtsEntity: DispatcherIdentity): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                write(dispatcherType)
                write(dispatcherId.toLittleEndianByteArray())
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): DispatcherIdentity {
        ByteArrayInputStream(byteArray).apply {
            val dispatcherType = read()
            val dispatcherId = readInt()

            return DispatcherIdentity(
                dispatcherType = dispatcherType,
                dispatcherId = dispatcherId,
            )
        }
    }
}