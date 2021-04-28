package com.weather.module.network

import com.weather.model.Location
import com.weather.model.Search
import io.reactivex.Observable
import io.reactivex.Single

interface DataModel {

    fun getSearch(
        query: String,
    ): Single<ArrayList<Search>>

    fun getLocation(
            woeid: Int,
    ): Single<Location>

}

