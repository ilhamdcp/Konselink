package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client

import android.content.Context
import androidx.room.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.*

@Database(entities = [User::class, Registration::class, Consultation::class], version = 2)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun consultationDao(): ConsultationDao

    companion object {
        private var INSTANCE: ApplicationDatabase? = null

        fun getInstance(context: Context): ApplicationDatabase? {
            if (INSTANCE == null) {
                synchronized(RoomDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ApplicationDatabase::class.java, "user.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}