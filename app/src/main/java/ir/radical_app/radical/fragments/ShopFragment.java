package ir.radical_app.radical.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import ir.radical_app.radical.activities.QrcodeActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.ShopsImagesAdapter;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.classes.PermissionUtil;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.PlanData;
import ir.radical_app.radical.models.ShopDetailsModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopFragment extends Fragment {
    private final int CAMERA_REQUEST_CODE = 658;
    private final int QR_REQUEST_CODE = 668;
    private String shopId;

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    private LoadingDialog dialog;
    private ShopsImagesAdapter adapter;
    private ScaleRatingBar rate;
    private boolean fromuser=true;

    private TextView name,category,description,address,tel,open,rateTtile,buy;
    private ImageView telegram,instagram,website,bookmark,share;
    private String telegramID,instagramID,websiteID,bookmarkStat;

    private TextView bronzeDiscount1,bronzeDiscount2,bronzeDiscount3,bronzeDiscount4,bronzeDiscount5;
    private TextView diamondDiscount1,diamondDiscount2,diamondDiscount3,diamondDiscount4,diamondDiscount5;
    private TextView diamondDiscount1title,diamondDiscount2title,diamondDiscount3title,diamondDiscount4title,diamondDiscount5title;

    private View globalView;

    public ShopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_shop, container, false);

        globalView=v;

        init(v);

        return v;
    }
    private void init(View v){
        name= v.findViewById(R.id.shop_name);
        category= v.findViewById(R.id.shop_category);
        description= v.findViewById(R.id.shop_description);
        address= v.findViewById(R.id.shop_address);
        tel= v.findViewById(R.id.shop_tel);
        open= v.findViewById(R.id.shop_open);
        rateTtile = v.findViewById(R.id.shop_rate_title);
        buy=v.findViewById(R.id.shop_buy);

        rate = v.findViewById(R.id.shop_RatingBar);
        telegram = v.findViewById(R.id.shop_telegram);
        instagram = v.findViewById(R.id.shop_instagram);
        website = v.findViewById(R.id.shop_website);
        bookmark = v.findViewById(R.id.shop_bookmark);
        share = v.findViewById(R.id.shop_share);

        bronzeDiscount1 = v.findViewById(R.id.shop_bronze_discount1);
        bronzeDiscount2 = v.findViewById(R.id.shop_bronze_discount2);
        bronzeDiscount3 = v.findViewById(R.id.shop_bronze_discount3);
        bronzeDiscount4 = v.findViewById(R.id.shop_bronze_discount4);
        bronzeDiscount5 = v.findViewById(R.id.shop_bronze_discount5);

        diamondDiscount1 = v.findViewById(R.id.shop_diamond_discount1);
        diamondDiscount1title = v.findViewById(R.id.shop_diamond_discount1_title1);
        diamondDiscount2 = v.findViewById(R.id.shop_diamond_discount2);
        diamondDiscount2title = v.findViewById(R.id.shop_diamond_discount1_title2);
        diamondDiscount3 = v.findViewById(R.id.shop_diamond_discount3);
        diamondDiscount3title = v.findViewById(R.id.shop_diamond_discount1_title3);
        diamondDiscount4 = v.findViewById(R.id.shop_diamond_discount4);
        diamondDiscount4title = v.findViewById(R.id.shop_diamond_discount1_title4);
        diamondDiscount5 = v.findViewById(R.id.shop_diamond_discount5);
        diamondDiscount5title = v.findViewById(R.id.shop_diamond_discount1_title5);


        getData();
        onClicks();
    }
    private void rateListener(){
        rate.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                if(fromuser) {
                    updateRate(v + "");
                    MyToast.Create(getContext(),"امتیاز شما: "+v);
                }
                fromuser=true;
            }
        });
    }
    private void onClicks() {
        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("tel:"+tel.getText().toString());
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("https://instagram.com/"+instagramID);
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("https://t.me/"+telegramID);

            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction(websiteID);

            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookmark();
                if(bookmarkStat.equals("0")){
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));
                    bookmarkStat="1";

                }else{
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
                    bookmarkStat="0";
                }

            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCamera();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.shareCode(getActivity(),getString(R.string.shop_share,name.getText().toString(),"https://radical-app.ir/shops?id="+shopId));
            }
        });

    }
    private void intentAction(String id){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(id));
        startActivity(intent);
    }
    private void loadingDialog(boolean cancel) {
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
        if(shopId==null)
            shopId = "";

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .getShopDetails(shopId,number,accessToken)
                    .enqueue(new Callback<ShopDetailsModel>() {
                        @Override
                        public void onResponse(Call<ShopDetailsModel> call, Response<ShopDetailsModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                switch (response.body().getData().getMessage()){
                                    case "ok":
                                        parseData(response.body());
                                        break;

                                    default:
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
    private void initViewPager(int size){
        UltraViewPager ultraViewPager = globalView.findViewById(R.id.shop_images_pager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        adapter = new ShopsImagesAdapter(getContext(),shopId,size);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(0xFFFCD736)
                .setNormalColor(0xFFECEFF1)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().setMargin(0,0,0,16);
        ultraViewPager.getIndicator().build();
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAutoScroll(3000);
    }
    private void parseData(ShopDetailsModel response){
        shopId = response.getData().getShopId();
        name.setText(response.getData().getName());
        category.setText(response.getData().getCategoryName());
        address.setText(response.getData().getAddress());
        description.setText(response.getData().getDescription());
        tel.setText(response.getData().getTel());
        open.setText(response.getData().getOpen().trim());
        telegramID = response.getData().getTelegram();
        if(telegramID.isEmpty())
            telegram.setVisibility(View.GONE);
        instagramID = response.getData().getInstagram();
        if(instagramID.isEmpty())
            instagram.setVisibility(View.GONE);
        websiteID = response.getData().getWebsite();
        if(websiteID.isEmpty())
            website.setVisibility(View.GONE);

        bookmarkStat = response.getData().getBookmark();
        if(bookmarkStat.equals("0"))
            bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
        else
            bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));

        int count = Integer.parseInt(response.getData().getCount()+"");
        float sum = Float.parseFloat(response.getData().getSum()+"");
        if(count!=0 || sum!=0){
            float mean = sum /count;
            rate.setRating(mean);
            rateTtile.setText(getString(R.string.shop_rate_title,String.valueOf(mean).substring(0,3),count));
        }

        initViewPager(response.getData().getPicNum()-1);


        ArrayList<PlanData> list = response.getPlans();
        switch (list.size()){
            case 5:
                String des5 = list.get(4).getDescription();
                String b5 = list.get(4).getPlanBronze();
                String d5 = list.get(4).getPlanDiamond();
                diamondDiscount5.setVisibility(View.VISIBLE);
                diamondDiscount5.setText(d5+"%");
                diamondDiscount5title.setVisibility(View.VISIBLE);
                diamondDiscount5title.setText(des5);
                bronzeDiscount5.setVisibility(View.VISIBLE);
                bronzeDiscount5.setText(b5+"%");

            case 4:
                String des4 = list.get(3).getDescription();
                String b4 = list.get(3).getPlanBronze();
                String d4 = list.get(3).getPlanDiamond();
                diamondDiscount4.setVisibility(View.VISIBLE);
                diamondDiscount4.setText(d4+"%");
                diamondDiscount4title.setVisibility(View.VISIBLE);
                diamondDiscount4title.setText(des4);
                bronzeDiscount4.setVisibility(View.VISIBLE);
                bronzeDiscount4.setText(b4+"%");

            case 3:
                String des3 = list.get(2).getDescription();
                String b3 = list.get(2).getPlanBronze();
                String d3 = list.get(2).getPlanDiamond();
                diamondDiscount3.setVisibility(View.VISIBLE);
                diamondDiscount3.setText(d3+"%");
                diamondDiscount3title.setVisibility(View.VISIBLE);
                diamondDiscount3title.setText(des3);
                bronzeDiscount3.setVisibility(View.VISIBLE);
                bronzeDiscount3.setText(b3+"%");

            case 2:
                String des2 = list.get(1).getDescription();
                String b2 = list.get(1).getPlanBronze();
                String d2 = list.get(1).getPlanDiamond();
                diamondDiscount2.setVisibility(View.VISIBLE);
                diamondDiscount2.setText(d2+"%");
                diamondDiscount2title.setVisibility(View.VISIBLE);
                diamondDiscount2title.setText(des2);
                bronzeDiscount2.setVisibility(View.VISIBLE);
                bronzeDiscount2.setText(b2+"%");

            case 1:
                String des = list.get(0).getDescription();
                String b = list.get(0).getPlanBronze();
                String d = list.get(0).getPlanDiamond();
                diamondDiscount1.setVisibility(View.VISIBLE);
                diamondDiscount1.setText(d+"%");
                diamondDiscount1title.setVisibility(View.VISIBLE);
                diamondDiscount1title.setText(des);
                bronzeDiscount1.setVisibility(View.VISIBLE);
                bronzeDiscount1.setText(b+"%");

        }
        rateListener();
    }
    private void updateBookmark(){
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();
        if(shopId==null)
            shopId = "";


        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .updateBookmark(shopId,number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                            if(response.isSuccessful()){

                                if(response.body().getMessage().equals("inserted")){
                                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));
                                    bookmarkStat="1";
                                }else if(response.body().getMessage().equals("deleted")){
                                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
                                    bookmarkStat="0";
                                }

                            }else{
                                if(bookmarkStat.equals("0")){
                                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));
                                    bookmarkStat="1";

                                }else{
                                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
                                    bookmarkStat="0";
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            if(bookmarkStat.equals("0")){
                                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));
                                bookmarkStat="1";

                            }else{
                                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
                                bookmarkStat="0";
                            }
                        }
                    });

        }
    }
    private void updateRate(String myRate){
        rate.setEnabled(false);

        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();
        if(shopId==null)
            shopId = "";
        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .updateRate(shopId,number,accessToken,myRate)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            rate.setEnabled(true);

                            if(response.isSuccessful()){
                                if(response.body().getMessage().equals("ok")){
                                    int count = Integer.parseInt(response.body().getCount()+"");
                                    float sum = Float.parseFloat(response.body().getSum()+"");
                                    float mean = sum/count;
                                    fromuser=false;
                                    rate.setRating(mean);
                                    rateTtile.setText(getString(R.string.shop_rate_title,mean+"",count));
                                }

                            }
                        }
                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            rate.setEnabled(true);
                        }
                    });

        }
    }

    private int checkPermission(){
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
    }
    private void showExplanation(){
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.permission_camera));
        builder.setMessage(getString(R.string.permission_camera_message));
        builder.setPositiveButton(getString(R.string.permission_allow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });
        builder.setNegativeButton(getString(R.string.permission_dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }
    private void requestCamera(){
        PermissionUtil permissionUtil = new PermissionUtil(getActivity());

        if(checkPermission()!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)){
                showExplanation();
            }

            else if(!permissionUtil.check("camera")){
                requestPermission();
                permissionUtil.update("camera");
            }else{
                MyToast.Create(getContext(),getString(R.string.permission_camera_toast));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getActivity().getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }
        }else{
            readQr();
        }

    }
    private void readQr() {
        getActivity().startActivityForResult(new Intent(getContext(), QrcodeActivity.class),QR_REQUEST_CODE);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode== QR_REQUEST_CODE){
//            if(resultCode== Activity.RESULT_OK){
//                BuyFragment buyFragment = new BuyFragment();
//                buyFragment.setQrId( data.getStringExtra("qrCode"));
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .add(R.id.main_container,buyFragment).addToBackStack(null).commit();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    readQr();
                }
        }
    }

}
