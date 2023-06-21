package com.kamrulhasan.crickinfo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kamrulhasan.crickinfo.model.country.CountryData
import com.kamrulhasan.crickinfo.model.custom.CustomPlayer
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.model.fixture.Run
import com.kamrulhasan.crickinfo.model.leagues.LeaguesData
import com.kamrulhasan.crickinfo.model.officials.OfficialsData
import com.kamrulhasan.crickinfo.model.team.TeamsData
import com.kamrulhasan.crickinfo.model.venues.VenuesData

@Database(
    entities = [TeamsData::class, FixturesData::class, Run::class, LeaguesData::class, OfficialsData::class, CountryData::class, VenuesData::class, CustomPlayer::class],
    version = 11,
    exportSchema = false
)

abstract class CrickInfoDatabase : RoomDatabase() {

    abstract fun cricketDao(): CricketDao

    companion object {
        @Volatile
        private var INSTANCE: CrickInfoDatabase? = null

        fun getDatabase(context: Context): CrickInfoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, CrickInfoDatabase::class.java, "crick_info_database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                return instance
            }
        }
    }
}