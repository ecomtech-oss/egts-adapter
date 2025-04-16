package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.DispatcherIdentity

class DispatcherIdentityTest : AbstractIntegrationTest() {
    private val TEST_DISPATCHER_ID = 1111
    private val TEST_DISPATCHER_TYPE = 2

    @Test
    fun `WHEN DispatcherIdentity encoded with some DispatcherId and DispatcherType THEN they preserved after decoding`() {
        // given
        val dispatcherIdentity = DispatcherIdentity(
            dispatcherId = TEST_DISPATCHER_ID,
            dispatcherType = TEST_DISPATCHER_TYPE,
        )

        // when
        val encodedAndDecodedDispatcherIdentity = dispatcherIdentityEncoder.decode(
            dispatcherIdentityEncoder.encode(dispatcherIdentity),
        )

        // then
        with(encodedAndDecodedDispatcherIdentity) {
            assertThat(dispatcherId).isEqualTo(TEST_DISPATCHER_ID)
            assertThat(dispatcherType).isEqualTo(TEST_DISPATCHER_TYPE)
        }
    }

    @Test
    fun `WHEN dispatcherId changed THEN byte representation of DispatcherIdentity changed`() {
        // given
        val dispatcherIdentity = DispatcherIdentity(
            dispatcherId = TEST_DISPATCHER_ID,
        )
        val anotherDispatcherIdentity = DispatcherIdentity(
            dispatcherId = TEST_DISPATCHER_ID + 1,
        )

        // when
        val dispatcherIdentityBytes = dispatcherIdentityEncoder.encode(dispatcherIdentity)
        val anotherDispatcherIdentityBytes = dispatcherIdentityEncoder.encode(anotherDispatcherIdentity)

        // then
        assertThat(dispatcherIdentityBytes).isNotEqualTo(anotherDispatcherIdentityBytes)
    }
}