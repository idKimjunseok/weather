package com.weather.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.weather.base.MyApplication
import com.weather.databinding.CellToastBinding
import com.weather.widger.OnThrottleClickListener


fun View.onThrottleClick(action: (v: View) -> Unit) {
    val listener = View.OnClickListener { action(it) }
    setOnClickListener(OnThrottleClickListener(listener))
}

fun hideKeyboard(view: View) {
    (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun String.showToast(context: Context) {
    this.showToast(context, Gravity.BOTTOM, Toast.LENGTH_SHORT)
}

fun String.showToast(context: Context, gravity: Int, duration: Int) {
    val viewDataBinding = CellToastBinding.inflate(LayoutInflater.from(context))
    viewDataBinding.toastText.text = this

    Toast(context).apply {
        view = viewDataBinding.root
        setDuration(duration)
        setGravity(gravity, 0, 16.dp)
    }.show()

}
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

