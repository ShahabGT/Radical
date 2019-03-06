package ir.radical_app.radical.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ir.radical_app.radical.R;

public class MyToast {





    public static void Create(Context context, String text){
        Toast t= Toast.makeText(context,"",Toast.LENGTH_LONG);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast,null);
        TextView textView =  view.findViewById(R.id.toast_text);
        textView.setText(text);
        t.setView(view);
        t.show();

    }
}
