package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.SubRecordResponse

class SubRecordResponseTest : AbstractIntegrationTest() {
    private val TEST_CONFIRMED_RECORD_NUMBER = 1111.toShort()
    private val TEST_RECORD_STATUS = 1.toByte()

    @Test
    fun `WHEN SubRecordResponse encoded with some confirmedRecordNumber and recordStatus THEN they preserved after decoding`() {
        // given
        val subRecordResponse = SubRecordResponse(
            confirmedRecordNumber = TEST_CONFIRMED_RECORD_NUMBER,
            recordStatus = TEST_RECORD_STATUS,
        )

        // when
        val encodedAndDecodedSubRecordResponse = subRecordResponseEncoder.decode(
            subRecordResponseEncoder.encode(subRecordResponse),
        )

        // then
        with(encodedAndDecodedSubRecordResponse) {
            assertThat(confirmedRecordNumber).isEqualTo(TEST_CONFIRMED_RECORD_NUMBER)
            assertThat(recordStatus).isEqualTo(TEST_RECORD_STATUS)
        }
    }

    @Test
    fun `WHEN confirmedRecordNumber changed THEN byte representation of SubRecordResponse changed`() {
        // given
        val subRecordResponse = SubRecordResponse(
            confirmedRecordNumber = TEST_CONFIRMED_RECORD_NUMBER,
            recordStatus = TEST_RECORD_STATUS,
        )
        val anotherSubRecordResponse = SubRecordResponse(
            confirmedRecordNumber = (TEST_CONFIRMED_RECORD_NUMBER + 1).toShort(),
            recordStatus = TEST_RECORD_STATUS,
        )

        // when
        val subRecordResponseBytes = subRecordResponseEncoder.encode(subRecordResponse)
        val anotherSubRecordResponseBytes = subRecordResponseEncoder.encode(anotherSubRecordResponse)

        // then
        assertThat(subRecordResponseBytes).isNotEqualTo(anotherSubRecordResponseBytes)
    }
}