package com.podoynikov.medhelpmobile.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import kotlinx.coroutines.*
import org.json.JSONObject

object ApiService {
    object Urls  {
        val host: String = "http://192.168.30.7:8080"
        val auth = host + "/auth"
        val registration = host + "/registration"
        val referrals = host + "/directions"
    }

    suspend fun postRequestAsync(url: String, headers: Headers = Headers(), parameters: Parameters?=null, bodyJson : JSONObject): ResponseResultOf<String> {
        return withContext(Dispatchers.IO) {
            return@withContext httpPostApi(url, headers, parameters, bodyJson)
        }
    }
    suspend fun getRequestAsync(url: String, headers: Headers = Headers(), parameters: Parameters?=null): ResponseResultOf<String> {
        return withContext(Dispatchers.IO) {
            return@withContext httpGetApi(url, headers, parameters)
        }
    }

    fun httpPostApi(url: String, headers: Headers, parameters: Parameters?=null, bodyJson : JSONObject): ResponseResultOf<String> {
        if (!headers.keys.contains("Accept")) {
            //в API запросах обязательно дожен быть заполнен формат заппрашиваемых данных
            headers.append("Accept", "application/json")
        }

        return Fuel
            .post(url, parameters)
            .body(bodyJson.toString())
            .header(headers)
            .responseString()
    }

    fun httpGetApi(url: String, headers: Headers, parameters: Parameters?=null): ResponseResultOf<String> {
        if (!headers.keys.contains("Accept")) {
            //в API запросах обязательно дожен быть заполнен формат заппрашиваемых данных
            headers.append("Accept", "application/json")
        }

        return Fuel
            .get(url, parameters)
            .header(headers)
            .responseString()
    }
}


