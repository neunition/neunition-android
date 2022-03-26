package ca.neunition.data.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TotalDaily(
    @Json(name = "CA")
    val cA: ca.neunition.data.model.api.CA?,
    @Json(name = "CHOCDF")
    val cHOCDF: ca.neunition.data.model.api.CHOCDF?,
    @Json(name = "CHOLE")
    val cHOLE: ca.neunition.data.model.api.CHOLE?,
    @Json(name = "ENERC_KCAL")
    val eNERCKCAL: ca.neunition.data.model.api.ENERCKCAL?,
    @Json(name = "FASAT")
    val fASAT: ca.neunition.data.model.api.FASAT?,
    @Json(name = "FAT")
    val fAT: ca.neunition.data.model.api.FAT?,
    @Json(name = "FE")
    val fE: ca.neunition.data.model.api.FE?,
    @Json(name = "FIBTG")
    val fIBTG: ca.neunition.data.model.api.FIBTG?,
    @Json(name = "FOLDFE")
    val fOLDFE: ca.neunition.data.model.api.FOLDFE?,
    @Json(name = "K")
    val k: ca.neunition.data.model.api.K?,
    @Json(name = "MG")
    val mG: ca.neunition.data.model.api.MG?,
    @Json(name = "NA")
    val nA: ca.neunition.data.model.api.NA?,
    @Json(name = "NIA")
    val nIA: ca.neunition.data.model.api.NIA?,
    @Json(name = "P")
    val p: ca.neunition.data.model.api.P?,
    @Json(name = "PROCNT")
    val pROCNT: ca.neunition.data.model.api.PROCNT?,
    @Json(name = "RIBF")
    val rIBF: ca.neunition.data.model.api.RIBF?,
    @Json(name = "THIA")
    val tHIA: ca.neunition.data.model.api.THIA?,
    @Json(name = "TOCPHA")
    val tOCPHA: ca.neunition.data.model.api.TOCPHA?,
    @Json(name = "VITA_RAE")
    val vITARAE: ca.neunition.data.model.api.VITARAE?,
    @Json(name = "VITB12")
    val vITB12: ca.neunition.data.model.api.VITB12?,
    @Json(name = "VITB6A")
    val vITB6A: ca.neunition.data.model.api.VITB6A?,
    @Json(name = "VITC")
    val vITC: ca.neunition.data.model.api.VITC?,
    @Json(name = "VITD")
    val vITD: ca.neunition.data.model.api.VITD?,
    @Json(name = "VITK1")
    val vITK1: ca.neunition.data.model.api.VITK1?,
    @Json(name = "ZN")
    val zN: ca.neunition.data.model.api.ZN?
)
