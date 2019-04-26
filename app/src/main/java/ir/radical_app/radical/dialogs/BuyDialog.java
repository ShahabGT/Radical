package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitBuyClient;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDialog extends Dialog {

    private Context context;
    private String amount;
    private int plan;
    private int walletAmount;
    private boolean useWallet;
    private String giftCode="0";


    public BuyDialog(@NonNull Context context,String amount,int plan,boolean useWallet,int walletAmount) {
        super(context);
        this.context = context;
        this.amount = amount;
        this.useWallet = useWallet;
        this.plan = plan;
        this.walletAmount = walletAmount;
    }

    private MaterialButton buy,cancel,giftcodeBtn,giftcodeCheck;
    private TextView tAmount,tTitle,tDiscountAmount,tDiscountTitle;
    private EditText giftcode;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_buy);
        init();
    }
    private void init(){
        buy = findViewById(R.id.buy_dialog_buy);
        cancel = findViewById(R.id.buy_dialog_cancel);
        giftcodeBtn = findViewById(R.id.buy_dialog_giftcode_btn);
        giftcodeCheck = findViewById(R.id.buy_dialog_giftcode_check);
        giftcode = findViewById(R.id.buy_dialog_giftcode);
        linearLayout = findViewById(R.id.buy_dialog_linear);

        tTitle = findViewById(R.id.buy_dialog_title);
        tDiscountTitle = findViewById(R.id.buy_dialog_discount_title);
        tAmount = findViewById(R.id.buy_dialog_amount);
        tDiscountAmount = findViewById(R.id.buy_dialog_discount_amount);


        tAmount.setText(context.getString(R.string.amount_model, MyUtils.Companion.moneySeparator(amount)));
        onClicks();
    }



    private void onClicks(){
        cancel.setOnClickListener(View->
                dismiss());

        giftcodeBtn.setOnClickListener((View v)-> {

                v.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

        });

        giftcodeCheck.setOnClickListener(View-> {

                if(!MyUtils.Companion.checkInternet(getContext())){
                    MyToast.Companion.create(getContext(),getContext().getString(R.string.internet_error));
                    return;
                }
                if(giftcode.length()>2) {

                    checkGiftcode(giftcode.getText().toString());
                }else
                    MyToast.Companion.create(context, context.getString(R.string.giftcode_error));


        });

        buy.setOnClickListener(View-> {

                if(MyUtils.Companion.checkInternet(context)){
                    initBuy();
                }else{
                    MyToast.Companion.create(context,context.getString(R.string.internet_error));
                }

        });

    }

    private void buyPlan(String merchant){
        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        if(number.isEmpty()){
            MyToast.Companion.create(context,context.getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(context).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else {
            if(MyUtils.Companion.checkInternet(context)){
                String url = "https://radical-app.ir/buy/buyzarin.php?merchant="+merchant;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
                dismiss();

            }else{
                MyToast.Companion.create(context,context.getString(R.string.internet_error));
            }
        }


    }

    private void initBuy(){
        buy.setEnabled(false);
        cancel.setEnabled(false);

        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(context).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(context,context.getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(context).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else {

            int selectedPlan=0;
            switch (plan){
                case 1:
                    selectedPlan=3;
                    break;
                case 2:
                    selectedPlan=7;
                    break;
                case 3:
                    selectedPlan=15;
                    break;
                case 4:
                    selectedPlan=30;
                    break;
                case 5:
                    selectedPlan=60;
                    break;
                case 6:
                    selectedPlan=90;
                    break;
            }
            int wallet=0;

            if(useWallet){
                wallet=walletAmount;
            }

            String description=context.getString(R.string.upgrade_buy_description,selectedPlan+"");

            RetrofitBuyClient.Companion.getInstance().getApi()
                    .upgrade(number,accessToken,selectedPlan,giftCode,wallet,description,amount)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            buy.setEnabled(true);
                            cancel.setEnabled(true);
                            if(response.isSuccessful()){
                                switch (response.body().getMessage()){
                                    case "ok":
                                        String merchant=response.body().getMerchant();
                                        buyPlan(merchant);
                                        break;

                                    case "wrongaccess":
                                        MyToast.Companion.create(context,context.getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        context.startActivity(new Intent(context, SplashActivity.class));
                                        dismiss();
                                        break;

                                        default:
                                            MyToast.Companion.create(context, context.getString(R.string.general_error));
                                }

                            }else{
                                MyToast.Companion.create(context, context.getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            buy.setEnabled(true);
                            cancel.setEnabled(true);
                            MyToast.Companion.create(context, context.getString(R.string.general_error));

                        }
                    });


        }
    }

    private void checkGiftcode(String code){
        giftcode.setEnabled(false);
        giftcodeCheck.setEnabled(false);

        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(context).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(context,context.getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(context).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else {
            RetrofitClient.Companion.getInstance().getApi()
                    .checkGiftcode(number,accessToken,code)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                switch (response.body().getMessage()){
                                    case "valid":
                                        int percent = Integer.parseInt(response.body().getAmount());
                                        giftCode=code;
                                        tTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                        tAmount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                                        tDiscountTitle.setVisibility(View.VISIBLE);
                                        tDiscountTitle.setText(context.getString(R.string.buy_dialog_discount_text,percent));
                                        amount = ((Integer.parseInt(amount) *(100-percent) )/100)+"";
                                        tDiscountAmount.setVisibility(View.VISIBLE);
                                        tDiscountAmount.setText(context.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(amount)));
                                        break;

                                    case "invalid":
                                        MyToast.Companion.create(context, context.getString(R.string.giftcode_error));
                                        giftcode.setEnabled(true);
                                        giftcodeCheck.setEnabled(true);
                                        break;

                                    case "wrongaccess":
                                        MyToast.Companion.create(context,context.getString(R.string.data_error));
                                        MySharedPreference.Companion.getInstance(context).clear();
                                        context.startActivity(new Intent(context, SplashActivity.class));
                                        dismiss();
                                        break;

                                    default:
                                        giftcode.setEnabled(true);
                                        giftcodeCheck.setEnabled(true);
                                        MyToast.Companion.create(context, context.getString(R.string.general_error));
                                }

                            } else {
                                giftcode.setEnabled(true);
                                giftcodeCheck.setEnabled(true);
                                MyToast.Companion.create(context, context.getString(R.string.general_error));
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            MyToast.Companion.create(context, context.getString(R.string.general_error));
                        }
                    });
        }

    }
}
