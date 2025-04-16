package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.PosSubRecordData
import tech.ecom.egts.library.utils.hexStringToByteArray

class PosSubRecordDataTest : AbstractIntegrationTest() {
    private val DOUBLE_TO_BINARY_PRECISION_FOR_TESTS = 0.0000001

    private val TEST_NAVIGATION_TIME = Timestamp.from(
        LocalDateTime.parse("2024-09-30T20:32:36.000").toInstant(ZoneOffset.ofHours(3)),
    )
    private val TEST_LATITUDE = 37.33756602446958
    private val TEST_LONGITUDE = -122.04120234168163

    private val TEST_IS_ALT_PRESENT = false
    private val TEST_IS_MOVING = true
    private val TEST_IS_BLACK_BOX = false
    private val TEST_COORDINATE_SYSTEM = false // 0 - WGS-84, 1 - ПЗ-90.0
    private val TEST_FIX = false // 0 - 2D, 1 - 3D
    private val TEST_IS_VALID = true

    private val TEST_ALTITUDE_SIGN = 0

    private val TEST_DIRECTION = 340
    private val TEST_ODOMETER = 0.0
    private val TEST_DIGITAL_INPUTS = "00000000"
    private val TEST_SOURCE = "00000000"
    private val TEST_SPEED = 100.1

    private val ENCODED_SUBRECORD_HEX_STRING = "xb4xa5xbdx1bxc7x62x34x6ax83xd8x91xadx51xe9x83x54x00x00x00x00x00"

    @Test
    fun `WHEN decoded test subrecord THEN it's attributes matches expected ones`() {
        // given
        val posSubRecordData = posSubRecordDataEncoder.decode(ENCODED_SUBRECORD_HEX_STRING.hexStringToByteArray())

        // then
        with(posSubRecordData) {
            assertThat(navigationTime).isEqualTo(TEST_NAVIGATION_TIME)
            assertThat(latitude).isEqualTo(TEST_LATITUDE)
            assertThat(longitude).isEqualTo(TEST_LONGITUDE)
            assertThat(isAltitudePresent).isEqualTo(TEST_IS_ALT_PRESENT)
            assertThat(isMoving).isEqualTo(TEST_IS_MOVING)
            assertThat(isSentFromBlackBox).isEqualTo(TEST_IS_BLACK_BOX)
            assertThat(coordinateSystem).isEqualTo(TEST_COORDINATE_SYSTEM)
            assertThat(fix).isEqualTo(TEST_FIX)
            assertThat(isValid).isEqualTo(TEST_IS_VALID)
            assertThat(altitudeSign).isEqualTo(TEST_ALTITUDE_SIGN)
            assertThat(direction).isEqualTo(TEST_DIRECTION)
            assertThat(odometer).isEqualTo(TEST_ODOMETER)
            assertThat(digitalInputs).isEqualTo(TEST_DIGITAL_INPUTS)
            assertThat(source).isEqualTo(TEST_SOURCE)
            assertThat(speed).isEqualTo(TEST_SPEED)
        }
    }

    @Test
    fun `WHEN posSubRecordData encoded with some fields values THEN these are preserved after decoding`() {
        // given
        val posSubRecordData = preparePosSubRecordData()

        // when
        val encodedAndDecodedPosSubRecordData = posSubRecordDataEncoder.decode(
            posSubRecordDataEncoder.encode(posSubRecordData),
        )

        // then
        with(encodedAndDecodedPosSubRecordData) {
            assertThat(navigationTime).isEqualTo(TEST_NAVIGATION_TIME)
            assertThat(latitude).isEqualTo(TEST_LATITUDE, within(DOUBLE_TO_BINARY_PRECISION_FOR_TESTS))
            assertThat(longitude).isEqualTo(TEST_LONGITUDE, within(DOUBLE_TO_BINARY_PRECISION_FOR_TESTS))
            assertThat(isAltitudePresent).isEqualTo(TEST_IS_ALT_PRESENT)
            assertThat(isMoving).isEqualTo(TEST_IS_MOVING)
            assertThat(isSentFromBlackBox).isEqualTo(TEST_IS_BLACK_BOX)
            assertThat(coordinateSystem).isEqualTo(TEST_COORDINATE_SYSTEM)
            assertThat(fix).isEqualTo(TEST_FIX)
            assertThat(isValid).isEqualTo(TEST_IS_VALID)
            assertThat(altitudeSign).isEqualTo(TEST_ALTITUDE_SIGN)
            assertThat(direction).isEqualTo(TEST_DIRECTION)
            assertThat(odometer).isEqualTo(TEST_ODOMETER, within(DOUBLE_TO_BINARY_PRECISION_FOR_TESTS))
            assertThat(digitalInputs).isEqualTo(TEST_DIGITAL_INPUTS)
            assertThat(source).isEqualTo(TEST_SOURCE)
            assertThat(speed).isEqualTo(TEST_SPEED, within(DOUBLE_TO_BINARY_PRECISION_FOR_TESTS))
        }
    }

    @Test
    fun `WHEN some posSubRecordData attributes changed THEN byte representation also changed`() {
        // given
        val posSubRecordData = preparePosSubRecordData(speed = TEST_SPEED)
        val anotherPosSubRecordData = preparePosSubRecordData(speed = TEST_SPEED - 0.1)

        // when
        val posSubRecordDataBytes = posSubRecordDataEncoder.encode(posSubRecordData)
        val anotherPosSubRecordDataBytes = posSubRecordDataEncoder.encode(anotherPosSubRecordData)

        // then
        assertThat(posSubRecordDataBytes).isNotEqualTo(anotherPosSubRecordDataBytes)
    }

    private fun preparePosSubRecordData(
        speed: Double = TEST_SPEED,
    ) = PosSubRecordData(
        navigationTime = TEST_NAVIGATION_TIME,
        latitude = TEST_LATITUDE,
        longitude = TEST_LONGITUDE,

        isMoving = TEST_IS_MOVING,
        isSentFromBlackBox = TEST_IS_BLACK_BOX,
        coordinateSystem = TEST_COORDINATE_SYSTEM,
        fix = TEST_FIX,
        isValid = TEST_IS_VALID,

        direction = TEST_DIRECTION,
        odometer = TEST_ODOMETER,
        digitalInputs = TEST_DIGITAL_INPUTS,
        source = TEST_SOURCE,
        speed = speed,
    )
}