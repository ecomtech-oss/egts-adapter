package tech.ecom.egts.library.utils

import java.io.ByteArrayInputStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BinaryHelperTest {
    private val sampleHexString = "x01x00x00x0bx00x0dx00x01x00x01x91x06x00x00x00x48x01x01x05x03x00x00xbax0bx8dxc3"

    @Test
    fun `WHEN hex String converted to byte array and back THEN result String matches original one`() {
        assertThat(sampleHexString).isEqualTo(sampleHexString.hexStringToByteArray().toHexString())
    }

    @Test
    fun `WHEN proper range int converted to tree bytes little endian byte array and back THEN value preserved`() {
        // given
        val testInt = 123456

        // when
        val byteArray = testInt.toThreeByteLittleEndianByteArray()

        // then
        assertThat(ByteArrayInputStream(byteArray).readThreeBytesToPositiveInt()).isEqualTo(testInt)
    }

    @Test
    fun `WHEN out of proper range int converted to little endian byte array THEN exception is thrown`() {
        // given
        val negativeInt = -1
        val maxTreeByteInt = 16777215

        // then
        assertThrows<RuntimeException> { negativeInt.toThreeByteLittleEndianByteArray() }
        assertThrows<RuntimeException> { (maxTreeByteInt + 1).toThreeByteLittleEndianByteArray() }
    }

    @Test
    fun `WHEN less then tree bytes available for reading tree bytes int from bytearray THEN exception is thrown`() {
        // given
        val testInt = 123456

        // when
        val byteArray = testInt.toThreeByteLittleEndianByteArray().copyOf(2)

        // then
        assertThrows<RuntimeException> { ByteArrayInputStream(byteArray).readThreeBytesToPositiveInt() }
    }

    @Test
    fun `WHEN less then four bytes available for reading int from bytearray THEN exception is thrown`() {
        // given
        val testInt = 123456

        // when
        val byteArray = testInt.toLittleEndianByteArray().copyOf(3)

        // then
        assertThrows<RuntimeException> { ByteArrayInputStream(byteArray).readInt() }
    }

    @Test
    fun `WHEN less then two bytes available for reading short from bytearray THEN exception is thrown`() {
        // given
        val testInt = 123456

        // when
        val byteArray = testInt.toLittleEndianByteArray().copyOf(1)

        // then
        assertThrows<RuntimeException> { ByteArrayInputStream(byteArray).readShort() }
    }
}