package com.podoynikov.medhelpmobile

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
import com.podoynikov.medhelpmobile.services.ApiService

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
//        sharedPreferences.edit().clear().apply()
//        val token = sharedPreferences.getString("JWT_TOKEN", null)
//        if (token != null) {
//            CoroutineScope(Dispatchers.IO + Job()).launch {
//                val (request, response, result) = ApiService.getRequestAsync(ApiService.Urls.referrals, Headers().append("Authorization", token), null)
//
//                withContext(Dispatchers.Main) {
//                    when (result) {
//                        is Result.Success -> {
//                            Client.instance.authorize(token)
//                            onAuthSuccess()
//                        }
//                        is Result.Failure -> Toast.makeText(applicationContext, "Срок действия токена истёк", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }

        signInBtn.setOnClickListener {
            val username = usernameEt.text.toString()
            val password = passwordEt.text.toString()

            CoroutineScope(Dispatchers.IO + Job()).launch {
                val (_, _, result) = Client.instance.authenticate(username, password)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> onAuthSuccess()
                        is Result.Failure -> Toast.makeText(applicationContext, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun onAuthSuccess() {
        sharedPreferences.edit()
            .putString("JWT_TOKEN", Client.instance.jwtToken)
            .apply()
////        editor.putString("USERNAME", Client.instance.username)
////        editor.putString("PASSWORD", Client.instance.password)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}