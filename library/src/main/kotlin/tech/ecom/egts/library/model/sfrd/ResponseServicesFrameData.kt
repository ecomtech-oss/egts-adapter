package tech.ecom.egts.library.model.sfrd

data class ResponseServicesFrameData(
    val responsePacketId: UShort,
    val processingResult: TransportLayerProcessingResult = TransportLayerProcessingResult.EGTS_PC_OK,
    val serviceDataRecords: ServiceDataRecords? = null,
) : ServicesFrameData()

// error codes description available at table A.14 of egts protocol description
enum class TransportLayerProcessingResult(val byteCode: Byte) {
    EGTS_PC_OK(0),
    EGTS_PC_IN_PROGRESS(1),
    EGTS_PC_UNS_PROTOCOL(128.toByte()),
    EGTS_PC_DECRYPT_ERROR(129.toByte()),
    EGTS_PC_INC_HEADERFORM(131.toByte()),
    EGTS_PC_INC_DATAFORM(132.toByte()),
    EGTS_PC_UNS_TYPE(133.toByte()),
    EGTS_PC_HEADERCRC_ERROR(137.toByte()),
    EGTS_PC_DATACRC_ERROR(138.toByte()),
    EGTS_PC_INVDATALEN(139.toByte()),
    EGTS_PC_AUTH_DENIED(151.toByte()),
    GTS_PC_INC_DATETIME(154.toByte()),
    ;
    companion object {
        private val byteCodeMap = TransportLayerProcessingResult.entries.associateBy(
            tech.ecom.egts.library.model.sfrd.TransportLayerProcessingResult::byteCode,
        )
        fun fromByteCode(byteCode: Byte?): TransportLayerProcessingResult = TransportLayerProcessingResult.byteCodeMap[byteCode]!!
    }

    fun toByteArray() = byteArrayOf(this.byteCode)
}