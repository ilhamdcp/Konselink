package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant

enum class Specialization (val specializationName: String) {
    CLINICAL("Psikologi Klinis"),
    ORGANIZATION("Psikologi Industri & Organisasi"),
    EDUCATION("Psikologi Pendidikan");

    override fun toString(): String{
        return specializationName
    }
}