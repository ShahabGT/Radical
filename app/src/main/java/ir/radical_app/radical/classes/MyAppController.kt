package ir.radical_app.radical.classes

import android.app.Application
import android.graphics.Color
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import ir.map.sdk_map.Mapir
import ir.map.sdk_services.ServiceSDK
import ir.radical_app.radical.R


class MyAppController : Application() {

    override fun onCreate() {
        super.onCreate()
        Mapir.getInstance(this, getString(R.string.map_key))
        ServiceSDK.init(this)
        Fresco.initialize(this)
        MultiDex.install(this)
        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(this, fontRequest).setReplaceAll(true)

        EmojiCompat.init(config)


    }
}