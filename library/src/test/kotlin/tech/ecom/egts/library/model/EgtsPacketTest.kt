package tech.ecom.egts.library.model

import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.constants.EgtsConstants.Companion.DEFAULT_DISPATCHER_TYPE_FOR_RNIS_INTEGRATION
import tech.ecom.egts.library.constants.EgtsConstants.Companion.RST_OK
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.DEFAULT_PRIORITY_PRIORITY_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.EGTS_PACKET_PREFIX
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.DEFAULT_EGTS_PROTOCOL_VERSION
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.HEADER_LENGTH_FOR_HEADER_WITH_NO_OPTIONAL_FIELDS
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_COMPRESSION_FLAG
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCODING_FLAG
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCRYPTION_ENCRYPTION_ALG_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ROUTING_ROUTE_FIELD_VALUE
import tech.ecom.egts.library.constants.PacketHeaderConstants.Companion.NO_ENCODING_SECURITY_KEY_ID_FIELD_VALUE
import tech.ecom.egts.library.constants.RecordConstants.Companion.DEFAULT_PROCESSING_PRIORITY
import tech.ecom.egts.library.model.sfrd.AppServicesFrameData
import tech.ecom.egts.library.model.sfrd.ResponseServicesFrameData
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.DispatcherIdentity
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.SubRecordResponse
import tech.ecom.egts.library.utils.hexStringToByteArray
import tech.ecom.egts.library.utils.toHexString

class EgtsPacketTest : AbstractIntegrationTest() {
    companion object {
        private val AUTH_PACKET = "x01x00x03x0bx00x0fx00x00x00x01x9bx08x00x00x00x98x01x01x05x05x00x00xbax0bx00x00x6bxcb"
        private val AUTH_PACKET_RESPONSE = "x01x00x03x0bx00x10x00x00x00x00xb3x01x00x00x06x00x00x00x18x01x01x00x03x00x00x00x00x24x26"
        private val AUTH_PACKET_RESULT = "x01x00x03x0bx00x0bx00x01x00x01xc2x04x00x01x00x00x01x01x09x01x00x00xddx45"

        private val AUTH_PACKET_RESULT_RESPONSE = "x01x00x03x0bx00x10x00x00x00x00xb3x00x00x00x06x00x00x00x18x01x01x00x03x00x00x00x00x00x8e"

        private val TELEMATICS_PACKET = "x01x00x03x0bx00x8cx00x01x00x01x2ax3bx00x00x00x99x01x00x00x00x02x02x10x15x00xb4xa5xbdx1bxc7x62x34" +
            "x6ax83xd8x91xadx50xe9x83x54x00x00x00x00x00x18x04x00xc2x02x00x00x18x04x00xc5x00x00x00x2cx0bx00xc0xddx00x00x6fx72x64x65x72x73" +
            "x3dx18x04x00xc1x01x00x00x3bx00x01x00x99x01x00x00x00x02x02x10x15x00xb4xa5xbdx1bxc7x62x34x6ax83xd8x91xadx50xe9x83x54x00x00x00" +
            "x00x00x18x04x00xc2x02x00x00x18x04x00xc5x00x00x00x2cx0bx00xc0xddx00x00x6fx72x64x65x72x73x3dx18x04x00xc1x01x00x00xa2xf9"
        private val TELEMATICS_PACKET_RESPONSE = "x01x00x03x0bx00x10x00x02x00x00x3fx02x00x00x06x00x02x00x18x02x02x00x03x00x00x00x00x2dx7e"

        val AUTH_PACKET_PID: UShort = 0u
        const val TEST_DISPATCHER_ID = 3002

        @JvmStatic
        fun hexPacketTestCaseProvider(): Stream<HexPacketTestCase> {
            return Stream.of(
                HexPacketTestCase(
                    "AUTH_PACKET",
                    AUTH_PACKET
                ),
                HexPacketTestCase(
                    "AUTH_PACKET_RESPONSE",
                    AUTH_PACKET_RESPONSE
                ),
                HexPacketTestCase(
                    "AUTH_PACKET_RESULT",
                    AUTH_PACKET_RESULT
                ),
                HexPacketTestCase(
                    "AUTH_PACKET_RESULT_RESPONSE",
                    AUTH_PACKET_RESULT_RESPONSE
                ),
                HexPacketTestCase(
                    "TELEMATICS_PACKET",
                    TELEMATICS_PACKET
                ),
                HexPacketTestCase(
                    "TELEMATICS_PACKET_RESPONSE",
                    TELEMATICS_PACKET_RESPONSE
                ),
            )
        }
    }

