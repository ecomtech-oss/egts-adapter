package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import java.sql.Timestamp
import java.time.Instant.now
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class PosSubRecordData(
    val navigationTime: Timestamp = Timestamp.from(now()),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    val isAltitudePresent: Boolean = false,

    val isMoving: Boolean = true,
    val isSentFromBlackBox: Boolean = false,
    val coordinateSystem: Boolean = false, // 0 - WGS-84, 1 - ПЗ-90.0
    val fix: Boolean = false, // 0 - 2D, 1 - 3D
    val isValid: Boolean = true,

    val altitudeSign: Int = 0, // altitude sign

    val direction: Int = 0,
    val odometer: Double = 0.0,
    val digitalInputs: String = "00000000",
    val source: String = "00000000",

    val speed: Double = 0.0,
) : SubRecordData()