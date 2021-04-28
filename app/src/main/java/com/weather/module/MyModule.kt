package com.weather.module

import com.weather.base.MyConfig
import com.weather.module.network.DataModel
import com.weather.module.network.DataModelImpl
import com.weather.module.network.WeatherService
import com.weather.view.adapter.WeatherAdapter
import com.weather.view.viewmodel.MainViewModel
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

var retrofitModule = module {
    single {
        OkHttpClient.Builder()
                .addInterceptor(
                        HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .authenticator(object : Authenticator {
                    override fun authenticate(route: Route?, response: Response): Request? {
                        return null
                    }
                })
                .build()
    }

    single<WeatherService> {
        Retrofit.Builder()
                .baseUrl(MyConfig.SETTING_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(get())
                .build()
                .create(WeatherService::class.java)

    }

}

var adapterModule = module {
    factory {
        WeatherAdapter()
    }
}

var modelModule = module {
    factory<DataModel> {
        DataModelImpl(get())
    }
}
var viewModelModule = module {
    viewModel {
        MainViewModel(get(), get())
    }
}


var listOfModule = listOf(
        retrofitModule,
        adapterModule,
        modelModule,
        viewModelModule
)
