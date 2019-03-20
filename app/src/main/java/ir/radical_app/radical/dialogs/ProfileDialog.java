package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.fragments.ProfileFragment;

public class ProfileDialog extends Dialog {

    private MaterialButton profile,cancel;
    private FragmentActivity context;

    public ProfileDialog(@NonNull FragmentActivity context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile);

        init();
    }

    private void init(){
        profile = findViewById(R.id.profile_dialog_buy);
        cancel = findViewById(R.id.profile_dialog_cancel);
        onClicks();
    }

    private void onClicks(){

        profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    context.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                            .add(R.id.main_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                    dismiss();
                }
            });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
