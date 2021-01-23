package com.podoynikov.medhelpmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.podoynikov.medhelpmobile.activities.LoginActivity

abstract class HttpRequester {
    fun onTokenExpired(context: Context, activity: Activity)
    {
        Client.instance.logout()

        startActivity(context, Intent(activity, LoginActivity::class.java), Bundle())
        activity.finish()
    }
}