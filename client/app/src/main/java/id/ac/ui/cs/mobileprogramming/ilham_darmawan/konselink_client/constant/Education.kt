package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant

enum class Education(val educationType: String, val level: Int) {
    KINDERGARTEN("TK", 1),
    ELEMENTARY("SD", 2),
    JUNIOR("SMP", 3),
    SENIOR("SMA/SMK", 4),
    DIPLOMA("DIPLOMA (D1, D2, D3, D4)", 5),
    BACHELOR("S1", 6),
    MASTER("S2", 7),
    DOCTORAL("S3", 8);

    override fun toString(): String {
        return educationType
    }
}