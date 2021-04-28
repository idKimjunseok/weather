package com.weather.module.network

import com.weather.model.Location
import com.weather.model.Search
import io.reactivex.Observable
import io.reactivex.Single

class DataModelImpl(private val service: WeatherService) : DataModel {

    override fun getSearch(query: String): Single<ArrayList<Search>> {
        return service.search(query)
    }

    override fun getLocation(woeid: Int): Single<Location> {
        return service.location(woeid)
    }

}
