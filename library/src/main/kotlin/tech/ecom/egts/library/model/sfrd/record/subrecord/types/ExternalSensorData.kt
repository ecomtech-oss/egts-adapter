package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class ExternalSensorData(
    val vendorsData: VendorData,
    val vendorIdentifier: UShort = 0xDDC0.toUShort(),
) : SubRecordData()
data class VendorData(
    val data: String?,
    val dataType: UShort = 0.toUShort(),
)