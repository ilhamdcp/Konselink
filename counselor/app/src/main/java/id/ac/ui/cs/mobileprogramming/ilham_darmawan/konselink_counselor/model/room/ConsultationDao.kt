package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


// TODO create tests
@Dao
interface ConsultationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateConsultation(consultation: Consultation)

    @Query("SELECT * FROM consultations WHERE scheduleId = :scheduleId ORDER BY id DESC LIMIT 1")
    fun getConsultation(scheduleId: Int): LiveData<Consultation>
}