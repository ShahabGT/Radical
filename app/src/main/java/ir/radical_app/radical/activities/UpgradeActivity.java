package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.BuyDialog;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class UpgradeActivity extends AppCompatActivity {

    private MaterialButton buy,buyWallet,cancel;
    private TextView walletTitle;
    private int myWallet;
    private LoadingDialog dialog;
    private RadioGroup radioGroup;
    private AppCompatRadioButton radio3,radio7,radio15,radio30,radio60,radio90;
    private int[] plansCost= new int[6];
    private int selectedIndex=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        init();
    }

    private void init(){
        walletTitle = findViewById(R.id.upgrade_wallet);
        buy = findViewById(R.id.upgrade_buy);
        buyWallet = findViewById(R.id.upgrade_buy_wallet);
        cancel = findViewById(R.id.upgrade_cancel);
        radioGroup = findViewById(R.id.upgrade_radio_group);
        radio3 = findViewById(R.id.upgrade_radio3);
        radio7 = findViewById(R.id.upgrade_radio7);
        radio15 = findViewById(R.id.upgrade_radio15);
        radio30 = findViewById(R.id.upgrade_radio30);
        radio60 = findViewById(R.id.upgrade_radio60);
        radio90 = findViewById(R.id.upgrade_radio90);


        onClicks();
        getData();

    }

    private void onClicks(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedIndex==-1){
                    MyToast.Create(UpgradeActivity.this,"not selected");
                }else{
                    showBuyDialog(plansCost[selectedIndex]+"",false);
                }
            }
        });

        buyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedIndex==-1){
                    MyToast.Create(UpgradeActivity.this,"not selected");
                }else{
                    int cost = plansCost[selectedIndex]-myWallet;
                    if(cost<100){
                        MyToast.Create(UpgradeActivity.this,"low cost");
                    }else{
                        showBuyDialog(cost+"",false);

                    }
                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = group.findViewById(group.getCheckedRadioButtonId());
                selectedIndex = group.indexOfChild(radioButton);

            }
        });

    }

    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(UpgradeActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(false);
            dialog.show();
        }else{
            dialog.dismiss();
        }
    }


    private void getData(){
        String number = MySharedPreference.getInstance(UpgradeActivity.this).getNumber();
        String accessToken = MySharedPreference.getInstance(UpgradeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(UpgradeActivity.this,getString(R.string.data_error));
            MySharedPreference.getInstance(UpgradeActivity.this).clear();
            startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
            UpgradeActivity.this.finish();
        }else{
            loadingDialog(false);
            RetrofitClient.getInstance().getApi()
                    .getPlan()
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if(response.isSuccessful()){
                                if(response.body().getMessage().equals("ok")){
                                    plansCost[0]=Integer.parseInt(response.body().getAmount3());
                                    plansCost[1]=Integer.parseInt(response.body().getAmount7());
                                    plansCost[2]=Integer.parseInt(response.body().getAmount15());
                                    plansCost[3]=Integer.parseInt(response.body().getAmount());
                                    plansCost[4]=Integer.parseInt(response.body().getAmount60());
                                    plansCost[5]=Integer.parseInt(response.body().getAmount90());
                                    radio3.setText(getString(R.string.upgrade_radio_model,"3",MyUtils.moneySeparator(plansCost[0]+"")));
                                    radio7.setText(getString(R.string.upgrade_radio_model,"7",MyUtils.moneySeparator(plansCost[1]+"")));
                                    radio15.setText(getString(R.string.upgrade_radio_model,"15",MyUtils.moneySeparator(plansCost[2]+"")));
                                    radio30.setText(getString(R.string.upgrade_radio_model,"30",MyUtils.moneySeparator(plansCost[3]+"")));
                                    radio60.setText(getString(R.string.upgrade_radio_model,"60",MyUtils.moneySeparator(plansCost[4]+"")));
                                    radio90.setText(getString(R.string.upgrade_radio_model,"90",MyUtils.moneySeparator(plansCost[5]+"")));
                                    getWallet();
                                }else{
                                    loadingDialog(true);
                                    MyToast.Create(UpgradeActivity.this,getString(R.string.general_error));
                                    onBackPressed();
                                }

                            }else{
                                loadingDialog(true);
                                MyToast.Create(UpgradeActivity.this,getString(R.string.general_error));
                                onBackPressed();
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Create(UpgradeActivity.this,getString(R.string.general_error));
                            onBackPressed();
                        }
                    });
        }

    }
    private void getWallet(){
        String number = MySharedPreference.getInstance(UpgradeActivity.this).getNumber();
        String accessToken = MySharedPreference.getInstance(UpgradeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(UpgradeActivity.this,getString(R.string.data_error));
            MySharedPreference.getInstance(UpgradeActivity.this).clear();
            startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
            UpgradeActivity.this.finish();
        }else {
            RetrofitClient.getInstance().getApi()
                    .getWallet(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("ok")) {
                                   walletTitle.setText(getString(R.string.upgrade_wallet, MyUtils.moneySeparator(response.body().getAmount())));

                                    myWallet = Integer.parseInt(response.body().getAmount());

                                } else {
                                    MyToast.Create(UpgradeActivity.this, getString(R.string.general_error));
                                    onBackPressed();
                                }

                            } else {
                                MyToast.Create(UpgradeActivity.this, getString(R.string.general_error));
                                onBackPressed();
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);

                            MyToast.Create(UpgradeActivity.this, getString(R.string.general_error));
                            onBackPressed();
                        }
                    });
        }

    }

    private void showBuyDialog(String amount,boolean useWallet) {
        BuyDialog buyDialog = new BuyDialog(UpgradeActivity.this,amount,useWallet);

        buyDialog.setCanceledOnTouchOutside(false);
        buyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        buyDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        buyDialog.show();
        Window window = buyDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        buyDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    buyDialog.dismiss();
                }
                return true;
            }
        });
    }
}
