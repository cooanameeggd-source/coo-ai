package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPasscodeValidation() {
        val masterCode = "3345687"
        val userEnteredCorrect = "3345687"
        val userEnteredWrong = "12345"

        assertTrue(userEnteredCorrect == masterCode)
        assertFalse(userEnteredWrong == masterCode)
    }

    @Test
    fun testQuestionAnsweringLogic() {
        val wormholeAnswer = "ทฤษฎีรูหนอน เกิดจากการที่ มีสิ่งของที่มีมวลติดลบ จึงทำให้เกิดการยุบตัวของเวลา  และความโค้งของอวกาศ จึงทำให้เกิดแรงผลักอย่างมาก"
        val q1 = "ทฤษฎีรูหนอนเป็นยังไง"
        val q2 = "เด็กที่ฉลาดที่สุด"

        fun mockAnswer(q: String): String {
            return when {
                q.contains("รูหนอน") -> wormholeAnswer
                q.contains("เด็กที่ฉลาดที่สุด") -> "อากิ้น"
                q.contains("อากิ้น") -> "อากิ้นฉลาดที่สุด"
                else -> "ไม่พบข้อมูล"
            }
        }

        // Test non-sequential calling: Q2 before Q1
        assertEquals("อากิ้น", mockAnswer(q2))
        assertEquals(wormholeAnswer, mockAnswer(q1))
        assertEquals("อากิ้นฉลาดที่สุด", mockAnswer("อากิ้นเก่งไหม"))
        assertEquals("ไม่พบข้อมูล", mockAnswer("สภาพอากาศวันนี้"))
    }
}
