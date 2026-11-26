package com.cortinadev.snoopy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cortinadev.snoopy.data.local.entity.CoverEntity

@Dao
interface CoverDao {

    @Query("SELECT * FROM covers WHERE lookupKey = :key")
    suspend fun get(key: String): CoverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: CoverEntity)
}
