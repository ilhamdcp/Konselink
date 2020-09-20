package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class Registration(
    @ColumnInfo(name = "id") @Transient @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var fullname: String? = null,
    var specialization: String? = null,
    @ColumnInfo(name = "specialization_spinner_id") @Transient var specializationSpinnerId: Int? = null,
    var gender: String? = null,
    var strNumber: String? = null,
    @ColumnInfo(name = "str_photo_path") @Transient var strPhotoPath: String? = null,
    var sipNumber: String? = null,
    @ColumnInfo(name = "sip_photo_path") @Transient var sipPhotoPath: String? = null,
    var sspNumber: String? = null,
    @ColumnInfo(name = "ssp_photo_path") @Transient var sspPhotoPath: String? = null

) {
    override fun toString(): String {
        return "Registration(id=$id, fullName=$fullname, specialization=$specialization, gender=$gender, strNumber=$strNumber, sipNumber=$sipNumber, sspNumber=$sspNumber)"
    }
};