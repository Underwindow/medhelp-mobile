package com.podoynikov.medhelpmobile.activities

import android.annotation.SuppressLint
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.core.Headers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.Client
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.ResponseStatus
import com.podoynikov.medhelpmobile.extensions.formIsValid
import com.podoynikov.medhelpmobile.services.ApiService
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
//        sharedPreferences.edit().clear().apply()
        val token = sharedPreferences.getString("JWT_TOKEN", null)
        if (token != null) {
            CoroutineScope(Dispatchers.IO + Job()).launch {
                val (_, _, result) = ApiService.getRequestAsync(ApiService.Urls.referrals, Headers().append("Authorization", token), null)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> {
                            val jsonResponse = JSONObject(result.get())
                            if (ApiService.getResponseStatus(jsonResponse) == ResponseStatus.OK)
                            {
                                Client.instance.authorize(token)
                                onSuccessResponse()
                            }
                            else
                                Toast.makeText(applicationContext, "Необходима авторизация", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Failure -> Toast.makeText(applicationContext, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signInBtn.setOnClickListener {
            if (!formIsValid(usernameEt, passwordEt))
                return@setOnClickListener

            val username = usernameEt.text.toString()
            val password = passwordEt.text.toString()

            CoroutineScope(Dispatchers.IO + Job()).launch {
                val (_, _, result) = Client.instance.authenticate(username, password)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> onSuccessResponse()
                        is Result.Failure -> Toast.makeText(applicationContext, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun onSuccessResponse() {
        if (Client.instance.isAuthenticated) {
            sharedPreferences
                    .edit()
                    .putString("JWT_TOKEN", Client.instance.jwtToken)
                    .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else
            Toast.makeText(applicationContext, "Ошибка. Проверьте введенные данные", Toast.LENGTH_SHORT).show()
    }
}