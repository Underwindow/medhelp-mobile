package com.podoynikov.medhelpmobile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.*
import com.podoynikov.medhelpmobile.activities.LoginActivity
import com.podoynikov.medhelpmobile.activities.ReferralActivity
import com.podoynikov.medhelpmobile.adapters.ReferralsRecyclerAdapter
import com.podoynikov.medhelpmobile.extensions.iterator
import com.podoynikov.medhelpmobile.extensions.parseReferralAnalysesJSON
import com.podoynikov.medhelpmobile.extensions.parseReferralJSON
import com.podoynikov.medhelpmobile.interfaces.ItemClickListener
import com.podoynikov.medhelpmobile.services.ApiService
import com.podoynikov.medhelpmobile.services.ApiService.getResponseStatus
import com.podoynikov.medhelpmobile.services.ApplicationService
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


class ScheduleFragment : Fragment(), ItemClickListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
//        println("onCreateView() called")

        val fragmentSchedule = inflater.inflate(R.layout.fragment_schedule, container, false) as RelativeLayout
        val recycler = fragmentSchedule.findViewById<RecyclerView>(R.id.recyclerView)
            recycler.setHasFixedSize(true);
            recycler.layoutManager = LinearLayoutManager(activity)

        initRecyclerAdapter(recycler, fragmentSchedule.context)

        fragmentSchedule.swipeRefresher.setOnRefreshListener {
            updateContent()
        }

        if (Client.instance.referrals.isEmpty())
            updateContent()

        return fragmentSchedule
    }

    private fun updateContent(){
        initReferralsByResponse()
    }

    private fun initReferralsByResponse() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val (request, response, result) = ApiService.getRequestAsync(ApiService.Urls.referrals, Client.instance.authHeaders, null)
//            println("jsonResponse: ${JSONObject(result.get())}")

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Failure -> println(
                        "Get referrals Failure result: ${String(response.data)}\n" +
                        "Headers: ${response.headers}"
                    )
                    is Result.Success -> {
                        val jsonResponse = JSONObject(result.get())
                        println("jsonResponse: $jsonResponse")
                        println("getResponseStatus(jsonResponse) == ResponseStatus.OK: ${getResponseStatus(jsonResponse) == ResponseStatus.OK}")
                        if (getResponseStatus(jsonResponse) == ResponseStatus.OK)
                        {
                            Client.instance.referrals.clear()
                            Client.instance.referrals.addAll(parseReferrals(jsonResponse))
                            Client.instance.referrals.sortBy { it.date }
                            Client.instance.referrals.forEach { initAnalysesByReferral(it) }

                            updateReferralsView()
                        }
                        else
                            activity?.parent?.let { ApplicationService.onSessionExpired(requireContext(), it) }
                    }
                }
            }
        }
    }

    private fun initAnalysesByReferral(referral: Referral) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val (request, response, result) = ApiService.getRequestAsync(ApiService.Urls.getAnalysisByReferralId(referral.id), Client.instance.authHeaders, null)

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Failure -> println(
                            "Get referrals Failure result: ${String(response.data)}\n" +
                                    "Headers: ${response.headers}"
                    )
                    is Result.Success -> {
                        val jsonResponse = JSONObject(result.get())

                        if (getResponseStatus(jsonResponse) == ResponseStatus.OK)
                        {
                            referral.analyses.clear()
                            referral.analyses.addAll(parseReferralAnalyses(jsonResponse))
                        }
                        else
                            activity?.parent?.let { ApplicationService.onSessionExpired(requireContext(), it) }
                    }
                }
            }
        }
    }

//    private fun onSessionExpired(){
//        Toast.makeText(activity, "Сессия устарела", Toast.LENGTH_SHORT).show()
//
//        Client.instance.logout()
//
//        startActivity(Intent(activity, LoginActivity::class.java))
//        activity?.finish()
//    }

    private fun updateReferralsView() {
        val adapter: ReferralsRecyclerAdapter = (recyclerView.adapter as ReferralsRecyclerAdapter)
            adapter.setItems(Client.instance.referrals)

        recyclerView.adapter?.notifyDataSetChanged()
        swipeRefresher.isRefreshing = false
    }

    private fun initRecyclerAdapter(recycler: RecyclerView, context: Context) {
        val adapter = ReferralsRecyclerAdapter(context, Client.instance.referrals, this)
        recycler.adapter = adapter //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView
    }

    private fun parseReferrals(data: JSONObject) : MutableList<Referral>{
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Client.instance.locale)
        val referrals = mutableListOf<Referral>()
        val referralsJSON = data.getJSONArray("directions")
        println("referralsJSON Size: ${referralsJSON.length()}")

        for (referralJSON: JSONObject in referralsJSON)
        {
            referrals.add(parseReferralJSON(referralJSON, dateFormatter))

        }

        return referrals
    }

    override fun onItemClickListener(item: Any) {
        val referral = item as Referral

        val intent = Intent(activity, ReferralActivity::class.java)
        intent.putExtra("EXTRA_REFERRAL_ID", referral.id)

        startActivity(intent)
    }

    private fun parseReferralAnalyses(data: JSONObject) : MutableList<Analysis>{
        val analyses = mutableListOf<Analysis>()
        val analysesJSON = data.getJSONArray("analysis")
        for (analysisJSON: JSONObject in analysesJSON)
            analyses.add(parseReferralAnalysesJSON(analysisJSON))

        return analyses
    }
}
