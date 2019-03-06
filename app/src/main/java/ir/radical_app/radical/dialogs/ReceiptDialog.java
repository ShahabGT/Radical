package ir.radical_app.radical.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

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
    private TextView tTotal,tDescount,tPay,tShopname,tTitle;
    private ArrayList<String> discountPercents ;
    private ArrayList<String> discountTitles ;
    private ArrayList<String> prices;
    private ArrayList<String> pricesDiscounts;
    private String shopName;
    private String shopBuyId;
    private int amount,pay,discount;
    private boolean fromHistory=false;
    private LoadingDialog dialog;



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

        tTitle = findViewById(R.id.receipt_title);
        tTotal = findViewById(R.id.receipt_total);
        tDescount = findViewById(R.id.receipt_discount);
        tShopname = findViewById(R.id.receipt_shop_title);
        tPay = findViewById(R.id.receipt_pay);

        if(!fromHistory){
            setData();
        }else{
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
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),context.getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            context.startActivity(new Intent(context, SplashActivity.class));
            dismiss();
        }else{
            RetrofitClient.getInstance().getApi()
                    .getBuyDetails(number,accessToken,shopBuyId)
                    .enqueue(new Callback<BuyDetailsModel>() {
                        @Override
                        public void onResponse(Call<BuyDetailsModel> call, Response<BuyDetailsModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                switch (response.body().getMessage()){
                                    case "ok":
                                        ArrayList<BuyDetailsItem> list = response.body().getData();
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
                                        MySharedPreference.getInstance(getContext()).clear();
                                        context.startActivity(new Intent(context, SplashActivity.class));
                                        dismiss();
                                        break;


                                        default:
                                            MyToast.Create(getContext(),context.getString(R.string.general_error));
                                            dismiss();
                                }


                            }else{
                                loadingDialog(true);
                                MyToast.Create(getContext(),context.getString(R.string.general_error));
                                dismiss();
                            }


                        }

                        @Override
                        public void onFailure(Call<BuyDetailsModel> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Create(getContext(),context.getString(R.string.general_error));
                            dismiss();

                        }
                    });

        }



    }

    private void setData(){
        tTitle.setText(context.getString(R.string.receipt_ok));
        tPay.setText(context.getString(R.string.amount_model, MyUtils.moneySeparator(pay+"")));
        tDescount.setText(context.getString(R.string.amount_model, MyUtils.moneySeparator(discount+"")));
        if(shopName.startsWith("فروشگاه"))
            shopName=shopName.replaceFirst("فروشگاه","");
        tShopname.setText(context.getString(R.string.receipt_shop,shopName.trim()));
        tTotal.setText(context.getString(R.string.amount_model, MyUtils.moneySeparator(amount+"")));
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
                t4.setText(MyUtils.moneySeparator(pricesDiscounts.get(i)));
                t4.setTypeface(font);
                t4.setBackgroundResource(R.drawable.cell_shape2);

                t4.setGravity(Gravity.CENTER);
                newRow.addView(t4);

                TextView t3 = new TextView(context);
                t3.setText(MyUtils.moneySeparator(prices.get(i) ));
                t3.setTypeface(font);
                t3.setBackgroundResource(R.drawable.cell_shape2);

                t3.setGravity(Gravity.CENTER);

                newRow.addView(t3);

                TextView t2 = new TextView(context);
                t2.setText(discountTitles.get(i));
                t2.setTypeface(font);
                t2.setBackgroundResource(R.drawable.cell_shape2);

                t2.setGravity(Gravity.CENTER);

                newRow.addView(t2);

                TextView t1 = new TextView(context);
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


