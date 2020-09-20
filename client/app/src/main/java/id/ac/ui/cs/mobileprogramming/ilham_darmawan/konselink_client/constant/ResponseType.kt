package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant

enum class ResponseType(val code: Int, val message: String) {
    SUCCESS(200, "Success"),
    INTERNAL_SERVER_ERROR(530, "Internal Server Error"),
    NOT_FOUND(404, "Not found"),
    FAILED_TO_CONNECT(463, "Failed to connect"),
    BAD_REQUEST(400, "Bad Request"),

}