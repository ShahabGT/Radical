package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import ir.radical_app.radical.R;

public class MessageDialog extends Dialog {


    private String title,text,date;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MessageDialog(@NonNull Context context) {
        super(context);
    }

    private TextView dText,dTitle,dDate;
    private MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        dText = findViewById(R.id.message_dialog_text);
        dTitle = findViewById(R.id.message_dialog_title);
        dDate = findViewById(R.id.message_dialog_date);

        dText.setText(text);
        dTitle.setText(title);
        dDate.setText(date);

        button = findViewById(R.id.message_dialog_back);
        button.setOnClickListener(View->
                dismiss()
          );

    }
}
