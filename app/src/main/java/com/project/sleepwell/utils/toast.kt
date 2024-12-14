package com.project.sleepwell.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.showLoading(isLoading: Boolean) {
    this.visibility = if (isLoading) View.VISIBLE else View.GONE
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}