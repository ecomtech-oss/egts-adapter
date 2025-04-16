package tech.ecom.egts.library.exception

class EgtsAdapterException(
    val code: EgtsExceptionErrorCode,
    val errorMessage: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(errorMessage, cause)

enum class EgtsExceptionErrorCode {
    INCORRECT_BINARY_HELPER_USAGE, // exception which shows that wrong binary helper fun was picked to perform some operation
    UNSUPPORTED_EGTS_PROTOCOL_VERSION, // current implementation works only with EGTS protocol version 1
    WRONG_EGTS_DATA_CHECKSUM, // used when decoding received packets
    WRONG_EGTS_HEADER_CHECKSUM, // use this if implement received packets headers cs verification. I did not for the sake of performance
    EGTS_DECODE_EXCEPTION, // used to show that exception was thrown while decoding packet
    EGTS_ENCODE_EXCEPTION, // used to show that exception was thrown while encoding packet
    MISSING_OBJECT_IDENTIFIER_AT_SERVICE_DATA_RECORD, // used to show that object identifier missing while objectIdFieldExists flag set
    MISSING_EVENT_IDENTIFIER_AT_SERVICE_DATA_RECORD, // used to show that event identifier missing while eventIdFieldExists flag set
    MISSING_TIME_FIELD_AT_SERVICE_DATA_RECORD, // used to show that time field missing while timeFieldExists flag set
    EGTS_NEGATIVE_VALUE_CONVERTED_TO_UNSIGNED_ONE, // used at client code to show that packet field should not be negative
}