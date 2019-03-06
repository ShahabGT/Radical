package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class UpgradeActivity extends AppCompatActivity {

    private MaterialButton buy,buyWallet,cancel;
    private TextView costTitle,walletTitle;
    private int myWallet;
    private int planCost;
    private LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        init();
    }

    private void init(){
        costTitle = findViewById(R.id.upgrade_cost);
        walletTitle = findViewById(R.id.upgrade_wallet);
        buy = findViewById(R.id.upgrade_buy);
        buyWallet = findViewById(R.id.upgrade_buy_wallet);
        cancel = findViewById(R.id.upgrade_cancel);


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
                showBuyDialog(planCost+"",false);
            }
        });

        buyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyDialog((planCost-myWallet)+"",true);
            }
        });
    }

    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(UpgradeActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                                    costTitle.setText(getString(R.string.upgrade_cost, MyUtils.moneySeparator(response.body().getAmount())));
                                    planCost = Integer.parseInt(response.body().getAmount());
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
