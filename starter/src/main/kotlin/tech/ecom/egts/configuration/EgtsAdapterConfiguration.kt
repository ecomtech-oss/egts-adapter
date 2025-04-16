package tech.ecom.egts.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tech.ecom.egts.library.encoder.EgtsPacketEncoder
import tech.ecom.egts.library.encoder.sfrd.AppServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ResponseServicesFrameDataEncoder
import tech.ecom.egts.library.encoder.sfrd.ServiceDataRecordsEncoder
import tech.ecom.egts.library.encoder.sfrd.record.RecordDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AbstractSubRecordEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AnalogSensorDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.AuthResultEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.DispatcherIdentityEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.ExternalDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.PosSubRecordDataEncoder
import tech.ecom.egts.library.encoder.sfrd.record.subrecord.SubRecordResponseEncoder
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

@Configuration
open class EgtsAdapterConfiguration {

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun egtsPacketEncoder(
        appServicesFrameDataEncoder: AppServicesFrameDataEncoder,
        responseServicesFrameDataEncoder: ResponseServicesFrameDataEncoder,
    ): EgtsPacketEncoder {
        return EgtsPacketEncoder(appServicesFrameDataEncoder, responseServicesFrameDataEncoder)
    }

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun appServicesFrameDataEncoder(
        serviceDataRecordsEncoder: ServiceDataRecordsEncoder,
    ): AppServicesFrameDataEncoder {
        return AppServicesFrameDataEncoder(serviceDataRecordsEncoder)
    }

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun responseServicesFrameDataEncoder(
        serviceDataRecordsEncoder: ServiceDataRecordsEncoder,
    ): ResponseServicesFrameDataEncoder {
        return ResponseServicesFrameDataEncoder(serviceDataRecordsEncoder)
    }

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun serviceDataRecordsEncoder(
        recordDataEncoder: RecordDataEncoder,
    ): ServiceDataRecordsEncoder {
        return ServiceDataRecordsEncoder(recordDataEncoder)
    }

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun recordDataEncoder(
        subRecordEncoders: List<AbstractSubRecordEncoder<out SubRecordData>>,
    ): RecordDataEncoder {
        return RecordDataEncoder(subRecordEncoders)
    }

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun analogSensorDataEncoder() = AnalogSensorDataEncoder()

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun authResultEncoder() = AuthResultEncoder()

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun dispatcherIdentityEncoder() = DispatcherIdentityEncoder()

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun posSubRecordDataEncoder() = PosSubRecordDataEncoder()

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun externalDataEncoder() = ExternalDataEncoder()

    @Bean
    @ConditionalOnProperty("egts.initialize-encoders")
    open fun subRecordResponseEncoder() = SubRecordResponseEncoder()
}