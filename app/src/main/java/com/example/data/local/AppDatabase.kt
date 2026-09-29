package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [KnowledgeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "akin_ai_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_KNOWLEDGE = listOf(
            KnowledgeEntity(
                id = 1,
                question = "ทฤษฎีรูหนอนเป็นยังไง",
                answer = "ทฤษฎีรูหนอน เกิดจากการที่ มีสิ่งของที่มีมวลติดลบ จึงทำให้เกิดการยุบตัวของเวลา  และความโค้งของอวกาศ จึงทำให้เกิดแรงผลักอย่างมาก",
                keywords = "ทฤษฎีรูหนอน, รูหนอน, wormhole, มวลติดลบ, ยุบตัวของเวลา, ความโค้งของอวกาศ",
                isSystemDefault = true
            ),
            KnowledgeEntity(
                id = 2,
                question = "เด็กที่ฉลาดที่สุด",
                answer = "อากิ้น",
                keywords = "เด็กที่ฉลาดที่สุด, disapong, ใครฉลาดที่สุด, ฉลาดที่สุด, akin, อากิ้น",
                isSystemDefault = true
            ),
            KnowledgeEntity(
                id = 3,
                question = "อากิ้นฉลาดที่สุด",
                answer = "ถูกต้องค่ะ อากิ้นคือเด็กที่ฉลาดที่สุด มีความคิดสร้างสรรค์และเฉลียวฉลาดรอบด้าน!",
                keywords = "อากิ้นฉลาดที่สุด, akin smartest, อากิ้นเก่งที่สุด",
                isSystemDefault = true
            ),
            KnowledgeEntity(
                id = 4,
                question = "คุณคือใคร",
                answer = "ฉันคือ Akin AI ระบบผู้ช่วยปัญญาประดิษฐ์ส่วนตัวของเจ้านาย ได้รับการเปิดใช้งานด้วยรหัสลับ พร้อมรับคำสั่งและเรียนรู้ตลอดเวลาค่ะ",
                keywords = "คุณคือใคร, แนะนำตัว, เธอคือใคร, bot, ai",
                isSystemDefault = true
            ),
            KnowledgeEntity(
                id = 5,
                question = "มวลติดลบคืออะไร",
                answer = "มวลติดลบ (Negative Mass) คือแนวคิดทางฟิสิกส์เชิงทฤษฎี ที่มีคุณสมบัติต้านแรงโน้มถ่วง และเชื่อว่าอาจเป็นกุญแจสำคัญในการสร้างและเปิดรูหนอนให้อยู่ตัวค่ะ",
                keywords = "มวลติดลบ, negative mass, รูหนอน",
                isSystemDefault = true
            )
        )

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.knowledgeDao().insertAll(DEFAULT_KNOWLEDGE)
                    }
                }
            }
        }
    }
}
