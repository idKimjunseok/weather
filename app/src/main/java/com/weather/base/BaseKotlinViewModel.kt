package com.weather.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.weather.model.ErrorData
import com.weather.util.MyLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseKotlinViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun setOnCleared() {
        onCleared()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getErrorMessage(it: Any) : String {
        try {
            return when(it) {
                it as? UnknownHostException, it as? SocketTimeoutException, it as? ConnectException -> {
                    "네트워크 상태를 확인해 주세요"
                }
                it as? HttpException -> {
                    val errorbody : ResponseBody = it.response()?.errorBody()!!
                    val errormessage = errorbody.charStream()
                    val text = GsonBuilder().create().fromJson(errormessage, ErrorData::class.java)
                    text.error_msg
                }
                it as? Throwable -> {
                    val data = it as Throwable
                    data.message?.let {
                        it
                    }?: kotlin.run {
                        ""
                    }
                }
                else -> {
                    ""
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return ""
        }
    }
}