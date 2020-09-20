package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var userId: Int? = null,
    var academicId: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var role: String? = null,
    var isVerified: Boolean? = false,
    var isRegistered: Boolean? = false


) {
    override fun toString(): String {
        return "User(id=$id, userId=$userId, academicId=$academicId, fullname=$fullname, username=$username, role=$role, isVerified=$isVerified, isRegistered=$isRegistered)"
    }
}