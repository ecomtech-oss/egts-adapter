package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.ExternalSensorData
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.VendorData

class ExternalSensorDataEncoderTest : AbstractIntegrationTest() {
    private val TEST_VENDOR_DATA = "Hello VendorData world!"
    private val TEST_VENDOR_IDENTIFIER = Random.nextInt(from = 0, until = UShort.MAX_VALUE.toInt() + 1).toUShort()
    private val TEST_VENDOR_DATA_TYPE = Random.nextInt(from = 0, until = UShort.MAX_VALUE.toInt() + 1).toUShort()

    @Test
    fun `WHEN RnisExternalData encoded with some vendorData THEN they it's preserved after decoding`() {
        // given
        val externalSensorData = ExternalSensorData(
            vendorsData = VendorData(
                data = TEST_VENDOR_DATA,
                dataType = TEST_VENDOR_DATA_TYPE,
            ),
            vendorIdentifier = TEST_VENDOR_IDENTIFIER,
        )

        // when
        val encodedAndDecodedRnisExternalData = externalDataEncoder.decode(
            externalDataEncoder.encode(externalSensorData),
        )

        // then
        with(encodedAndDecodedRnisExternalData) {
            assertThat(vendorIdentifier).isEqualTo(TEST_VENDOR_IDENTIFIER)
            assertThat(vendorsData.dataType).isEqualTo(TEST_VENDOR_DATA_TYPE)
            assertThat(vendorsData.data).isEqualTo(TEST_VENDOR_DATA)
        }
    }

    @Test
    fun `WHEN vendorData changed THEN byte representation of RnisExternalData changed`() {
        // given
        val externalSensorData = ExternalSensorData(
            vendorsData = VendorData(
                data = TEST_VENDOR_DATA,
                dataType = TEST_VENDOR_DATA_TYPE,
            ),
            vendorIdentifier = TEST_VENDOR_IDENTIFIER,
        )
        val anotherExternalSensorData = ExternalSensorData(
            vendorsData = VendorData(
                data = TEST_VENDOR_DATA + "some suffix",
                dataType = TEST_VENDOR_DATA_TYPE,
            ),
            vendorIdentifier = TEST_VENDOR_IDENTIFIER,
        )

        // when
        val rnisExternalDataBytes = externalDataEncoder.encode(externalSensorData)
        val anotherRnisExternalDataBytes = externalDataEncoder.encode(anotherExternalSensorData)

        // then
        assertThat(rnisExternalDataBytes).isNotEqualTo(anotherRnisExternalDataBytes)
    }
}