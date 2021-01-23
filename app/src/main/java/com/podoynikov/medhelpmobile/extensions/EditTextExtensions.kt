package com.podoynikov.medhelpmobile.extensions

import android.text.Editable
import android.text.TextUtils
import android.widget.EditText

fun EditText.setEditableText(text: CharSequence) {
    this.text = Editable.Factory.getInstance().newEditable(text)
}

fun formIsValid(vararg params: EditText, errMsg : String = "Заполните это поле") : Boolean {
    val emptyEtList = params.filter { TextUtils.isEmpty(it.text.toString()) }
    for (et : EditText in emptyEtList)
        et.error = errMsg

    return emptyEtList.isEmpty()
}