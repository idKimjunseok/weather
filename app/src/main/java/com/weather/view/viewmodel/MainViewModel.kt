package com.weather.view.viewmodel

import android.content.Context
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weather.base.BaseKotlinViewModel
import com.weather.model.Location
import com.weather.model.Search
import com.weather.module.network.DataModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val model: DataModel, private val context: Context) : BaseKotlinViewModel() {

    lateinit var mProgressDialog: ContentLoadingProgressBar

    val _search = MutableLiveData<ArrayList<Search>>()
    val search: LiveData<ArrayList<Search>>
        get() = _search


    val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location

    val _list = MutableLiveData<ArrayList<Location>>()
    val list: LiveData<ArrayList<Location>>
        get() = _list

    fun setProgress(view: ContentLoadingProgressBar) {
        this.mProgressDialog = (view)
    }

    fun ShowProgress() {
        if (::mProgressDialog.isInitialized) {
            mProgressDialog.show()
        }
    }

    fun HideProgress() {
        if (::mProgressDialog.isInitialized) {
            mProgressDialog.hide()
        }
    }

    fun getSearch(q: String) {
        addDisposable(model.getSearch(q)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe{ShowProgress()}
                .subscribe({
                    it.run {
                        _search.postValue(this)
                    }
                }, {
                    HideProgress()
                    _error.postValue(getErrorMessage(it))
                })
        )
    }

    fun getLocation(data: ArrayList<Search>) {
        val locationList = ArrayList<Single<Location>>()
        for (item in data ) {
            locationList.add(model.getLocation(item.woeid).subscribeOn(Schedulers.io()))
        }
        addDisposable(Single.zip(locationList) { args -> arrayListOf(args) }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally{HideProgress()}
                .subscribe({
                    it.run {
                        _list.postValue((it[0].toCollection(ArrayList())) as ArrayList<Location>)
                    }
                }, {
                    HideProgress()
                    _error.postValue(getErrorMessage(it))
                })
        )
    }

}