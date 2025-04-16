package tech.ecom.egts.library.encoder.sfrd.record.subrecord

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordType
import tech.ecom.egts.library.model.sfrd.record.subrecord.types.AnalogSensorData
import tech.ecom.egts.library.utils.readByte
import tech.ecom.egts.library.utils.readThreeBytesToPositiveInt
import tech.ecom.egts.library.utils.toThreeByteLittleEndianByteArray

class AnalogSensorDataEncoder : AbstractSubRecordEncoder<AnalogSensorData>(
    subRecordTypeId = SubRecordType.EGTS_SR_ABS_AN_SENS_DATA.id,
    fieldName = SubRecordType.EGTS_SR_ABS_AN_SENS_DATA.fieldName,
) {

    override fun performEncode(egtsEntity: AnalogSensorData): ByteArray =
        ByteArrayOutputStream().apply {
            with(egtsEntity) {
                write(byteArrayOf(analogSensorNumber))
                write(analogSensorValue.toThreeByteLittleEndianByteArray())
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): AnalogSensorData {
        ByteArrayInputStream(byteArray).apply {
            val analogSensorNumber = readByte().also {
                logger.trace("AnalogSensor sensorNumber byteValue is {}", it)
            }

            val analogSensorValue = readThreeBytesToPositiveInt().also {
                logger.trace("AnalogSensor value is {}", it)
            }

            return AnalogSensorData(
                analogSensorNumber = analogSensorNumber,
                analogSensorValue = analogSensorValue,
            )
        }
    }
}