package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MyToast;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class NewVersionDialog extends Dialog {
    private FragmentActivity context;
    private String urgent;
    private boolean doubleBackToExitPressedOnce;
    private ImageView playstore,direct;


    public NewVersionDialog(@NonNull FragmentActivity context, String urgent) {
        super(context);
        this.context= context;
        this.urgent = urgent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_newversion);
        doubleBackToExitPressedOnce = false;

        playstore = findViewById(R.id.newversion_dialog_playstore);
        direct = findViewById(R.id.newversion_dialog_direct);
        onClicks();

    }

    private void onClicks(){
        playstore.setOnClickListener(View-> {

                final String appPackageName = context.getPackageName();
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

        });

        direct.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://radical-app.ir/download/radical.apk"));
            context.startActivity(intent);


        });


    }

    @Override
    public void onBackPressed() {
        if(urgent.equals("0"))
            super.onBackPressed();
        else{
            if (doubleBackToExitPressedOnce) {
                context.finishAffinity();
                return;
            }

            doubleBackToExitPressedOnce = true;
            MyToast.Companion.create(context,context.getString(R.string.txt_exit));
            new Handler().postDelayed(()->

                    doubleBackToExitPressedOnce=false
            , 2000);}
    }
}
