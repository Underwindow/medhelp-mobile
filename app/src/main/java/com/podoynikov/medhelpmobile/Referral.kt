package com.podoynikov.medhelpmobile

import java.util.*

class Referral (
        val id: Int,
        val firstName: String,
        val lastName: String,
        val policyNumber: Int,
        val phoneNumber: String,
        val doctorName: String,
        val doctorSpecialty: String,
        val date: Date,
        val icdCode: String,
        val medicalOrganization: String,
        val status: String){
    companion object {
        val statusesMap = mapOf(
                0 to "Ожидаются мед.обследования",
                1 to "На проверке",
                2 to "Одобрено",
                3 to "Отклонено"
        )
    }
}