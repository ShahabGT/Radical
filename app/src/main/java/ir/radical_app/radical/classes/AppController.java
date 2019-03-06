package ir.radical_app.radical.classes;

import com.facebook.drawee.backends.pipeline.Fresco;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class AppController extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        MultiDex.install(this);

    }
}
