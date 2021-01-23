package com.podoynikov.medhelpmobile

import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.services.ApiService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*

class Client {

    companion object {
        var instance : Client = Client()
            private set
    }

    val locale : Locale = Locale("ru")
    val timezone = TimeZone.getTimeZone("GMT")

    var referrals: MutableList<Referral> = mutableListOf()

        private set
    var jwtToken: String? = null
        private set

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

        val responseResultOf = ApiService.postRequestAsync(ApiService.Urls.registration, Headers(), null, bodyJson.toString())

        withContext(Dispatchers.Main) {
            when (responseResultOf.third) {
                is Result.Failure -> println("Registration Failure result:  ${responseResultOf.third.get()}")
                is Result.Success -> {
                    print(responseResultOf.third.get())
                    val parsedResponse = RegistrationResponse(JSONObject(responseResultOf.third.get()))
                    if (parsedResponse.status == ResponseStatus.OK)
                        authorize(parsedResponse.token)
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

        val responseResultOf = ApiService.postRequestAsync(ApiService.Urls.auth, Headers(), null, bodyJson.toString())

        withContext(Dispatchers.Main) {
            when (responseResultOf.third) {
                is Result.Failure -> println("Authenticate Failure result:  ${responseResultOf.third.get()}")
                is Result.Success -> {
                    print(responseResultOf.third.get())
                    val parsedResponse = RegistrationResponse(JSONObject(responseResultOf.third.get()))
                    if (parsedResponse.status == ResponseStatus.OK)
                        authorize(parsedResponse.token)
                    println("Client Authenticated!")
//
//                    authorize(responseResultOf.third.get())
//                    println("Client Authenticated!")
                }
            }
        }

        return responseResultOf
    }

    fun authorize(token: String?){
        isAuthenticated = true
        jwtToken = token
    }

    fun logout() {
        instance = Client()
    }
}