    @Test
    fun `WHEN auth packet hex String representation decoded THEN packet fields matching expected ones`() {
        // when
        val authPacket = egtsPacketEncoder.decode(AUTH_PACKET.hexStringToByteArray())
        val sfrd = authPacket.servicesFrameData as AppServicesFrameData
        val subRecord = sfrd.serviceDataRecords.sdrList.first().recordData.subRecordList.first()
        val dispacherIdSubrecordData = subRecord
            .subRecordData as DispatcherIdentity

        // then
        with(authPacket) {
            assertThat(packetIdentifier).isEqualTo(AUTH_PACKET_PID)
            assertThat(packetType).isEqualTo(tech.ecom.egts.library.model.PacketType.APP_DATA)
        }

        with(subRecord) {
            assertThat(subRecordTypeId).isEqualTo(dispatcherIdentityEncoder.subRecordTypeId)
        }

        with(dispacherIdSubrecordData) {
            assertThat(dispatcherType).isEqualTo(DEFAULT_DISPATCHER_TYPE_FOR_RNIS_INTEGRATION)
            assertThat(dispatcherId).isEqualTo(TEST_DISPATCHER_ID)
        }

        with(sfrd.serviceDataRecords.sdrList.first()) {
            assertThat(recordNumber).isEqualTo(0.toUShort())
            assertThat(sourceServiceOnDevice).isEqualTo(true)
            assertThat(recipientServiceOnDevice).isEqualTo(false)
            assertThat(group).isEqualTo(false)
            assertThat(recordProcessingPriority).isEqualTo(DEFAULT_PROCESSING_PRIORITY)
        }
    }

    @Test
    fun `WHEN auth packet result response hex String representation decoded THEN packet fields mathing expected ones`() {
        // when
        val authPacketResultResponse = egtsPacketEncoder.decode(AUTH_PACKET_RESULT_RESPONSE.hexStringToByteArray())
        val sfrd = authPacketResultResponse.servicesFrameData as ResponseServicesFrameData
        val subRecordResponse = sfrd
            .serviceDataRecords!!
            .sdrList.first()
            .recordData
            .subRecordList.first()
            .subRecordData as SubRecordResponse

        // then
        with(authPacketResultResponse) {
            assertThat(packetIdentifier).isEqualTo(AUTH_PACKET_PID)
            assertThat(packetType).isEqualTo(tech.ecom.egts.library.model.PacketType.RESPONSE)
        }

        with(subRecordResponse) {
            assertThat(confirmedRecordNumber).isEqualTo(0)
            assertThat(recordStatus).isEqualTo(RST_OK)
        }

        with(sfrd.serviceDataRecords!!.sdrList.first()) {
            assertThat(sourceServiceOnDevice).isEqualTo(false)
            assertThat(recipientServiceOnDevice).isEqualTo(false)
            assertThat(group).isEqualTo(false)
            assertThat(recordProcessingPriority).isEqualTo(DEFAULT_PROCESSING_PRIORITY)
        }
    }

    @ParameterizedTest
    @MethodSource("hexPacketTestCaseProvider")
    fun `WHEN packet hex Strings decoded THEN constant header fields math defined constants`(testCase: HexPacketTestCase) {
        // when
        val packet = egtsPacketEncoder.decode(testCase.hexString.hexStringToByteArray())

        // then
        with(packet) {
            assertThat(protocolVersion).isEqualTo(DEFAULT_EGTS_PROTOCOL_VERSION)
            assertThat(securityKeyId).isEqualTo(NO_ENCODING_SECURITY_KEY_ID_FIELD_VALUE)

            assertThat(prefix).isEqualTo(EGTS_PACKET_PREFIX)
            assertThat(route).isEqualTo(NO_ROUTING_ROUTE_FIELD_VALUE)
            assertThat(encryptionAlg).isEqualTo(NO_ENCRYPTION_ENCRYPTION_ALG_FIELD_VALUE)
            assertThat(compression).isEqualTo(NO_COMPRESSION_FLAG)
            assertThat(priority).isEqualTo(DEFAULT_PRIORITY_PRIORITY_FIELD_VALUE)

            assertThat(headerLength).isEqualTo(HEADER_LENGTH_FOR_HEADER_WITH_NO_OPTIONAL_FIELDS)
            assertThat(headerEncoding).isEqualTo(NO_ENCODING_FLAG)
        }
    }

    @ParameterizedTest
    @MethodSource("hexPacketTestCaseProvider")
    fun `WHEN packet hex Strings decoded and encoded back THEN hex string matches the original one`(testCase: HexPacketTestCase) {
        // given
        val testCaseHexString = testCase.hexString

        // when
        val packet = egtsPacketEncoder.decode(testCaseHexString.hexStringToByteArray())

        // then
        val packetHexRepresentation = egtsPacketEncoder.encode(packet).toHexString()
        assertThat(packetHexRepresentation).isEqualTo(testCaseHexString)
    }

    data class HexPacketTestCase(
        val packetName: String,
        val hexString: String,
    )
}