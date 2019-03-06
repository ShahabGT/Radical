package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import ir.radical_app.radical.activities.UpgradeActivity;
import ir.radical_app.radical.R;

public class UpgradeDialog extends Dialog {

    private MaterialButton buy,cancel;
    private Context context;


    public UpgradeDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upgrade);
        buy = findViewById(R.id.plan_buy);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UpgradeActivity.class));
                dismiss();
            }
        });

        cancel = findViewById(R.id.plan_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
