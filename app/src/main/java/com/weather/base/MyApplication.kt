package com.weather.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.weather.module.listOfModule
import com.weather.widger.image.ImageManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {


    fun get(context: Context): MyApplication {
        return context.applicationContext as MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(listOfModule)
            logger(AndroidLogger(Level.DEBUG))
        }

        MyAppGlideModule()
        ImageManager.init(this)

    }




}