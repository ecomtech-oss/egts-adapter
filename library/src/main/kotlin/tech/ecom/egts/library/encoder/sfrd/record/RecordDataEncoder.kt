package tech.ecom.egts.library.encoder.sfrd.record

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import tech.ecom.egts.library.encoder.AbstractEgtsEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AbstractSubRecordEncoder
import tech.ecom.egts.library.exception.EgtsAdapterException
import tech.ecom.egts.library.exception.EgtsExceptionErrorCode
import tech.ecom.egts.library.model.sfrd.record.RecordData
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecord
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData
import tech.ecom.egts.library.utils.readShort
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class RecordDataEncoder(
    subRecordEncoders: List<AbstractSubRecordEncoder<out SubRecordData>>,
) : AbstractEgtsEncoder<RecordData>("RECORD_DATA") {
    private val encodersBySubRecordTypeId = subRecordEncoders.associateBy { it.subRecordTypeId }

    override fun performEncode(egtsEntity: RecordData): ByteArray =
        ByteArrayOutputStream().apply {
            for (subRecord in egtsEntity.subRecordList) {
                write(subRecord.subRecordTypeId)

                subRecord.subRecordData?.let { subRecordData ->
                    val subRecordEncoder = pickAnEncoder(subRecord.subRecordTypeId)
                    val subRecordDataByteArray: ByteArray = subRecordEncoder.encode(
                        egtsEntity = subRecordData,
                    )

                    val subRecordLength = subRecordDataByteArray.size
                    write(subRecordLength.toShort().toLittleEndianByteArray())
                    write(subRecordDataByteArray)
                } ?: {
                    val subRecordLength = 0
                    write(subRecordLength.toShort().toLittleEndianByteArray())
                }
            }
        }.toByteArray()

    override fun performDecode(byteArray: ByteArray): RecordData {
        ByteArrayInputStream(byteArray).apply {
            val subRecordList = mutableListOf<SubRecord>()
            while (available() > 0) {
                val subRecordTypeId = read()
                val subRecordEncoder = pickAnEncoder(subRecordTypeId)

                val subRecordLength = readShort()
                if (subRecordLength > 0) {
                    val subRecordBytes = readNBytes(subRecordLength.toInt())

                    val subRecordData = subRecordEncoder.decode(subRecordBytes)
                    val subRecord = SubRecord(
                        subRecordTypeId = subRecordTypeId,
                        subRecordData = subRecordData,
                    )
                    subRecordList.add(subRecord)
                }
            }

            return RecordData(
                subRecordList = subRecordList,
            )
        }
    }

    private fun pickAnEncoder(subRecordTypeId: Int): AbstractSubRecordEncoder<SubRecordData> {
        val subRecordEncoder = encodersBySubRecordTypeId[subRecordTypeId] ?:
        throw EgtsAdapterException(
            code = EgtsExceptionErrorCode.EGTS_DECODE_EXCEPTION,
            errorMessage = "no subrecord type with id $subRecordTypeId implemented yes. To implement it: \n" +
                "1) start from SubRecordType enum (that's not mandatory step. it just helps to keep things organized) \n" +
                "2) add data class to 'model' package \n" +
                "3) implement appropriate encoder at 'encoder' package. Use subRecordTypeId and fieldName from 1) \n" +
                "4) add new encoder bean initialization to autoconfiguration \n" +
                "5) implement test at 'test' package. Use SubRecordType enum entry from 1) to reference subRecordTypeId \n" +
                "6) use new encoder at your app code. Use SubRecordType enum entry from 1) to reference subRecordTypeId \n" +
                "you're awesome!",
        )

        @Suppress("UNCHECKED_CAST")
        return subRecordEncoder as AbstractSubRecordEncoder<SubRecordData>
    }
}