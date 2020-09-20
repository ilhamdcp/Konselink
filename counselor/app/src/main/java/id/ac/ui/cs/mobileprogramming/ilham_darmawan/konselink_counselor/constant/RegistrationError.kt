package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant

enum class RegistrationError(val errName: String) {
    GENDER_NOT_CHOSEN("Gender belum dipilih"),
    SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR("Data tidak boleh berisi angka / karakter spesial"),
    SHOULD_NOT_EMPTY("Data tidak boleh kosong"),
    CURRENT_EDUCATION_NOT_SELECTED("Pendidikan terakhir / saat ini belum dipilih")
}