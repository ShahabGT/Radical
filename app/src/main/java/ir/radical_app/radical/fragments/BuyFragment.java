package ir.radical_app.radical.fragments;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.StringTokenizer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.dialogs.ReceiptDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.PlanData;
import ir.radical_app.radical.models.ShopData;
import ir.radical_app.radical.models.ShopDetailsModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BuyFragment extends Fragment {

    private LoadingDialog dialog;
    private EditText discount1,discount2,discount3,discount4,discount5;
    private TextView discountAmount1,discountAmount2,discountAmount3,discountAmount4,discountAmount5;
    private TextView discountTitle1,discountTitle2,discountTitle3,discountTitle4,discountTitle5;
    private String qrId;

    private SoundPool soundPool;
    private int sound;

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    private String shopId;

    private SimpleDraweeView shopImage;
    private Button buy;
    private TextView shopName;
    private CardView card1,card2,card3,card4,card5;

    private ArrayList<String> discountPercents = new ArrayList<>();
    private ArrayList<String> discountTitles = new ArrayList<>();
    private ArrayList<String> prices= new ArrayList<>();
    private ArrayList<String> pricesDiscounts= new ArrayList<>();
    private int amount=0,pay=0,discount;


    public BuyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_buy, container, false);
        init(v);
        getData();

        return v;
    }

    private void init(View v){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(2)
                    .build();
        }else {
            soundPool = new SoundPool(2, AudioManager.STREAM_ALARM,0);
        }
        sound = soundPool.load(getActivity(),R.raw.notification,1);
        shopImage = v.findViewById(R.id.buy_shop_image);
        shopName = v.findViewById(R.id.buy_shop_title);
        buy=v.findViewById(R.id.buy_footer_buy);

        card1 = v.findViewById(R.id.shop_price_card1);
        card2 = v.findViewById(R.id.shop_price_card2);
        card3 = v.findViewById(R.id.shop_price_card3);
        card4 = v.findViewById(R.id.shop_price_card4);
        card5 = v.findViewById(R.id.shop_price_card5);

        discount1 = v.findViewById(R.id.buy_discount1_amount);
        discountTitle1 = v.findViewById(R.id.buy_discount1_title1);
        discountAmount1 = v.findViewById(R.id.buy_discount1_discount);

        discount2 = v.findViewById(R.id.buy_discount2_amount);
        discountTitle2 = v.findViewById(R.id.buy_discount2_title1);
        discountAmount2 = v.findViewById(R.id.buy_discount2_discount);

        discount3 = v.findViewById(R.id.buy_discount3_amount);
        discountTitle3 = v.findViewById(R.id.buy_discount3_title1);
        discountAmount3 = v.findViewById(R.id.buy_discount3_discount);

        discount4 = v.findViewById(R.id.buy_discount4_amount);
        discountTitle4 = v.findViewById(R.id.buy_discount4_title1);
        discountAmount4 = v.findViewById(R.id.buy_discount4_discount);

        discount5 = v.findViewById(R.id.buy_discount5_amount);
        discountTitle5 = v.findViewById(R.id.buy_discount5_title1);
        discountAmount5 = v.findViewById(R.id.buy_discount5_discount);


        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUtils.checkInternet(getContext())){
                    if(
                            !discount1.getText().toString().isEmpty()||
                                    !discount2.getText().toString().isEmpty()||
                                    !discount3.getText().toString().isEmpty()||
                                    !discount4.getText().toString().isEmpty()||
                                    !discount5.getText().toString().isEmpty()
                    )
                        sendData();
                    else
                        MyToast.Create(getContext(),getString(R.string.fillbuy_error));
                }
                else
                    MyToast.Create(getContext(),getString(R.string.internet_error));
            }
        });

    }
    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

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
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else if (qrId==null || qrId.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.general_error));
            getActivity().finish();

        }else{
            RetrofitClient.getInstance().getApi()
                    .getShopQr(qrId,number,accessToken)
                    .enqueue(new Callback<ShopDetailsModel>() {
                        @Override
                        public void onResponse(Call<ShopDetailsModel> call, Response<ShopDetailsModel> response) {
                            if(response.isSuccessful()){

                                switch (response.body().getData().getMessage()){
                                    case "ok":
                                        parseData(response.body());
                                        soundPool.play(sound, 0.99f, 0.99f, 1, 0, 0.99f);

                                        break;

                                    case "wrongqr":
                                        loadingDialog(true);
                                        MyToast.Create(getContext(),getString(R.string.qr_error));
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                        break;

                                    case "wrongaccess":
                                        loadingDialog(true);
                                        MyToast.Create(getContext(),getString(R.string.data_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getContext(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        loadingDialog(true);
                                        MyToast.Create(getContext(),getString(R.string.general_error));
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }


                            }else{
                                MyToast.Create(getContext(),getString(R.string.general_error));
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }

                        }

                        @Override
                        public void onFailure(Call<ShopDetailsModel> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Create(getContext(),getString(R.string.general_error));
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                        }
                    });
        }
    }
    private TextWatcher moneyWatcher(EditText editText, double discountPercent, TextView discountTitle){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try
                {
                    editText.removeTextChangedListener(this);
                    String value = editText.getText().toString();
                    if (!value.equals(""))
                    {
                        if(value.startsWith(".")){
                            editText.setText("0.");
                        }
                        if(value.startsWith("0") && !value.startsWith("0.")){
                            editText.setText("");
                        }
                        String str = editText.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            editText.setText(getDecimalFormattedString(str));
                        editText.setSelection(editText.getText().toString().length());
                    }
                    String res =Math.round(Integer.parseInt(s.toString().replaceAll(",",""))*discountPercent)+"";
                    discountTitle.setText(getDecimalFormattedString(res));
                    editText.addTextChangedListener(this);
                }
                catch (Exception ex)
                {
                    discountTitle.setText("0");
                    editText.addTextChangedListener(this);
                }
            }
        };
    }
    private String getDecimalFormattedString(String value){
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1)
        {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt( -1 + str1.length()) == '.')
        {
            j--;
            str3 = ".";
        }
        for (int k = j;; k--)
        {
            if (k < 0)
            {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3)
            {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }

    }
    private void parseData(ShopDetailsModel response){
        ShopData data = response.getData();
        shopId = data.getShopId();
        ArrayList<PlanData> plans = response.getPlans();
        Uri uri = Uri.parse(getString(R.string.main_image_url,data.getShopId()));
        shopImage.setImageURI(uri);
        shopName.setText(data.getName());

        switch (plans.size()){
            case 5:
                discountTitles.add(plans.get(4).getDescription());
                discountPercents.add(plans.get(4).getPlan());
                discountTitle5.setText(plans.get(4).getDescription());
                float di5 =(float) (100 - Integer.parseInt(plans.get(4).getPlan()))/100;
                discount5.addTextChangedListener(moneyWatcher(discount5,di5,discountAmount5));
                card5.setVisibility(View.VISIBLE);
            case 4:
                discountTitles.add(plans.get(3).getDescription());
                discountPercents.add(plans.get(3).getPlan());
                discountTitle4.setText(plans.get(3).getDescription());
                float di4 =(float) (100 - Integer.parseInt(plans.get(3).getPlan()))/100;
                discount4.addTextChangedListener(moneyWatcher(discount4,di4,discountAmount4));
                card4.setVisibility(View.VISIBLE);
            case 3:
                discountTitles.add(plans.get(2).getDescription());
                discountPercents.add(plans.get(2).getPlan());
                discountTitle3.setText(plans.get(2).getDescription());
                float di3 =(float) (100 - Integer.parseInt(plans.get(2).getPlan()))/100;
                discount3.addTextChangedListener(moneyWatcher(discount3,di3,discountAmount3));
                card3.setVisibility(View.VISIBLE);
            case 2:
                discountTitles.add(plans.get(1).getDescription());
                discountPercents.add(plans.get(1).getPlan());
                discountTitle2.setText(plans.get(1).getDescription());
                float di2 =(float) (100 - Integer.parseInt(plans.get(1).getPlan()))/100;
                discount2.addTextChangedListener(moneyWatcher(discount2,di2,discountAmount2));
                card2.setVisibility(View.VISIBLE);
            case 1:
                discountTitles.add(plans.get(0).getDescription());
                discountPercents.add(plans.get(0).getPlan());
                discountTitle1.setText(plans.get(0).getDescription());
                float di =(float) (100 - Integer.parseInt(plans.get(0).getPlan()))/100;
                discount1.addTextChangedListener(moneyWatcher(discount1,di,discountAmount1));
                card1.setVisibility(View.VISIBLE);
        }
        loadingDialog(true);
    }
    private void sendData(){
        loadingDialog(false);
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{

            switch (discountPercents.size()) {
                case 5:
                    prices.add(discount5.getText().toString().replace(",",""));
                    pricesDiscounts.add(discountAmount5.getText().toString().replace(",",""));
                case 4:
                    prices.add(discount4.getText().toString().replace(",",""));
                    pricesDiscounts.add(discountAmount4.getText().toString().replace(",",""));
                case 3:
                    prices.add(discount3.getText().toString().replace(",",""));
                    pricesDiscounts.add(discountAmount3.getText().toString().replace(",",""));
                case 2:
                    prices.add(discount2.getText().toString().replace(",",""));
                    pricesDiscounts.add(discountAmount2.getText().toString().replace(",",""));
                case 1:
                    prices.add(discount1.getText().toString().replace(",",""));
                    pricesDiscounts.add(discountAmount1.getText().toString().replace(",",""));


            }

            for(int i=0;i<prices.size();i++){
                if(!prices.get(i).isEmpty())
                    amount += Integer.parseInt(prices.get(i));
                if(!pricesDiscounts.get(i).isEmpty())
                    pay += Integer.parseInt(pricesDiscounts.get(i));
            }
            discount = amount - pay;
            RetrofitClient.getInstance().getApi()
                    .buyFromShop(number,accessToken,shopId,amount+"",discount+"",pay+"",prices,pricesDiscounts,discountPercents,discountTitles)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);

                            if(response.isSuccessful()){

                                switch (response.body().getMessage()){
                                    case "ok":
                                        showReceiptDialog();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.data_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getContext(), SplashActivity.class));
                                       getActivity().finish();
                                        break;

                                    default:
                                        MyToast.Create(getContext(),getString(R.string.general_error));


                                }
                            }else{
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Create(getContext(),getString(R.string.general_error));
                        }
                    });


        }
    }
    private void showReceiptDialog() {
        ReceiptDialog receiptDialog = new ReceiptDialog(getContext());
        receiptDialog.setAmount(amount);
        receiptDialog.setDiscount(discount);
        receiptDialog.setDiscountPercents(discountPercents);
        receiptDialog.setDiscountTitles(discountTitles);
        receiptDialog.setPay(pay);
        receiptDialog.setPrices(prices);
        receiptDialog.setPricesDiscounts(pricesDiscounts);
        receiptDialog.setShopName(shopName.getText().toString());
        receiptDialog.setCanceledOnTouchOutside(false);
        receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        receiptDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        receiptDialog.show();
        Window window = receiptDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        receiptDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    while(fm.getBackStackEntryCount()>1)
                        fm.popBackStackImmediate();

                    receiptDialog.dismiss();
                }
                return true;
            }
        });
    }
}
