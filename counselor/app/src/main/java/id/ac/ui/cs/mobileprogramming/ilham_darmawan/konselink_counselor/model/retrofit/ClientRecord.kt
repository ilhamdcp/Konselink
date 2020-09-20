package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class ClientRecord(
    @SerializedName("scheduleId")
    var scheduleId: Int? = null,

    @SerializedName("diagnosis")
    var diagnosis: String? = null,

    @SerializedName("problemDescription")
    var problemDescription: String? = null,

    @SerializedName("diagnosisCode")
    var diagnosisCode: Int? = null,

    @SerializedName("physicalHealthHistory")
    var physicalHealthHistory: String? = null,

    @SerializedName("medicalConsumption")
    var medicalConsumption: String? = null,

    @SerializedName("suicideRisk")
    var suicideRisk: String? = null,

    @SerializedName("selfHarmRisk")
    var selfHarmRisk: String? = null,

    @SerializedName("othersHarmRisk")
    var othersHarmRisk: String? = null,

    @SerializedName("assessment")
    var assessment: String? = null,

    @SerializedName("consultationPurpose")
    var consultationPurpose: String? = null,

    @SerializedName("treatmentPlan")
    var treatmentPlan: String? = null,

    @SerializedName("meetings")
    var meetings: Int? = null,

    @SerializedName("notes")
    var notes: String? = null,

    @SerializedName("counselorName")
    var counselorName: String? = null,

    @SerializedName("counselorId")
    var counselorId: Int? = null,

    @SerializedName("icd9Code")
    var icd9: String? = null,

    @SerializedName("icd10Code")
    var icd10: String? = null
)