package tech.ecom.egts.library.model.sfrd.record

import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecord

data class RecordData(
    val subRecordList: List<SubRecord> = ArrayList(),
)