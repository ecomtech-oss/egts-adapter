package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.ExternalSensorData
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.VendorData
import tech.ecom.egts.library.utils.readUShort
import tech.ecom.egts.library.utils.toLittleEndianByteArray
import tech.ecom.egts.library.utils.toStringFromVdtByteArray
import tech.ecom.egts.library.utils.toVdtByteArray

class ExternalDataEncoder : AbstractSubRecordEncoder<ExternalSensorData>(
    subRecordTypeId = SubRecordType.EGTS_SR_EXT_DATA.id,
    fieldName = SubRecordType.EGTS_SR_EXT_DATA.fieldName,
) {

    override fun performEncode(egtsEntity: ExternalSensorData): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                write(vendorIdentifier.toLittleEndianByteArray())
                write(vendorsData.dataType.toLittleEndianByteArray())
                vendorsData.data?.let { write(vendorsData.data.toVdtByteArray()) }
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): ExternalSensorData {
        ByteArrayInputStream(byteArray).apply {
            val vendorIdentifier = readUShort().also {
                logger.trace("ExternalData vendorIdentifier is {}", it)
            }

            val dataType = readUShort().also {
                logger.trace("VendorData dataType is {}", it)
            }

            val data = readNBytes(available()).toStringFromVdtByteArray().also {
                logger.trace("VendorData data is {}", it)
            }

            return ExternalSensorData(
                vendorsData = VendorData(
                    data = data,
                    dataType = dataType,
                ),
                vendorIdentifier = vendorIdentifier,
            )
        }
    }
}