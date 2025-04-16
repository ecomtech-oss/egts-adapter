package tech.ecom.egts.library.constants

import java.time.Instant

class EgtsConstants {
    companion object {
        const val DEFAULT_DISPATCHER_TYPE_FOR_RNIS_INTEGRATION = 0
        const val RST_OK: Byte = 0
        val EGTS_START_SECONDS = Instant.parse("2010-01-01T00:00:00Z").epochSecond
    }
}