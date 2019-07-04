package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.BuyDetailsItem;
import ir.radical_app.radical.models.BuyDetailsModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptDialog extends Dialog {

    private Context context;
    private TextView tTotal,tDescount,tPay,tShopname,tTitle,tTitle2;
    private ArrayList<String> discountPercents ;
    private ArrayList<String> discountTitles ;
    private ArrayList<String> prices;
    private ArrayList<String> pricesDiscounts;
    private String shopName;
    private String shopBuyId;
    private int amount,pay,discount;
    private boolean fromHistory=false;
    private LoadingDialog dialog;
    private LottieAnimationView animationView;
    private MaterialCardView cardView;



    public void setDiscountPercents(ArrayList<String> discountPercents) {
        this.discountPercents = discountPercents;
    }
    public void setDiscountTitles(ArrayList<String> discountTitles) {
        this.discountTitles = discountTitles;
    }
    public void setPrices(ArrayList<String> prices) {
        this.prices = prices;
    }
    public void setPricesDiscounts(ArrayList<String> pricesDiscounts) {
        this.pricesDiscounts = pricesDiscounts;
    }

    public void setFromHistory(boolean fromHistory) {
        this.fromHistory = fromHistory;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public void setShopBuyId(String shopBuyId) {
        this.shopBuyId = shopBuyId;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setPay(int pay) {
        this.pay = pay;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public ReceiptDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receipt);
        init();
    }

    private void init(){
        cardView = findViewById(R.id.receipt_card1);
        animationView = findViewById(R.id.ok_anim);
        tTitle = findViewById(R.id.receipt_title);
        tTitle2 = findViewById(R.id.receipt_title2);
        tTotal = findViewById(R.id.receipt_total);
        tDescount = findViewById(R.id.receipt_discount);
        tShopname = findViewById(R.id.receipt_shop_title);
        tPay = findViewById(R.id.receipt_pay);

        if(!fromHistory){
            setData();
        }else{
            cardView.setVisibility(View.GONE);
            getData();
        }

    }
    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();
        }else{
            dialog.dismiss();
        }
    }

    private void getData(){
        loadingDialog(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),context.getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .getBuyDetails(number,accessToken,shopBuyId)
                    .enqueue(new Callback<BuyDetailsModel>() {
                        @Override
                        public void onResponse(Call<BuyDetailsModel> call, Response<BuyDetailsModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                switch (response.body().getMessage()){
                                    case "ok":
                                        List<BuyDetailsItem> list = response.body().getData();
                                        discountPercents = new ArrayList<>();
                                        discountTitles = new ArrayList<>();
                                        prices = new ArrayList<>();
                                        pricesDiscounts = new ArrayList<>();
                                        for(int i =0;i<list.size();i++){
                                            BuyDetailsItem model = list.get(i);
                                            discountPercents.add(model.getDiscountPercent());
                                            discountTitles.add(model.getDiscountTitle());
                                            prices.add(model.getPrice());
                                            pricesDiscounts.add(model.getPriceDiscount());
                                        }
                                        setData();

                                        break;

                                    case "wrongaccess":
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        context.startActivity(new Intent(context, SplashActivity.class));
                                        dismiss();
                                        break;


                                        default:
                                            MyToast.Companion.create(getContext(),context.getString(R.string.general_error));
                                            dismiss();
                                }


                            }else{
                                loadingDialog(true);
                                MyToast.Companion.create(getContext(),context.getString(R.string.general_error));
                                dismiss();
                            }


                        }

                        @Override
                        public void onFailure(Call<BuyDetailsModel> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Companion.create(getContext(),context.getString(R.string.general_error));
                            dismiss();

                        }
                    });

        }



    }

    private void setData(){
        tTitle.setText(context.getString(R.string.receipt_ok));
        tPay.setText(context.getString(R.string.amount_model, MyUtils.Companion.moneySeparator(pay+"")));
        tDescount.setText(context.getString(R.string.amount_model, MyUtils.Companion.moneySeparator(discount+"")));
        if(shopName.startsWith("فروشگاه"))
            shopName=shopName.replaceFirst("فروشگاه","");
        tShopname.setText(shopName.trim());
        tTotal.setText(context.getString(R.string.amount_model, MyUtils.Companion.moneySeparator(amount+"")));
        makeTable();
    }

    private void makeTable(){
        TableLayout myTable = findViewById(R.id.receipt_table);
        Typeface font = ResourcesCompat.getFont(context,R.font.vazir);

        int row = 1;
        for(int i = 0;i<discountTitles.size();i++){
            TableRow newRow = new TableRow(context);
            if(!pricesDiscounts.get(i).equals("0")) {

                TextView t4 = new TextView(context);
                t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                t4.setText(MyUtils.Companion.moneySeparator(pricesDiscounts.get(i)));
                t4.setTypeface(font);
                t4.setBackgroundResource(R.drawable.cell_shape2);

                t4.setGravity(Gravity.CENTER);
                newRow.addView(t4);

                TextView t3 = new TextView(context);
                t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));

                t3.setText(MyUtils.Companion.moneySeparator(prices.get(i) ));
                t3.setTypeface(font);
                t3.setBackgroundResource(R.drawable.cell_shape2);

                t3.setGravity(Gravity.CENTER);

                newRow.addView(t3);

                TextView t2 = new TextView(context);
                t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));

                t2.setText(discountTitles.get(i));
                t2.setTypeface(font);
                t2.setBackgroundResource(R.drawable.cell_shape2);

                t2.setGravity(Gravity.CENTER);

                newRow.addView(t2);

                TextView t1 = new TextView(context);
                t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));

                t1.setText(String.valueOf(row++));
                t1.setTypeface(font);
                t1.setBackgroundResource(R.drawable.cell_shape2);

                t1.setGravity(Gravity.CENTER);

                newRow.addView(t1);
            }
            myTable.addView(newRow);
        }
    }
}


