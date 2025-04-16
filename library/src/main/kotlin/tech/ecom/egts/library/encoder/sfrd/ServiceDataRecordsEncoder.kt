package tech.ecom.egts.library.encoder.sfrd

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import tech.ecom.egts.library.encoder.AbstractEgtsEncoder
import tech.ecom.egts.library.encoder.sfrd.record.RecordDataEncoder
import tech.ecom.egts.library.model.sfrd.ServiceDataRecords
import tech.ecom.egts.library.model.sfrd.record.ServiceDataRecord
import tech.ecom.egts.library.model.sfrd.record.ServiceType
import tech.ecom.egts.library.utils.readByte
import tech.ecom.egts.library.utils.readShort
import tech.ecom.egts.library.utils.readUInt
import tech.ecom.egts.library.utils.toBoolean
import tech.ecom.egts.library.utils.toBitCharValue
import tech.ecom.egts.library.utils.toHexString
import tech.ecom.egts.library.utils.toLittleEndianByteArray

class ServiceDataRecordsEncoder(
    private val recordDataEncoder: RecordDataEncoder,
) : AbstractEgtsEncoder<ServiceDataRecords>("SERVICE_DATA_RECORDS") {

    override fun performEncode(egtsEntity: ServiceDataRecords): ByteArray =
        ByteArrayOutputStream().apply {
            egtsEntity.sdrList.forEach { serviceDataRecord ->
                with(serviceDataRecord) {
                    val recordData = recordDataEncoder.encode(recordData)

                    val recordLength = recordData.size
                    write(recordLength.toShort().toLittleEndianByteArray())
                    write(recordNumber.toLittleEndianByteArray())

                    write(buildFlagBits())

                    objectIdentifier?.toLittleEndianByteArray()?.let(::write)
                    eventIdentifier?.toLittleEndianByteArray()?.let(::write)
                    time?.toLittleEndianByteArray()?.let(::write)

                    write(sourceServiceType.byteValue.toInt())
                    write(recipientServiceType.byteValue.toInt())
                    write(recordData)
                }
            }
        }.toByteArray()

    private fun ServiceDataRecord.buildFlagBits(): Int {
        val flagBits = buildString {
            append(sourceServiceOnDevice.toBitCharValue())
            append(recipientServiceOnDevice.toBitCharValue())
            append(group.toBitCharValue())
            append(recordProcessingPriority)
            append((time != null).toBitCharValue())
            append((eventIdentifier != null).toBitCharValue())
            append((objectIdentifier != null).toBitCharValue())
        }.also {
            logger.trace("flag bits string is {}", it)
        }
        return Integer.parseInt(flagBits, 2)
    }

    override fun performDecode(byteArray: ByteArray): ServiceDataRecords {
        logger.debug("parsing ServiceDataRecords {}", byteArray.toHexString())
        ByteArrayInputStream(byteArray).apply {
            var recordCounter = 0
            val sdrList = mutableListOf<ServiceDataRecord>()

            while (available() > 0) {
                logger.trace("parsing {}-th service data record", ++recordCounter)

                val recordLength = readShort().toUShort()
                val recordNumber = readShort().toUShort().also {
                    logger.trace("its record number defined as {}", it)
                }

                val flags = read().toString(2).padStart(8, '0').also {
                    logger.trace("flags bit read as {}", it)
                }
                val sourceServiceOnDevice = flags[0].toBoolean()
                val recipientServiceOnDevice = flags[1].toBoolean()
                val group = flags[2].toBoolean()
                val recordProcessingPriority = flags.substring(3, 5)
                val timeFieldExists = flags[5].toBoolean()
                val eventIdFieldExists = flags[6].toBoolean()
                val objectIdFieldExists = flags[7].toBoolean()

                val objectIdentifier = if (objectIdFieldExists) {
                    readUInt().also { logger.trace("objectIdentifier defined as {}", it) }
                } else {
                    null
                }

                val eventIdentifier = if (eventIdFieldExists) {
                    readUInt().also { logger.trace("eventIdentifier defined as {}", it) }
                } else {
                    null
                }

                val time = if (timeFieldExists) {
                    readUInt().also { logger.trace("time defined as {}", it) }
                } else {
                    null
                }

                val sourceServiceType = ServiceType.fromByteValue(readByte()).also {
                    logger.trace("sourceServiceType defined as {}", it)
                }

                val recipientServiceType = ServiceType.fromByteValue(readByte()).also {
                    logger.trace("recipientServiceType defined as {}", it)
                }

                val recordDataBytes = ByteBuffer.wrap(readNBytes(recordLength.toInt()))
                    .order(ByteOrder.LITTLE_ENDIAN).array()
                val recordData = recordDataEncoder.decode(recordDataBytes)

                val serviceDataRecord = ServiceDataRecord(
                    recordNumber = recordNumber,
                    sourceServiceOnDevice = sourceServiceOnDevice,
                    recipientServiceOnDevice = recipientServiceOnDevice,
                    group = group,
                    recordProcessingPriority = recordProcessingPriority,
                    objectIdentifier = objectIdentifier,
                    eventIdentifier = eventIdentifier,
                    time = time,
                    sourceServiceType = sourceServiceType,
                    recipientServiceType = recipientServiceType,
                    recordData = recordData,
                )

                sdrList.add(serviceDataRecord)
            }

            return ServiceDataRecords(
                sdrList = sdrList,
            )
        }
    }
}