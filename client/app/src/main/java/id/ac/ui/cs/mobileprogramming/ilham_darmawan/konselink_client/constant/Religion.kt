package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant

enum class Religion(val religionName: String) {
    ISLAM("Islam"),
    KATOLIK("Katolik"),
    PROTESTAN("Protestan"),
    HINDU("Hindu"),
    BUDDHA("Buddha"),
    KONGHUCHU("Konghuchu"),
    OTHER("Lain-lain");

    override fun toString(): String{
        return religionName
    }


}