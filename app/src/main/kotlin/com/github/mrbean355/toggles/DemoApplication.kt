package com.github.mrbean355.toggles

import android.app.Application
import android.util.Log

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val enabled = configureToggles {
            "my_awesome_feature".configure {
                good.free.debug = true
            }
            "my_awesome_feature_2".configure {
                good(default = true) {
                    free(default = false) {
                        debug = true
                    }
                }
            }
        }
        Log.i("DemoApplication", "Enabled toggles: $enabled")
    }
}