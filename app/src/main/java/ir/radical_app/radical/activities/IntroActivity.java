package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.onboarding.Onboarding1;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.intro_container,new Onboarding1())
                .commitNow();
        Const.Companion.setOnboarding(1);
    }

    @Override
    public void onBackPressed() {

    }


}
