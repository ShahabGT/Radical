package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MyUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

public class ErrorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Button reTry = findViewById(R.id.error_tryagain);
        reTry.setOnClickListener(View->
                onBackPressed()
        );
    }

    @Override
    public void onBackPressed() {
        if(MyUtils.Companion.checkInternet(ErrorActivity.this))
            super.onBackPressed();
    }
}
