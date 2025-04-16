package tech.ecom.egts.library.constants

class PacketHeaderConstants {
    companion object {
        const val DEFAULT_EGTS_PROTOCOL_VERSION = 1
        const val NO_ENCODING_SECURITY_KEY_ID_FIELD_VALUE = 0
        const val EGTS_PACKET_PREFIX = "00"
        const val NO_ROUTING_ROUTE_FIELD_VALUE = "0"
        const val NO_ENCRYPTION_ENCRYPTION_ALG_FIELD_VALUE = "00"
        const val NO_COMPRESSION_FLAG = "0"
        const val DEFAULT_PRIORITY_PRIORITY_FIELD_VALUE = "11"

        const val HEADER_LENGTH_FOR_HEADER_WITH_NO_OPTIONAL_FIELDS = 11
        const val NO_ENCODING_FLAG = 0
    }
}