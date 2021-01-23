package com.podoynikov.medhelpmobile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.podoynikov.medhelpmobile.Client
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.activities.LoginActivity

class LogoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val logoutFragment = inflater.inflate(R.layout.fragment_logout, container, false) as RelativeLayout
        val logoutButton = logoutFragment.findViewById<Button>(R.id.logoutBtn)

        logoutButton.setOnClickListener {
            Client.instance.logout()

            val sharedPreferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return logoutFragment
    }
}
