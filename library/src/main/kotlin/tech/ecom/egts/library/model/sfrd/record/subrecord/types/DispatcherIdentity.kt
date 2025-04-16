package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import tech.ecom.egts.library.constants.EgtsConstants.Companion.DEFAULT_DISPATCHER_TYPE_FOR_RNIS_INTEGRATION
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class DispatcherIdentity(
    val dispatcherType: Int = DEFAULT_DISPATCHER_TYPE_FOR_RNIS_INTEGRATION,
    val dispatcherId: Int,
) : SubRecordData()