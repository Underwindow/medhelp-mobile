package com.podoynikov.medhelpmobile.services

import android.net.Uri
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.podoynikov.medhelpmobile.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.lang.Exception


object ApiService {
    object Urls  {
        private val host: String = "http://192.168.3.6:8080"
        val auth = host + "/auth"
        val registration = host + "/registration"
        val referrals = host + "/directions"

        fun getAnalysisByReferralId(id : Int) : String {
            return host + "/direction/${id}/analysis"
        }

        fun uploadAnalysisById(id : Int) : String {
            return host + "/analysis/${id}/upload"
        }
    }

    fun getResponseStatus(response : JSONObject) : ResponseStatus {
        return when (response.getJSONObject("status").getString("status")){
            "info"  -> ResponseStatus.INFO
            "ok"    -> ResponseStatus.OK
            "error" -> ResponseStatus.ERROR
            else -> throw Exception("No such response status")
        }
    }

    suspend fun postRequestAsync(url: String, headers: Headers = Headers(), parameters: Parameters?=null, body : String): ResponseResultOf<String> {
        return withContext(Dispatchers.IO) {
            return@withContext httpPostApi(url, headers, parameters, body)
        }
    }
    suspend fun getRequestAsync(url: String, headers: Headers = Headers(), parameters: Parameters?=null): ResponseResultOf<String> {
        return withContext(Dispatchers.IO) {
            return@withContext httpGetApi(url, headers, parameters)
        }
    }
    suspend fun uploadRequestAsync(url: String, uri: Uri, headers: Headers): ResponseResultOf<String> {
        return withContext(Dispatchers.IO) {
            return@withContext httpUploadApi(url, uri, headers)
        }
    }

    fun httpPostApi(url: String, headers: Headers, parameters: Parameters?=null, body : String): ResponseResultOf<String> {
        if (!headers.keys.contains("Accept")) {
            //в API запросах обязательно дожен быть заполнен формат заппрашиваемых данных
            headers.append("Accept", "application/json")
        }

        return Fuel
            .post(url, parameters)
            .body(body)
            .header(headers)
                .also { println(it) }
            .responseString()
    }

    fun httpUploadApi(url: String, uri: Uri, headers: Headers): ResponseResultOf<String> {

        if (!headers.keys.contains("Accept")) {
            //в API запросах обязательно дожен быть заполнен формат заппрашиваемых данных
            headers.append("Accept", "application/json")
        }

        val fileToUpload = File(uri.path!!)
//        val tmpFile = File.createTempFile("temp", ".pdf")

        return Fuel.upload(url)
            .add(FileDataPart(fileToUpload, name = "file"))
            .requestProgress { readBytes, totalBytes ->
                val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                println("Bytes uploaded $readBytes / $totalBytes ($progress %)")
            }
            .header(headers)
                .also { println(it) }
            .responseString()
                .also { println(it) }

    }

    fun httpGetApi(url: String, headers: Headers, parameters: Parameters?=null): ResponseResultOf<String> {
        if (!headers.keys.contains("Accept")) {
            //в API запросах обязательно дожен быть заполнен формат заппрашиваемых данных
            headers.append("Accept", "application/json")
        }

        return Fuel
            .get(url, parameters)
            .header(headers)
                .also { print(it) }
            .responseString()
                .also { print(it) }
    }
}


