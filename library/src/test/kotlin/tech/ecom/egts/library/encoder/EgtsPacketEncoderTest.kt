package tech.ecom.egts.library.encoder

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.io.IOException
import kotlin.random.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.encoder.sfrd.AppServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ResponseServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.DispatcherIdentityEncoder
import tech.ecom.egts.library.exception.EgtsAdapterException
import tech.ecom.egts.library.model.EgtsPacket
import tech.ecom.egts.library.model.EgtsPacketTest.Companion.AUTH_PACKET_PID
import tech.ecom.egts.library.model.PacketType
import tech.ecom.egts.library.model.sfrd.AppServicesFrameData
import tech.ecom.egts.library.model.sfrd.ServiceDataRecords
import tech.ecom.egts.library.model.sfrd.record.RecordData
import tech.ecom.egts.library.model.sfrd.record.ServiceDataRecord
import tech.ecom.egts.library.model.sfrd.record.ServiceType
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecord
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.DispatcherIdentity

class EgtsPacketEncoderTest : AbstractIntegrationTest() {

    @Test
    fun `WHEN ArrayOutOfBounds at decoding THEN EgtsAdapterException is thrown`() {
        // given
        val byteArrayShortEnoughToTriggerOutOfBoundsException = byteArrayOf(1)

        // then
        assertThrows<EgtsAdapterException> { egtsPacketEncoder.decode(byteArrayShortEnoughToTriggerOutOfBoundsException) }
    }

    @Test
    fun `WHEN wrong protocol version at decoding THEN CourierTrackingException is thrown`() {
        // given
        val byteArrayStartingWithWrongProtocolVersion = byteArrayOf(0)

        // then
        assertThrows<EgtsAdapterException> { egtsPacketEncoder.decode(byteArrayStartingWithWrongProtocolVersion) }
    }

    @Test
    fun `WHEN IoException at encode THEN EgtsAdapterException is thrown`() {
        // given
        val appServicesFrameDataEncoderMock = mockk<AppServicesFrameDataEncoder> {
            every { encode(any()) } throws IOException("Test exception")
        }

        val egtsPacketEncoderMock = spyk(
            EgtsPacketEncoder(
                appServicesFrameDataEncoder = appServicesFrameDataEncoderMock,
                responseServicesFrameDataEncoder = mockk<ResponseServicesFrameDataEncoder>(),
            ),
        )

        val testPacketToEncode = assembleAuthPacket()

        // then
        assertThrows<EgtsAdapterException> { egtsPacketEncoderMock.encode(testPacketToEncode) }
    }

    private fun assembleAuthPacket(): EgtsPacket {
        val authSubRecord = SubRecord(
            subRecordTypeId = dispatcherIdentityEncoder.subRecordTypeId,
            subRecordData = DispatcherIdentity(dispatcherId = Random.nextInt(from = 0, until = 10000)),
        )

        val serviceDataRecord = ServiceDataRecord(
            sourceServiceType = ServiceType.AUTH_SERVICE,
            recipientServiceType = ServiceType.AUTH_SERVICE,
            recordData = RecordData(
                subRecordList = listOf(authSubRecord),
            ),
        )

        val servicesFrameData = AppServicesFrameData(
            serviceDataRecords = ServiceDataRecords(
                sdrList = listOf(serviceDataRecord),
            ),
        )

        val egtsPacket = EgtsPacket(
            packetIdentifier = AUTH_PACKET_PID,
            packetType = PacketType.APP_DATA,
            servicesFrameData = servicesFrameData,
        )

        return egtsPacket
    }
}