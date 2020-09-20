package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room

import androidx.room.*

@Entity(tableName = "consultations", indices = [Index("scheduleId", unique = true)])
data class Consultation(
    @ColumnInfo(name = "id")
    @Transient
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "scheduleId")
    var scheduleId: Int? = null,

    @ColumnInfo(name = "status")
    var status: Int? = null

)