package com.podoynikov.medhelpmobile.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.ProgressBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.result.Result
import com.podoynikov.medhelpmobile.*
import com.podoynikov.medhelpmobile.adapters.AnalysesRecyclerAdapter
import com.podoynikov.medhelpmobile.fragments.TimePickerFragment
import com.podoynikov.medhelpmobile.interfaces.ItemClickListener
import com.podoynikov.medhelpmobile.services.ApiService
import com.podoynikov.medhelpmobile.services.ApplicationService
import kotlinx.android.synthetic.main.referral_expanded.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ReferralActivity : AppCompatActivity(), ItemClickListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var referral : Referral
    private var uploadingAnalysisId : Int? = null
    private var uploadingProgressBar : ProgressBar? = null

    private val calendar = Calendar.getInstance()
    val dateFormat      = SimpleDateFormat("d MMMM, EEEE", Client.instance.locale)
    val timeFormat      = SimpleDateFormat("HH:mm", Client.instance.locale)

    private var notifyEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.referral_expanded)

        createNotificationChannel()
        notifyAtTimePicker.setOnClickListener {
            val timePicker = TimePickerFragment()
            timePicker.show(supportFragmentManager, "time picker")
        }

        referral = Client.instance.referrals.first {
            it.id == intent.getIntExtra("EXTRA_REFERRAL_ID", -1)
        }

        calendar.time       = referral.date
        dateFormat.timeZone = Client.instance.timezone
        timeFormat.timeZone = Client.instance.timezone

        setReferralDataToView(referral)

        val recycler = findViewById<RecyclerView>(R.id.analysesView)
            recycler.setHasFixedSize(true);
            recycler.layoutManager = LinearLayoutManager(this)

        initRecyclerAdapter(recycler, this)
        if (referral.analyses.isNotEmpty())
            updateAnalysesView()
    }

    private fun setReferralDataToView(referral: Referral){
        date.text               = "${dateFormat.format(calendar.time)}\n${timeFormat.format(calendar.time)}"
        doctorSpecialty.text    = referral.doctorSpecialty
        doctorName.text         = referral.doctorName
        hospital.text           = referral.medicalOrganization
        status.text             = referral.status
        status.setTextColor(ContextCompat.getColor(this, referral.statusTextColor))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
                "medicalAppointment",
                "medicalAppointmentChannel",
                NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Channel for medical appointments"
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun initRecyclerAdapter(recycler: RecyclerView, context: Context) {
        val adapter = AnalysesRecyclerAdapter(context, referral.analyses, this)
        recycler.adapter = adapter //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView
    }

    private fun updateAnalysesView() {
        val adapter: AnalysesRecyclerAdapter = (analysesView.adapter as AnalysesRecyclerAdapter)
        adapter.setItems(referral.analyses)

        analysesView.adapter?.notifyDataSetChanged()
    }

    override fun onItemClickListener(item: Any) {
        val analysis = item as Analysis

        if (!analysis.isChecked)
        {
            analysis.isClicked = true
            uploadingAnalysisId = analysis.id
            selectAnalysisPDF(analysis)
        }
    }

    private fun selectAnalysisPDF(analysis: Analysis) {
        val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
        val chooser = Intent.createChooser(intent, "Выберите PDF файл ${analysis.name}")
        startActivityForResult(chooser, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data?.data != null) {
            val tempFile = File(this.filesDir.absolutePath, "temp_file.pdf")
            //Copy URI contents into temporary file.
            try {
                tempFile.createNewFile()
                val inputStream = this.contentResolver.openInputStream(data.data!!)
                val outputStream = FileOutputStream(tempFile)

                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

            } catch (e: IOException) {
                e.stackTrace
            }

            //Now fetch the new URI
            val newUri = Uri.fromFile(tempFile)
            uploadPDFFile(newUri)
        }
        else
            uploadingAnalysisId = null
    }

    private fun uploadPDFFile(uri: Uri) {
        Toast.makeText(this, "Файл загружается", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val (request, response, result) = ApiService.uploadRequestAsync(ApiService.Urls.uploadAnalysisById(uploadingAnalysisId!!), uri, Client.instance.authHeaders)
            uploadingAnalysisId = null
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Failure -> println(
                            "Get referrals Failure result: ${response.data}\n" +
                            "response.Headers: ${response}" +
                            "request.Headers: ${request.headers}"
                    )
                    is Result.Success -> {
                        Toast.makeText(applicationContext, "файл загружен", Toast.LENGTH_LONG).show()
                        updateAnalysesView()
//                        if (ApiService.getResponseStatus(JSONObject(result.get())) == ResponseStatus.OK)
//                        {
//                            println("Upload ResponseStatus.OK")
//
//                            Toast.makeText(applicationContext, "файл загружен", Toast.LENGTH_LONG).show()
//                            updateAnalysesView()
//                        }
//                        else
//                        {
//                            println("Upload ResponseStatus.ERROR")
//                            ApplicationService.onSessionExpired(this@ReferralActivity, this@ReferralActivity)
//                        }
                    }
                }
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.time = referral.date
        c.add(Calendar.DATE, -1)
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)

        if (c.timeInMillis > System.currentTimeMillis())
        {
            val tFormat = SimpleDateFormat("mm", Client.instance.locale)
            notifyTimeTv.text = "${dateFormat.format(c.time)} ${c.get(Calendar.HOUR_OF_DAY)}:${tFormat.format(c.time)}"

            val intent = Intent(this, ReminderBroadcast::class.java)
            intent.putExtra("EXTRA_REMINDER_TITLE", "Завтра приём. ${referral.doctorSpecialty}")
            intent.putExtra(
                "EXTRA_REMINDER_TEXT",
                "${timeFormat.format(calendar.time)} ${referral.medicalOrganization}")

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + c.timeInMillis - System.currentTimeMillis(),
                pendingIntent
            )

            Toast.makeText(this, "Уведомление установлено", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show()
    }
}