package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant

enum class SymptomVisibility(val level: String) {
    NONE("Tidak ada"),
    IN_MIND("Ada pemikiran"),
    HAS_TRIED("Pernah mencoba"),
    WILL_DO("Akan melakukan");

    override fun toString(): String{
        return level
    }


}