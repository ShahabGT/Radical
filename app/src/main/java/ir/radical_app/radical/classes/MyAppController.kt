package ir.radical_app.radical.classes

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco

class MyAppController : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        MultiDex.install(this)
    }
}