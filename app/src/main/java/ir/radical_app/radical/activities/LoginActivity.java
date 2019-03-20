package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.fragments.LoginFragment;
import ir.radical_app.radical.R;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .add(R.id.login_container,new LoginFragment())
                .commit();
    }
}
