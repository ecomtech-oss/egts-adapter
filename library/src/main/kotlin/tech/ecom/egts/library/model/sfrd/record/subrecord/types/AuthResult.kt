package tech.ecom.egts.library.model.sfrd.record.subrecord.types

import tech.ecom.egts.library.constants.EgtsConstants.Companion.RST_OK
import tech.ecom.egts.library.model.sfrd.record.subrecord.SubRecordData

data class AuthResult(
    val resultCode: Byte = RST_OK,
) : SubRecordData()