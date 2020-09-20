package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant

enum class ConsultationStatus(val code: Int, description: String) {
    FILL_SURVEY(1, "Currently filling survey"),
    CHAT(2, "Currently chatting"),
    FILL_RECORD(3, "Currently fill record"),
    END(4, "Ended")
}