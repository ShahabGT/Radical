package ir.radical_app.radical.classes

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import ir.radical_app.radical.R
import ir.map.sdk_map.Mapir;
import ir.map.sdk_services.ServiceSDK


class MyAppController : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Mapir.getInstance(this, getString(R.string.map_key))
        ServiceSDK.init(this)
        Fresco.initialize(this)
        MultiDex.install(this)

    }
}