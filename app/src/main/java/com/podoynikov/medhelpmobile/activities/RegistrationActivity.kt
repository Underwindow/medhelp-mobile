package com.podoynikov.medhelpmobile.activities

import android.annotation.SuppressLint
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.Client
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.extensions.formIsValid
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.passwordEt
import kotlinx.android.synthetic.main.activity_registration.signInBtn
import kotlinx.android.synthetic.main.activity_registration.usernameEt

class RegistrationActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        signUnBtn.setOnClickListener {
            if (!formIsValid(usernameEt, passwordEt, lastnameEt, policyNumberEt))
                return@setOnClickListener

            val username        = usernameEt.text.toString()
            val password        = passwordEt.text.toString()
            val lastName        = lastnameEt.text.toString()
            val policyNumber    = policyNumberEt.text.toString()

            CoroutineScope(Dispatchers.IO + Job()).launch {
                val (_, _, result) = Client.instance.register(username, password, lastName, policyNumber)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Success -> onSuccessResponse()
                        is Result.Failure -> Toast.makeText(applicationContext, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signInBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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