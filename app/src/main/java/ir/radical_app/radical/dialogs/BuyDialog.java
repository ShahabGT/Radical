package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDialog extends Dialog {

    private Context context;
    private String amount;
    private boolean useWallet;


    public BuyDialog(@NonNull Context context,String amount,boolean useWallet) {
        super(context);
        this.context = context;
        this.amount = amount;
        this.useWallet = useWallet;
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


        tAmount.setText(context.getString(R.string.amount_model, MyUtils.moneySeparator(amount)));
        onClicks();
    }



    private void onClicks(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        giftcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        giftcodeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(giftcode.length()>2)
                    checkGiftcode(giftcode.getText().toString());
                else
                    MyToast.Create(context, context.getString(R.string.giftcode_error));

            }
        });

    }

    private void checkGiftcode(String code){
        giftcode.setEnabled(false);
        giftcodeCheck.setEnabled(false);

        String number = MySharedPreference.getInstance(context).getNumber();
        String accessToken = MySharedPreference.getInstance(context).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(context,context.getString(R.string.data_error));
            MySharedPreference.getInstance(context).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else {
            RetrofitClient.getInstance().getApi()
                    .checkGiftcode(number,accessToken,code)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                switch (response.body().getMessage()){
                                    case "valid":
                                        int percent = Integer.parseInt(response.body().getAmount());

                                        tTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                        tAmount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                                        tDiscountTitle.setVisibility(View.VISIBLE);
                                        tDiscountTitle.setText(context.getString(R.string.buy_dialog_discount_text,percent));
                                        amount = ((Integer.parseInt(amount) *(100-percent) )/100)+"";
                                        tDiscountAmount.setVisibility(View.VISIBLE);
                                        tDiscountAmount.setText(context.getString(R.string.amount_model,MyUtils.moneySeparator(amount)));
                                        break;

                                    case "invalid":
                                        MyToast.Create(context, context.getString(R.string.giftcode_error));
                                        giftcode.setEnabled(true);
                                        giftcodeCheck.setEnabled(true);
                                        break;

                                    case "wrongaccess":
                                        MyToast.Create(context,context.getString(R.string.data_error));
                                        MySharedPreference.getInstance(context).clear();
                                        context.startActivity(new Intent(context, SplashActivity.class));
                                        dismiss();
                                        break;

                                    default:
                                        giftcode.setEnabled(true);
                                        giftcodeCheck.setEnabled(true);
                                        MyToast.Create(context, context.getString(R.string.general_error));
                                }

                            } else {
                                giftcode.setEnabled(true);
                                giftcodeCheck.setEnabled(true);
                                MyToast.Create(context, context.getString(R.string.general_error));
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            MyToast.Create(context, context.getString(R.string.general_error));
                        }
                    });
        }

    }
}
