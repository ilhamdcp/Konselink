package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(registration: Registration): Long

    @Query("SELECT * FROM registrations ORDER BY id DESC")
    fun getAllRegistrations(): LiveData<List<Registration>>

    @Query("SELECT * FROM registrations ORDER BY id DESC LIMIT 1")
    fun getCurrentRegistration(): LiveData<Registration>
}
