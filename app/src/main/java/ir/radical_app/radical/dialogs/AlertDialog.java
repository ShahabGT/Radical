package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MyListener;

public class AlertDialog extends Dialog {

    private String text;
    private MyListener listener;

    private TextView description;
    private MaterialButton ok,cancel;


    public AlertDialog(@NonNull Context context,String text,MyListener listener) {
        super(context);
        this.text=text;
        this.listener=listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        description = findViewById(R.id.alert_dialog_description);
        description.setText(text);

        cancel = findViewById(R.id.alert_dialog_cancel);
        cancel.setOnClickListener(View->
                dismiss()
        );

        ok = findViewById(R.id.alert_dialog_show);
        ok.setOnClickListener(View-> {
                listener.buttonClicked(true);
                dismiss();
        });



    }
}
