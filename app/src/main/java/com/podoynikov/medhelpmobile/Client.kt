package com.podoynikov.medhelpmobile

import android.content.Context
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.services.ApiService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Client {

    companion object {
        val instance = Client()
    }
    val locale : Locale = Locale("ru")

    var referrals: MutableList<Referral> = ArrayList()

//    var username: String? = null
//        private set
//    var password: String? = null
        private set
    var jwtToken: String? = null
        private set
//    var lastName: String? = null
//        private set
//    var policyNumber: String? = null
//        private set
    var isAuthenticated: Boolean = false
        private set

    val authHeaders get() = Headers().append("Authorization", "${Client.instance.jwtToken?.toString()}")

    suspend fun register(_username : String, _password : String, _lastName: String? = null, _policyNumber: String? = null) : ResponseResultOf<String>{
        val bodyJson = JSONObject()
                .put("username", _username)
                .put("password", _password)
                .put("patient", JSONObject()
                        .put("lastname", _lastName)
                        .put("policy_number", _policyNumber)
                )

        val responseResultOf = ApiService.postRequestAsync(ApiService.Urls.registration, Headers(), null, bodyJson)

        withContext(Dispatchers.Main) {
            when (responseResultOf.third) {
                is Result.Failure -> println("Registration Failure result:  ${responseResultOf.third.get()}")
                is Result.Success -> {
//                    username = _username
//                    password = _password
                    authorize(responseResultOf.third.get())
                    println("Client registered!")
                }
            }
        }

        return responseResultOf
    }

    suspend fun authenticate(_username : String, _password : String) : ResponseResultOf<String> {
        val bodyJson = JSONObject()
            .put("username", _username)
            .put("password", _password)

        val responseResultOf = ApiService.postRequestAsync(ApiService.Urls.auth, Headers(), null, bodyJson)

        withContext(Dispatchers.Main) {
            when (responseResultOf.third) {
                is Result.Failure -> println("Authenticate Failure result:  ${responseResultOf.third.get()}")
                is Result.Success -> {
//                    username = _username
//                    password = _password
                    authorize(responseResultOf.third.get())
                    println("Client Authenticated!")
                }
            }
        }

        return responseResultOf
    }

    fun authorize(token: String?){
        isAuthenticated = true
        jwtToken = token
    }
}