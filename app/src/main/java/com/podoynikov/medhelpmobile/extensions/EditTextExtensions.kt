package com.podoynikov.medhelpmobile.extensions

import android.text.Editable
import android.widget.EditText

fun EditText.setEditableText(text: CharSequence) {
    this.text = Editable.Factory.getInstance().newEditable(text)
}