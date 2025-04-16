package tech.ecom.egts.library.encoder.sfrd

import tech.ecom.egts.library.encoder.AbstractEgtsEncoder
import tech.ecom.egts.library.model.sfrd.AppServicesFrameData

class AppServicesFrameDataEncoder(
    private val serviceDataRecordsEncoder: ServiceDataRecordsEncoder,
) : AbstractEgtsEncoder<AppServicesFrameData>("APP_SERVICES_FRAME_DATA") {
    override fun performEncode(egtsEntity: AppServicesFrameData) =
        serviceDataRecordsEncoder.encode(egtsEntity.serviceDataRecords)

    override fun performDecode(byteArray: ByteArray) =
        AppServicesFrameData(
            serviceDataRecords = serviceDataRecordsEncoder.decode(byteArray),
        )
}