package com.podoynikov.medhelpmobile.extensions

import com.podoynikov.medhelpmobile.Analysis
import com.podoynikov.medhelpmobile.Referral
import org.json.JSONArray
import org.json.JSONObject
import java.net.CacheResponse
import java.text.SimpleDateFormat

operator fun JSONArray.iterator(): Iterator<JSONObject>
        = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

fun parseReferralJSON(referralJSON : JSONObject, dateFormatter : SimpleDateFormat) : Referral {
    return Referral(
            id                  = referralJSON.getInt("id"),
            firstName           = referralJSON.getString("patientFirstName"),
            lastName            = referralJSON.getString("patientLastName"),
            policyNumber        = referralJSON.getInt("patientPolicyNumber"),
            phoneNumber         = referralJSON.getString("patientTel"),
            doctorName          = referralJSON.getString("doctorName"),
            doctorSpecialty     = referralJSON.getString("doctorSpecialty"),
            date                = dateFormatter.parse(referralJSON.getString("date")),
            icdCode             = referralJSON.getString("icdCode"),
            medicalOrganization = referralJSON.getString("medicalOrganization"),
            statusId            = referralJSON.getInt("status"))
}

fun parseReferralAnalysesJSON(analysesJSON : JSONObject) : Analysis {
    return Analysis(
            id                  = analysesJSON.getInt("id"),
            name                = analysesJSON.getString("name"),
            isChecked           = analysesJSON.getBoolean("isChecked"),
            file_id             = if (JSONObject.NULL == (analysesJSON.get("file_id"))) null else analysesJSON.getInt("file_id"))
}