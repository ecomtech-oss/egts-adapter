package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.AnalogSensorData

class AnalogSensorDataEncoderTest : AbstractIntegrationTest() {
    private val TEST_ANALOG_SENSOR_VALUE = 1111
    private val TEST_ANALOG_SENSOR_NUMBER = 1.toByte()

    @Test
    fun `WHEN AnalogSensorData encoded with some analogSensor and analogSensorValue values THEN they're preserved after decoding`() {
        // given
        val analogSensorData = AnalogSensorData(
            analogSensorNumber = TEST_ANALOG_SENSOR_NUMBER,
            analogSensorValue = TEST_ANALOG_SENSOR_VALUE,
        )

        // when
        val encodedAndDecodedAnalogSensorData = analogSensorDataEncoder.decode(
            analogSensorDataEncoder.encode(analogSensorData),
        )

        // then
        with(encodedAndDecodedAnalogSensorData) {
            assertThat(analogSensorNumber).isEqualTo(TEST_ANALOG_SENSOR_NUMBER)
            assertThat(analogSensorValue).isEqualTo(TEST_ANALOG_SENSOR_VALUE)
        }
    }

    @Test
    fun `WHEN analogSensorValue changed THEN byte representation of AnalogSensorData changed as well`() {
        // given
        val analogSensorData = AnalogSensorData(
            analogSensorNumber = TEST_ANALOG_SENSOR_NUMBER,
            analogSensorValue = TEST_ANALOG_SENSOR_VALUE,
        )
        val anotherAnalogSensorData = AnalogSensorData(
            analogSensorNumber = TEST_ANALOG_SENSOR_NUMBER,
            analogSensorValue = TEST_ANALOG_SENSOR_VALUE + 1,
        )

        // when
        val analogSensorDataBytes = analogSensorDataEncoder.encode(analogSensorData)
        val anotherAnalogSensorDataBytes = analogSensorDataEncoder.encode(anotherAnalogSensorData)

        // then
        assertThat(analogSensorDataBytes).isNotEqualTo(anotherAnalogSensorDataBytes)
    }
}