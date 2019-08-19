package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitBuyClient;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class UpgradeActivity extends AppCompatActivity {

    private MaterialButton buy,buyWallet;
    private TextView walletTitle;
    private int myWallet=0;
    private LoadingDialog dialog;

    private int[] plansCost= new int[6];

    private int selectedPlan =-1;

    private ConstraintLayout lOne,lTwo,lThree,lFour,lFive,lSix;
    private TextView pOne,pTwo,pThree,pFour,pFive,pSix;
    private TextView tOne,tTwo,tThree,tFour,tFive,tSix;
    private ImageView cOne,cTwo,cThree,cFour,cFive,cSix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        if(MySharedPreference.Companion.getInstance(this).getName().isEmpty()){
            MyToast.Companion.create(this,getString(R.string.profile_error2));
            onBackPressed();
        }
        init();


    }

    private void init(){
        lOne = findViewById(R.id.upgrade_one);
        lTwo = findViewById(R.id.upgrade_two);
        lThree = findViewById(R.id.upgrade_three);
        lFour = findViewById(R.id.upgrade_four);
        lFive = findViewById(R.id.upgrade_five);
        lSix = findViewById(R.id.upgrade_six);

        pOne = findViewById(R.id.upgrade_one_price);
        pTwo = findViewById(R.id.upgrade_two_price);
        pThree = findViewById(R.id.upgrade_three_price);
        pFour = findViewById(R.id.upgrade_four_price);
        pFive = findViewById(R.id.upgrade_five_price);
        pSix = findViewById(R.id.upgrade_six_price);

        tOne = findViewById(R.id.upgrade_one_title);
        tTwo = findViewById(R.id.upgrade_two_title);
        tThree = findViewById(R.id.upgrade_three_title);
        tFour = findViewById(R.id.upgrade_four_title);
        tFive = findViewById(R.id.upgrade_five_title);
        tSix = findViewById(R.id.upgrade_six_title);

        cOne = findViewById(R.id.upgrade_one_check);
        cTwo = findViewById(R.id.upgrade_two_check);
        cThree = findViewById(R.id.upgrade_three_check);
        cFour = findViewById(R.id.upgrade_four_check);
        cFive = findViewById(R.id.upgrade_five_check);
        cSix = findViewById(R.id.upgrade_six_check);


        walletTitle = findViewById(R.id.upgrade_wallet);
        buy = findViewById(R.id.upgrade_pay_btn);
        buyWallet = findViewById(R.id.upgrade_wallet_btn);


        onClicks();
        getData();

        select(1);

    }

    private void deSelect(){
        lOne.setBackgroundResource(R.drawable.item_unselected);
        pOne.setTextColor(getResources().getColor(R.color.black));
        tOne.setTextColor(getResources().getColor(R.color.black));
        cOne.setVisibility(View.INVISIBLE);

        lTwo.setBackgroundResource(R.drawable.item_unselected);
        pTwo.setTextColor(getResources().getColor(R.color.black));
        tTwo.setTextColor(getResources().getColor(R.color.black));
        cTwo.setVisibility(View.INVISIBLE);

        lThree.setBackgroundResource(R.drawable.item_unselected);
        pThree.setTextColor(getResources().getColor(R.color.black));
        tThree.setTextColor(getResources().getColor(R.color.black));
        cThree.setVisibility(View.INVISIBLE);

        lFour.setBackgroundResource(R.drawable.item_unselected);
        pFour.setTextColor(getResources().getColor(R.color.black));
        tFour.setTextColor(getResources().getColor(R.color.black));
        cFour.setVisibility(View.INVISIBLE);

        lFive.setBackgroundResource(R.drawable.item_unselected);
        pFive.setTextColor(getResources().getColor(R.color.black));
        tFive.setTextColor(getResources().getColor(R.color.black));
        cFive.setVisibility(View.INVISIBLE);

        lSix.setBackgroundResource(R.drawable.item_unselected);
        pSix.setTextColor(getResources().getColor(R.color.black));
        tSix.setTextColor(getResources().getColor(R.color.black));
        cSix.setVisibility(View.INVISIBLE);

        selectedPlan=-1;
    }

    private void select(int which){
        switch (which){
            case 1:
                deSelect();
                lOne.setBackgroundResource(R.drawable.item_selected);
                pOne.setTextColor(getResources().getColor(R.color.selected_plan));
                tOne.setTextColor(getResources().getColor(R.color.selected_plan));
                cOne.setVisibility(View.VISIBLE);
                selectedPlan=1;
                break;

            case 2:
                deSelect();
                lTwo.setBackgroundResource(R.drawable.item_selected);
                pTwo.setTextColor(getResources().getColor(R.color.selected_plan));
                tTwo.setTextColor(getResources().getColor(R.color.selected_plan));
                cTwo.setVisibility(View.VISIBLE);
                selectedPlan=2;
                break;

            case 3:
                deSelect();
                lThree.setBackgroundResource(R.drawable.item_selected);
                pThree.setTextColor(getResources().getColor(R.color.selected_plan));
                tThree.setTextColor(getResources().getColor(R.color.selected_plan));
                cThree.setVisibility(View.VISIBLE);
                selectedPlan=3;
                break;

            case 4:
                deSelect();
                lFour.setBackgroundResource(R.drawable.item_selected);
                pFour.setTextColor(getResources().getColor(R.color.selected_plan));
                tFour.setTextColor(getResources().getColor(R.color.selected_plan));
                cFour.setVisibility(View.VISIBLE);
                selectedPlan=4;
                break;

            case 5:
                deSelect();
                lFive.setBackgroundResource(R.drawable.item_selected);
                pFive.setTextColor(getResources().getColor(R.color.selected_plan));
                tFive.setTextColor(getResources().getColor(R.color.selected_plan));
                cFive.setVisibility(View.VISIBLE);
                selectedPlan=5;
                break;

            case 6:
                deSelect();
                lSix.setBackgroundResource(R.drawable.item_selected);
                pSix.setTextColor(getResources().getColor(R.color.selected_plan));
                tSix.setTextColor(getResources().getColor(R.color.selected_plan));
                cSix.setVisibility(View.VISIBLE);
                selectedPlan=6;
                break;



        }


    }

    private void onClicks(){

        lOne.setOnClickListener(View->
                select(1)
        );

        lTwo.setOnClickListener(View->
                select(2)
        );

        lThree.setOnClickListener(View->
                select(3)
        );

        lFour.setOnClickListener(View->
                select(4)
        );

        lFive.setOnClickListener(View->
                select(5)
        );

        lSix.setOnClickListener(View ->
                select(6)
        );


        buy.setOnClickListener(View-> {

                if(selectedPlan==-1){
                    MyToast.Companion.create(UpgradeActivity.this,getString(R.string.select_error));
                }else{
                    showBuyDialog(plansCost[selectedPlan-1]+"",false);
                }

        });

        buyWallet.setOnClickListener(View->{

                if(selectedPlan==-1){
                    MyToast.Companion.create(UpgradeActivity.this,getString(R.string.select_error));
                }else{
                    int cost = plansCost[selectedPlan-1]-myWallet;
                    if(cost<=0){
                        showAlertDialog();
                    }else if(cost<100){
                        showBuyDialog("100",true);
                    }else{
                        showBuyDialog(cost+"",true);
                    }
                }
            }
        );


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
        String number = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(UpgradeActivity.this,getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(UpgradeActivity.this).clear();
            startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
            UpgradeActivity.this.finish();
        }else{
            loadingDialog(false);
            RetrofitClient.Companion.getInstance().getApi()
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

                                    tOne.setText(getString(R.string.upgrade_title_model,"3"));
                                    pOne.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[0]+"")));

                                    tTwo.setText(getString(R.string.upgrade_title_model,"7"));
                                    pTwo.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[1]+"")));

                                    tThree.setText(getString(R.string.upgrade_title_model,"15"));
                                    pThree.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[2]+"")));

                                    tFour.setText(getString(R.string.upgrade_title_model,"30"));
                                    pFour.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[3]+"")));

                                    tFive.setText(getString(R.string.upgrade_title_model,"60"));
                                    pFive.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[4]+"")));

                                    tSix.setText(getString(R.string.upgrade_title_model,"90"));
                                    pSix.setText(getString(R.string.upgrade_price_model,MyUtils.Companion.moneySeparator(plansCost[5]+"")));

                                    getWallet();
                                }else{
                                    loadingDialog(true);
                                    MyToast.Companion.create(UpgradeActivity.this,getString(R.string.general_error));
                                    onBackPressed();
                                }

                            }else{
                                loadingDialog(true);
                                MyToast.Companion.create(UpgradeActivity.this,getString(R.string.general_error));
                                onBackPressed();
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Companion.create(UpgradeActivity.this,getString(R.string.general_error));
                            onBackPressed();
                        }
                    });
        }

    }
    private void getWallet(){
        String number = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(UpgradeActivity.this,getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(UpgradeActivity.this).clear();
            startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
            UpgradeActivity.this.finish();
        }else {
            RetrofitClient.Companion.getInstance().getApi()
                    .getWallet(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("ok")) {
                                   walletTitle.setText(getString(R.string.upgrade_wallet_model, MyUtils.Companion.moneySeparator(response.body().getAmount())));

                                    myWallet = Integer.parseInt(response.body().getAmount());
                                    if(myWallet==0){
                                        buyWallet.setEnabled(false);
                                    }

                                } else {
                                    MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));
                                    onBackPressed();
                                }

                            } else {
                                MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));
                                onBackPressed();
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);

                            MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));
                            onBackPressed();
                        }
                    });
        }

    }


    private void showBuyDialog(String amount,boolean useWallet) {
        BuyDialog buyDialog = new BuyDialog(UpgradeActivity.this,amount,selectedPlan,useWallet,myWallet);

        buyDialog.setCanceledOnTouchOutside(false);
        buyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        buyDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        buyDialog.show();
        Window window = buyDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        buyDialog.setOnKeyListener((DialogInterface dialog, int keyCode, KeyEvent event)-> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    buyDialog.dismiss();
                }
                return true;
        });
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog(UpgradeActivity.this, getString(R.string.upgrade_wallet_buy), (boolean isClicked)-> {
               if(MyUtils.Companion.checkInternet(UpgradeActivity.this)){
                   updateWallet();
               }else{
                   MyToast.Companion.create(UpgradeActivity.this,getString(R.string.internet_error));
               }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void updateWallet(){
        String number = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(UpgradeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(UpgradeActivity.this,getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(UpgradeActivity.this).clear();
            startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
            UpgradeActivity.this.finish();
        }else {
            loadingDialog(false);
            int plan = 0;
            switch (selectedPlan) {
                case 1:
                    plan = 3;
                    break;
                case 2:
                    plan = 7;
                    break;
                case 3:
                    plan = 15;
                    break;
                case 4:
                    plan = 30;
                    break;
                case 5:
                    plan = 60;
                    break;
                case 6:
                    plan = 90;
                    break;
            }

            String description = getString(R.string.upgrade_buy_description, plan + "");

            RetrofitBuyClient.Companion.getInstance().getApi()
                    .upgradeWallet(number, accessToken, plan, myWallet, description)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);

                            if (response.isSuccessful()) {
                                switch (response.body().getMessage()) {
                                    case "ok":
                                        MyToast.Companion.create(UpgradeActivity.this,getString(R.string.txt_buy_success));
                                        MySharedPreference.Companion.getInstance(UpgradeActivity.this).setPlan(2);
                                        UpgradeActivity.this.finish();
                                        break;

                                    case "wrongaccess":
                                        MyToast.Companion.create(UpgradeActivity.this, getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(UpgradeActivity.this).clear();
                                        startActivity(new Intent(UpgradeActivity.this, SplashActivity.class));
                                        UpgradeActivity.this.finish();
                                        break;

                                    default:
                                        MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));
                                }

                            } else {
                                MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Companion.create(UpgradeActivity.this, getString(R.string.general_error));

                        }
                    });
        }
    }
}
