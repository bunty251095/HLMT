package com.caressa.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caressa.model.entity.AppCacheMaster

@Dao
interface AppCacheMasterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApaCacheData(data: AppCacheMaster)

    @Query("SELECT * FROM AppCacheMaster WHERE mapKey=:key")
    fun getAppCacheData(key: String): AppCacheMaster
}