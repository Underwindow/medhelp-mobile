package com.podoynikov.medhelpmobile

import com.podoynikov.medhelpmobile.services.ApiService.getResponseStatus
import org.json.JSONObject

class RegistrationResponse(response: JSONObject) {

    lateinit var status: ResponseStatus
        private set

    var msg: String? = null
        private set

    var token: String? = null
        private set

    init {
        status = getResponseStatus(response)
        when (status) {
            ResponseStatus.OK -> {
                token = response.getString("token")
            }
            ResponseStatus.INFO -> {
                msg = response.getJSONObject("status").getString("message")
            }
        }
    }
}