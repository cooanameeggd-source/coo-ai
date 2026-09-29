package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_items ORDER BY id ASC")
    fun getAllKnowledgeFlow(): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_items ORDER BY id ASC")
    suspend fun getAllKnowledge(): List<KnowledgeEntity>

    @Query("SELECT * FROM knowledge_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): KnowledgeEntity?

    @Query("SELECT * FROM knowledge_items WHERE LOWER(question) LIKE '%' || LOWER(:query) || '%' OR LOWER(keywords) LIKE '%' || LOWER(:query) || '%'")
    fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KnowledgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<KnowledgeEntity>)

    @Update
    suspend fun update(item: KnowledgeEntity)

    @Delete
    suspend fun delete(item: KnowledgeEntity)

    @Query("DELETE FROM knowledge_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM knowledge_items")
    suspend fun getCount(): Int
}
