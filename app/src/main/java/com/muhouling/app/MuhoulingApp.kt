package com.muhouling.app

import android.app.Application
import com.muhouling.app.data.local.PrefsStore

class MuhoulingApp : Application() {
    lateinit var prefsStore: PrefsStore
        private set

    override fun onCreate() {
        super.onCreate()
        prefsStore = PrefsStore(this)
    }
}
