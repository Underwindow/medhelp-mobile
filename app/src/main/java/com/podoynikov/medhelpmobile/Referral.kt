package com.podoynikov.medhelpmobile

import java.util.*
import com.podoynikov.medhelpmobile.R.color

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
        val statusId: Int)
{
    var analyses = mutableListOf<Analysis>()
    val status          = statuses[statusId].first
    val statusTextColor = statuses[statusId].second
    companion object {
        val statuses = arrayOf(
                Pair("Ожидаются мед.обследования", color.orange),
                Pair("На проверке", color.orange),
                Pair("Приём одобрен", color.green),
                Pair("Приём отменён", color.red)
        )
    }
}