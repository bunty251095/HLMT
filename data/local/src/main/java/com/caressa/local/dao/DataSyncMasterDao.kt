package com.caressa.local.dao

import androidx.room.*
import com.caressa.model.entity.DataSyncMaster

@Dao
interface DataSyncMasterDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApiSyncData( data : DataSyncMaster)

    @Query("SELECT * FROM DataSyncMaster WHERE apiName=:apiName AND personId=:personId")
    fun getLastSyncDate(apiName : String, personId: String): DataSyncMaster

    @Query("SELECT * FROM DataSyncMaster WHERE personId=:personId OR personId = '007'")
    fun getLastSyncDataList(personId: String): List<DataSyncMaster>

    @Update
    fun updateRecord(data : DataSyncMaster)

    @Query("DELETE FROM DataSyncMaster")
    abstract suspend fun deleteAllRecords()

}