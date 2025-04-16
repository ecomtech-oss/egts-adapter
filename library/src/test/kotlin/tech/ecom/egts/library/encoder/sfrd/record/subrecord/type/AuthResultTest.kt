package tech.ecom.egts.library.encoder.sfrd.record.subrecord.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.ecom.egts.library.AbstractIntegrationTest
import tech.ecom.egts.library.constants.EgtsConstants.Companion.RST_OK
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.AuthResult

class AuthResultTest : AbstractIntegrationTest() {

    @Test
    fun `WHEN AuthResult encoded with some resultCode THEN it preserved after decoding`() {
        // given
        val authResult = AuthResult(
            resultCode = RST_OK,
        )

        // when
        val encodedAndDecodedAuthResult = authResultEncoder.decode(authResultEncoder.encode(authResult))

        // then
        assertThat(encodedAndDecodedAuthResult.resultCode).isEqualTo(RST_OK)
    }

    @Test
    fun `WHEN resultCode changed THEN byte representation of AuthResult changed`() {
        // given
        val authResult = AuthResult(
            resultCode = RST_OK,
        )
        val authResultWithAnotherCode = AuthResult(
            resultCode = (authResult.resultCode + 1).toByte(),
        )

        // when
        val authResultBytes = authResultEncoder.encode(authResult)
        val authResultWithAnotherCodeBytes = authResultEncoder.encode(authResultWithAnotherCode)

        // then
        assertThat(authResultBytes).isNotEqualTo(authResultWithAnotherCodeBytes)
    }
}