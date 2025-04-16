package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class AnalogSensorData(
    val analogSensorNumber: Byte,
    val analogSensorValue: Int,
) : SubRecordData()