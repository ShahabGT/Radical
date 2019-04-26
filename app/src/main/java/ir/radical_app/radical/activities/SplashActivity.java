package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MySharedPreference;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView title;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splash_logo);
        title = findViewById(R.id.splash_title);
        animation();

        new Handler().postDelayed(() -> {

            if(!MySharedPreference.Companion.getInstance(SplashActivity.this).getIsLogin()){
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                SplashActivity.this.finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                SplashActivity.this.finish();
            }
        },1500);

    }

    private void animation(){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                logo,
                "ScaleX",
                0f,1f
        );
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                logo,
                "ScaleY",
                0f,1f
        );
        ObjectAnimator alpha = ObjectAnimator.ofFloat(
                title,
                "Alpha",
                0f,0.5f,1f
        );

        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        alpha.setDuration(1000);
        scaleX.start();
        scaleY.start();
        alpha.start();
    }

}
