package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import ir.radical_app.radical.activities.UpgradeActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.fragments.ProfileFragment;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;

public class UpgradeDialog extends Dialog {

    private MaterialButton buy,cancel;
    private FragmentActivity context;
    private int userPlan = MySharedPreference.getInstance(getContext()).getPlan();


    public UpgradeDialog(@NonNull FragmentActivity context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upgrade);

        init();

    }
    private void init(){
        buy = findViewById(R.id.plan_buy);
        if(userPlan==2){
            buy.setText(getContext().getString(R.string.plan_account));
        }
        cancel = findViewById(R.id.plan_cancel);
        onClicks();
    }

    private void onClicks(){
        if(userPlan==2) {
            buy.setOnClickListener(new View.OnClickListener() {
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
        }else{
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UpgradeActivity.class));
                    dismiss();
                }
            });


        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
