package com.podoynikov.medhelpmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.podoynikov.medhelpmobile.Client
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.Referral
import com.podoynikov.medhelpmobile.interfaces.ItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class ReferralsRecyclerAdapter (context: Context, private var referrals: List<Referral>, private val referralClickListener: ItemClickListener)
    : RecyclerView.Adapter<ReferralsRecyclerAdapter.ViewHolder>(){
    val calendar : Calendar = Calendar.getInstance()
    private val inflater = LayoutInflater.from(context)

    fun setItems(_referrals: List<Referral>)
    {
        referrals =_referrals
        notifyDataSetChanged()
    }

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.referral, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val referral = referrals[position]
        holder.bind(referral, calendar, Client.instance.locale)
        holder.itemView.setOnClickListener {
            referralClickListener.onItemClickListener(referral)
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return referrals.size
    }

    class ViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
        private val dateView: TextView      = view.findViewById(R.id.referralDate)
        private val timeView: TextView      = view.findViewById(R.id.referralTime)
        private val doctorView: TextView    = view.findViewById(R.id.doctor)
        private val hospitalView: TextView  = view.findViewById(R.id.hospital)
        private val statusView: TextView    = view.findViewById(R.id.status)

        fun bind(referral: Referral, calendar: Calendar, locale: Locale) {
            calendar.time = referral.date
            val timezone = TimeZone.getTimeZone("GMT")
            val dateFormat = SimpleDateFormat("d MMM, EEE", locale)
                dateFormat.timeZone = timezone
            val timeFormat = SimpleDateFormat("HH:mm", locale)
                timeFormat.timeZone = timezone

            dateView.text       = dateFormat.format(calendar.time)
            timeView.text       = timeFormat.format(calendar.time)
            doctorView.text     = "${referral.doctorSpecialty} ${referral.doctorName}"
            hospitalView.text   = referral.medicalOrganization
            statusView.text     = referral.status
            statusView.setTextColor(ContextCompat.getColor(statusView.context, referral.statusTextColor))
        }
    }
}