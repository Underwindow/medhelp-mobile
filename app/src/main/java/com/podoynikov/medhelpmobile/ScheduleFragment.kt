package com.podoynikov.medhelpmobile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.adapters.ReferralsRecyclerAdapter
import com.podoynikov.medhelpmobile.extensions.iterator
import com.podoynikov.medhelpmobile.extensions.parseReferralJSON
import com.podoynikov.medhelpmobile.interfaces.ReferralClickListener
import com.podoynikov.medhelpmobile.services.ApiService
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment(), ReferralClickListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val fragmentSchedule = inflater.inflate(R.layout.fragment_schedule, container, false) as RelativeLayout
        val recycler = fragmentSchedule.findViewById<RecyclerView>(R.id.recyclerView)
            recycler.setHasFixedSize(true);
            recycler.layoutManager = LinearLayoutManager(activity)

        initRecycler(recycler, fragmentSchedule.context)
        if (Client.instance.referrals.isEmpty())
            initReferralsByResponse()

        return fragmentSchedule
    }

    private fun initReferralsByResponse() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val (request, response, result) = ApiService.getRequestAsync(ApiService.Urls.referrals, Client.instance.authHeaders, null)

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Failure -> println(
                        "Get referrals Failure result: ${String(response.data)}\n" +
                        "Headers: ${response.headers}"
                    )
                    is Result.Success -> {
                        parseReferrals(result)
                        updateReferralsView()
                    }
                }
            }
        }
    }

    private fun updateReferralsView() {
        val adapter: ReferralsRecyclerAdapter = (recyclerView.adapter as ReferralsRecyclerAdapter)
            adapter.setItems(Client.instance.referrals)

        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onReferralClickListener(referral: Referral) {
        TODO("Not yet implemented")
    }

    private fun initRecycler(recycler: RecyclerView, context: Context) {
        val adapter = ReferralsRecyclerAdapter(context, Client.instance.referrals, this)
        recycler.adapter = adapter //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView
    }

    private fun parseReferrals(result: Result<String, FuelError>) {
        Client.instance.referrals.clear()

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Client.instance.locale)
        dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Yekaterinburg")

        for (referralJSON: JSONObject in JSONArray(result.get()))
            Client.instance.referrals.add(parseReferralJSON(referralJSON, dateFormatter))

        Client.instance.referrals.sortByDescending { it.date }
    }
}
