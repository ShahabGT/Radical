package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import ir.radical_app.radical.listeners.DialogListener;
import ir.radical_app.radical.R;


public class IntroDialog extends Dialog {


    private DialogListener listener;

    public IntroDialog(@NonNull Context context, DialogListener listener) {
        super(context);
        this.listener=listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_intro);

        MaterialButton ok = findViewById(R.id.intro_dialog_show);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.buttonClicked(true);
                dismiss();
            }
        });

        MaterialButton cancel = findViewById(R.id.intro_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }



}
