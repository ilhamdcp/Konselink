package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant

enum class ClientRecordFormType (val typeName: String) {
    DIAGNOSIS("diagnosis"),
    DIAGNOSIS_CODE("diagnosisCode"),
    PHYSICAL_HEALTH_HISTORY("physicalHealthHistory"),
    MEDICAL_CONSUMPTION("medicalConsumption"),
    ASSESSMENT("assessment"),
    CONSULTATION_PURPOSE("consultationPurpose"),
    TREATMENT_PLAN("treatmentPlan"),
    MEETINGS("meetings"),
    NOTES("notes"),
    SELF_HARM_RISK("selfHarmRisk"),
    SUICIDE_RISK("suicideRisk"),
    OTHERS_HARM_RISK("othersHarmRisk"),
    PROBLEM_DESCRIPTION("problemDescription")
}