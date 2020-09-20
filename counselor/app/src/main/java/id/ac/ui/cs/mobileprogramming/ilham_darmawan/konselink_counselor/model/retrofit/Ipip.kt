package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class Ipip(
    @SerializedName("Extraversion")
    val extraversion: Int? = null,

    @SerializedName("Agreeableness")
    val agreeableness: Int? = null,

    @SerializedName("Conscientiousness")
    val conscientiousness: Int? = null,

    @SerializedName("Neuroticism")
    val neuroticism: Int? = null,

    @SerializedName("Openness/Intellect/Imagination")
    val opennessOrIntellectOrImagination: Int? = null
)