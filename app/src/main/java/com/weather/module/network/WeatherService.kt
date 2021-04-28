package com.weather.module.network

import com.weather.model.Location
import com.weather.model.Search
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("/api/location/search/")
    fun search(
            @Query("query") q: String,
    ): Single<ArrayList<Search>>

    @GET("api/location/{woeid}")
    fun location(
            @Path("woeid") woeid: Int,
    ): Single<Location>

}