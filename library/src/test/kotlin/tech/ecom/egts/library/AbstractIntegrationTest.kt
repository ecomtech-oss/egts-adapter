package tech.ecom.egts.library

import tech.ecom.egts.library.encoder.EgtsPacketEncoder
import tech.ecom.egts.library.encoder.sfrd.AppServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ResponseServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ServiceDataRecordsEncoder
import tech.ecom.egts.library.encoder.sfrd.record.RecordDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AnalogSensorDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AuthResultEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.DispatcherIdentityEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.ExternalDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.PosSubRecordDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.SubRecordResponseEncoder

abstract class AbstractIntegrationTest {
    protected val analogSensorDataEncoder = AnalogSensorDataEncoder()
    protected val authResultEncoder = AuthResultEncoder()
    protected val dispatcherIdentityEncoder = DispatcherIdentityEncoder()
    protected val externalDataEncoder = ExternalDataEncoder()
    protected val posSubRecordDataEncoder = PosSubRecordDataEncoder()
    protected val subRecordResponseEncoder = SubRecordResponseEncoder()

    protected val egtsPacketEncoder: EgtsPacketEncoder

    init {
        val servicesFrameDataEncoder = ServiceDataRecordsEncoder(
            RecordDataEncoder(
                subRecordEncoders = listOf(
                    analogSensorDataEncoder,
                    authResultEncoder,
                    dispatcherIdentityEncoder,
                    externalDataEncoder,
                    posSubRecordDataEncoder,
                    subRecordResponseEncoder,
                ),
            ),
        )
        egtsPacketEncoder = EgtsPacketEncoder(
            AppServicesFrameDataEncoder(servicesFrameDataEncoder),
            ResponseServicesFrameDataEncoder(servicesFrameDataEncoder),
        )
    }
}