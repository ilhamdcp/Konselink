package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant

enum class RegistrationError(val errName: String) {
    GENDER_NOT_CHOSEN("Gender belum dipilih"),
    SHOULD_CONTAIN_DIGIT_ONLY("Data hanya boleh berisi angka"),
    INVALID_DATE("Tanggal tidak valid"),
    INVALID_MONTH("Bulan tidak valid"),
    INVALID_YEAR("Tahun tidak valid"),
    SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR("Data tidak boleh berisi angka / karakter spesial"),
    SHOULD_NOT_EMPTY("Data tidak boleh kosong"),
    ADDRESS_SHOULD_NOT_LESS_THAN_TEN_CHAR("Alamat tidak boleh kurang dari 10 karakter"),
    PHONE_NUMBER_SHOULD_NOT_LESS_THAN_NINE_CHAR("Nomor HP tidak boleh kurang dari 9 karakter"),
    PHONE_NUMBER_SHOULD_CONTAIN_DIGIT_ONLY("Nomor HP hanya boleh berisi angka"),
    RELIGION_NOT_SELECTED("Agama belum dipilih"),
    CURRENT_EDUCATION_NOT_SELECTED("Pendidikan terakhir / saat ini belum dipilih")
}