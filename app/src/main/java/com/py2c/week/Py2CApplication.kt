package com.py2c.week

import android.app.Application
import com.py2c.week.data.AppContainer

class Py2CApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
