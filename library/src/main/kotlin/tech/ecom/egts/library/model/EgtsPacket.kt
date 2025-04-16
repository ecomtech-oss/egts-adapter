package tech.ecom.egts.library.model

import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.DEFAULT_PRIORITY_PRIORITY_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.EGTS_PACKET_PREFIX
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.DEFAULT_EGTS_PROTOCOL_VERSION
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.HEADER_LENGTH_FOR_HEADER_WITH_NO_OPTIONAL_FIELDS
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_COMPRESSION_FLAG
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCODING_FLAG
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCRYPTION_ENCRYPTION_ALG_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ROUTING_ROUTE_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCODING_SECURITY_KEY_ID_FIELD_VALUE
import tech.ecom.egts.library.model.sfrd.ServicesFrameData

data class EgtsPacket(
    val protocolVersion: Int = DEFAULT_EGTS_PROTOCOL_VERSION,
    val securityKeyId: Int = NO_ENCODING_SECURITY_KEY_ID_FIELD_VALUE,
    val prefix: String = EGTS_PACKET_PREFIX,
    val route: String = NO_ROUTING_ROUTE_FIELD_VALUE,
    val encryptionAlg: String = NO_ENCRYPTION_ENCRYPTION_ALG_FIELD_VALUE,
    val compression: String = NO_COMPRESSION_FLAG,
    val priority: String = DEFAULT_PRIORITY_PRIORITY_FIELD_VALUE,
    val headerLength: Int = HEADER_LENGTH_FOR_HEADER_WITH_NO_OPTIONAL_FIELDS,
    val headerEncoding: Int = NO_ENCODING_FLAG,
    val packetIdentifier: UShort,
    val packetType: PacketType = PacketType.APP_DATA,
    val servicesFrameData: ServicesFrameData? = null,
)

enum class PacketType(val code: Int) {
    RESPONSE(0),
    APP_DATA(1),
    ;
    companion object {
        fun fromCode(code: Int) = if (code == 0) RESPONSE else APP_DATA
    }
}