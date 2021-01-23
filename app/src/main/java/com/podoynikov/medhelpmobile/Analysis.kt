package com.podoynikov.medhelpmobile

class Analysis (
        val id: Int,
        val name: String,
        val isChecked: Boolean,
        val file_id : Int?) {

    var isClicked = false
    val statusColor: Int get() =
        if (isChecked)
            R.color.green
        else {
            if (isClicked || file_id != null) R.color.grey else R.color.orange
        }
}