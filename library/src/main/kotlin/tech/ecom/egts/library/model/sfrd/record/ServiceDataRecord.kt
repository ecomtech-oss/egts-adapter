package tech.ecom.egts.library.model.sfrd.record

import tech.ecom.egts.library.constants.RecordConstants.Companion.DEFAULT_PROCESSING_PRIORITY

data class ServiceDataRecord(
    val recordNumber: UShort = 0u,
    val sourceServiceOnDevice: Boolean = true,
    val recipientServiceOnDevice: Boolean = true,
    val group: Boolean = false,
    val recordProcessingPriority: String = DEFAULT_PROCESSING_PRIORITY,
    val objectIdentifier: UInt? = null,
    val eventIdentifier: UInt? = null,
    val time: UInt? = null,
    val sourceServiceType: ServiceType = ServiceType.AUTH_SERVICE,
    val recipientServiceType: ServiceType = ServiceType.TELE_DATA_SERVICE,
    val recordData: RecordData,
)

enum class ServiceType(val byteValue: Byte) {
    AUTH_SERVICE(1),
    TELE_DATA_SERVICE(2),
    ;
    companion object {
        fun fromByteValue(byteValue: Byte) = if (byteValue == 1.toByte()) AUTH_SERVICE else TELE_DATA_SERVICE
    }
}