package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.onboarding.Onboarding1;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static ir.radical_app.radical.classes.Const.onboarding;

public class IntroActivity extends AppCompatActivity {


    private ImageView backbtn;
    private FloatingActionButton nextbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        backbtn = findViewById(R.id.intro_backbtn);
        nextbtn = findViewById(R.id.intro_nextbtn);



        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.intro_container,new Onboarding1())
                .commit();
        onboarding=1;

    }

    @Override
    public void onBackPressed() {

        switch (onboarding){
            case 2:
                Onboarding1 onboarding1 = new Onboarding1();
                onboarding1.setFromBack(true);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                        .replace(R.id.intro_container,onboarding1)
                        .commit();
                onboarding=1;
                break;



        }
    }


}
