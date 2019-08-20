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
    }

    @Override
    public void onBackPressed() {

    }


}
