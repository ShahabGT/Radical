package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import ir.radical_app.radical.R;

public class InfoDialog extends Dialog {

    private String text;
    private TextView description;
    private MaterialButton btn;

    public InfoDialog(@NonNull Context context,String text) {
        super(context);
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info);
        description = findViewById(R.id.info_dialog_description);
        btn = findViewById(R.id.info_dialog_btn);

        description.setText(text);
        btn.setOnClickListener(View ->
            dismiss()
        );


    }
}
