package com.podoynikov.medhelpmobile.services

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.podoynikov.medhelpmobile.Client
import com.podoynikov.medhelpmobile.activities.LoginActivity

class ApplicationService : Application(){
    companion object {
        fun onSessionExpired(context: Context, activity : Activity){
            Toast.makeText(context, "Сессия устарела", Toast.LENGTH_SHORT).show()

            Client.instance.logout()

            startActivity(context, Intent(activity, LoginActivity::class.java), null)
            activity.finish()
        }
    }
}