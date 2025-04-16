package tech.ecom.egts.library.encoder

import java.io.IOException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.ecom.egts.library.exception.EgtsAdapterException
import tech.ecom.egts.library.exception.EgtsExceptionErrorCode
import tech.ecom.egts.library.utils.toHexString

abstract class AbstractEgtsEncoder<T>(
    private val fieldName: String,
) {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun encode(egtsEntity: T): ByteArray {
        var byteArray: ByteArray? = null
        try {
            byteArray = performEncode(egtsEntity)
        } catch (e: IOException) {
            handleEncodeException(e)
        }
        return byteArray!!.also { logResult(it) }
    }

    fun decode(byteArray: ByteArray): T {
        var egtsEntity: T? = null
        try {
            egtsEntity = performDecode(byteArray)
        } catch (e: IOException) {
            handleDecodeException(e)
        } catch (e: ArrayIndexOutOfBoundsException) {
            handleDecodeException(e)
        }
        return egtsEntity!!
    }

    protected abstract fun performEncode(egtsEntity: T): ByteArray
    protected abstract fun performDecode(byteArray: ByteArray): T

    protected fun logResult(byteArray: ByteArray) = logger.info(
        "{} field hex representation - {}",
        fieldName,
        byteArray.toHexString(),
    )

    protected fun handleEncodeException(e: IOException) {
        val errorMessage = "caught IO exception while encoding $fieldName"
        logger.warn(errorMessage, e)
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.EGTS_ENCODE_EXCEPTION,
            errorMessage = errorMessage,
        )
    }

    protected fun handleDecodeException(e: Exception) {
        val errorMessage = "caught ${e.javaClass.simpleName} exception while decoding $fieldName"
        logger.warn(errorMessage, e)
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.EGTS_DECODE_EXCEPTION,
            errorMessage = errorMessage,
        )
    }
}