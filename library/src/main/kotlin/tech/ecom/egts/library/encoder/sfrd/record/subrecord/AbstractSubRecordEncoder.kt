package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import tech.ecom.egts.library.encoder.AbstractEgtsEncoder

abstract class AbstractSubRecordEncoder<T>(
    val subRecordTypeId: Int,
    fieldName: String,
) : AbstractEgtsEncoder<T>(fieldName)