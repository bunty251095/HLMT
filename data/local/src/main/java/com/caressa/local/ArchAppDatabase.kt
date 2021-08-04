package com.caressa.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.caressa.common.constants.Constants
import com.caressa.local.converter.Converters
import com.caressa.local.dao.*
import com.caressa.model.entity.*

@Database(
    entities = [Users::class, AppVersion::class, HRAQuestions::class, HRAVitalDetails::class, HRALabDetails::class, HRASummary::class,
        HealthDocument::class, DocumentType::class, DataSyncMaster::class, RecordInSession::class, UserRelatives::class, MedicationEntity.Medication::class,
        FitnessEntity.StepGoalHistory::class, TrackParameterMaster.Parameter::class, TrackParameterMaster.TrackParameterRanges::class,
        TrackParameterMaster.History::class, TrackParameters::class, AppCacheMaster::class],
    version = 3,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class ArchAppDatabase : RoomDatabase() {

    // DAO
    abstract fun vivantUserDao(): VivantUserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun fitnessDao(): FitnessDao
    abstract fun hraDao(): HRADao
    abstract fun shrDao(): StoreRecordsDao
    abstract fun dataSyncMasterDao(): DataSyncMasterDao
    abstract fun trackParameterDao(): TrackParameterDao
    abstract fun appCacheMasterDao(): AppCacheMasterDao


    companion object {

        val DB_Name = Constants.MAIN_DATABASE_NAME

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, ArchAppDatabase::class.java, DB_Name)
                .fallbackToDestructiveMigration()
                .build()
    }
}