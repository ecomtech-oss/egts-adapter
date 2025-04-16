package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class SubRecordResponse(
    val confirmedRecordNumber: Short = 0,
    val recordStatus: Byte = 0,
) : SubRecordData()