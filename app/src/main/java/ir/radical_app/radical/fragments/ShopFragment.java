package ir.radical_app.radical.fragments;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.MaptexError;
import ir.map.sdk_services.models.base.ResponseListener;
import ir.radical_app.radical.activities.CommentsActivity;
import ir.radical_app.radical.activities.MapActivity;
import ir.radical_app.radical.activities.QrcodeActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.ShopsImagesAdapter;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.PlanData;
import ir.radical_app.radical.models.ShopDetailsModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopFragment extends Fragment implements ResponseListener<Bitmap> {
    private final int CAMERA_REQUEST_CODE = 658;
    private final int QR_REQUEST_CODE = 668;
    private String shopId;

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    private LoadingDialog dialog;
    private ShopsImagesAdapter adapter;
    private ScaleRatingBar rate;
    private boolean fromuser=true,liked=false,disliked=false;

    private TextView name,category,description,address,tel,open,rateTtile,buy,likeCount,dislikeCount,commentsCount;
    private ImageView telegram,instagram,website,bookmark,share,like,dislike,comment;
    private String telegramID,instagramID,websiteID,bookmarkStat;

    private TextView discount1, discount2, discount3, discount4, discount5;
    private TextView discount1title, discount2title, discount3title, discount4title, discount5title;
    private ConstraintLayout discount1layout,discount2layout,discount3layout,discount4layout,discount5layout;
    private View discount1line,discount2line,discount3line,discount4line;

    private View globalView;

    private int maxDiscount;
    private String categoryId;
    private ImageView staticMap;

    public ShopFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        staticMap = v.findViewById(R.id.shop_static_map);

        rate = v.findViewById(R.id.shop_RatingBar);
        telegram = v.findViewById(R.id.shop_telegram);
        instagram = v.findViewById(R.id.shop_instagram);
        website = v.findViewById(R.id.shop_website);
        bookmark = v.findViewById(R.id.shop_bookmark);
        share = v.findViewById(R.id.shop_share);
        like = v.findViewById(R.id.shop_like_img);
        dislike = v.findViewById(R.id.shop_dislike_img);
        comment = v.findViewById(R.id.shop_comments_img);

        likeCount = v.findViewById(R.id.shop_likes);
        dislikeCount = v.findViewById(R.id.shop_dislikes);
        commentsCount = v.findViewById(R.id.shop_comments);

        discount1 = v.findViewById(R.id.shop_discount1);
        discount1title = v.findViewById(R.id.shop_discount1_title);
        discount1layout = v.findViewById(R.id.shop_discount1_layout);
        discount1line = v.findViewById(R.id.shop_discount1_line);

        discount2 = v.findViewById(R.id.shop_discount2);
        discount2title = v.findViewById(R.id.shop_discount2_title);
        discount2layout = v.findViewById(R.id.shop_discount2_layout);
        discount2line = v.findViewById(R.id.shop_discount2_line);


        discount3 = v.findViewById(R.id.shop_discount3);
        discount3title = v.findViewById(R.id.shop_discount3_title);
        discount3layout = v.findViewById(R.id.shop_discount3_layout);
        discount3line = v.findViewById(R.id.shop_discount3_line);


        discount4 = v.findViewById(R.id.shop_discount4);
        discount4title = v.findViewById(R.id.shop_discount4_title);
        discount4layout = v.findViewById(R.id.shop_discount4_layout);
        discount4line = v.findViewById(R.id.shop_discount4_line);


        discount5 = v.findViewById(R.id.shop_discount5);
        discount5title = v.findViewById(R.id.shop_discount5_title);
        discount5layout = v.findViewById(R.id.shop_discount5_layout);



        getData();
        onClicks();
    }
    private void rateListener(){
        rate.setOnRatingChangeListener((BaseRatingBar baseRatingBar, float v)-> {
                if(fromuser) {
                    updateRate(v + "");
                    MyToast.Companion.create(getContext(),"امتیاز شما: "+v);
                }
                fromuser=true;
        });
    }
    private void onClicks() {
        tel.setOnClickListener(View->
                intentAction("tel:"+tel.getText().toString())

        );
        instagram.setOnClickListener(View->
                intentAction("https://instagram.com/"+instagramID)

        );

        telegram.setOnClickListener(View->
                intentAction("https://t.me/"+telegramID)
        );
        website.setOnClickListener(View->
                intentAction(websiteID)
        );

        bookmark.setOnClickListener(View-> {
                updateBookmark();
                if(bookmarkStat.equals("0")){
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_full));
                    bookmarkStat="1";

                }else{
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.vector_bookmark_empty));
                    bookmarkStat="0";
                }
        });

        buy.setOnClickListener(View-> {
                if(MySharedPreference.Companion.getInstance(getContext()).getPlan()!=1)
                    requestCamera();
                else
                    showUpgradeDialog();
        });

        share.setOnClickListener(View->
                MyUtils.Companion.shareCode(getActivity(),
                        getString(R.string.shop_share,
                                name.getText().toString(),
                                maxDiscount+"%",
                                address.getText().toString(),
                                tel.getText().toString(),
                                "https://radical-app.ir/shops?id="+shopId))
        );

        like.setOnClickListener(v->{
           if (!liked){
               like.setImageResource(R.drawable.tup);
               dislike.setImageResource(R.drawable.tdownd);
               animation(like);
               liked=true;
               disliked=false;
               doLike(1);
           }else {
               animation(like);
           }

        });
        dislike.setOnClickListener(v->{
            if (!disliked){
                like.setImageResource(R.drawable.tupd);
                dislike.setImageResource(R.drawable.tdown);
                animation(dislike);
                disliked=true;
                liked=false;
                doLike(-1);

            }else{
                animation(dislike);
            }

        });

        comment.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), CommentsActivity.class);
            intent.putExtra("shopid",shopId);
            startActivity(intent);
            Const.Companion.setShopid(shopId);
            getActivity().overridePendingTransition(R.anim.bottom_up,R.anim.bottom_down);
        });

    }

    private void animation(View v){
        ObjectAnimator scaleXanimator = ObjectAnimator.ofFloat(v, "scaleX", 1f,1.5f,1f);
        scaleXanimator.setDuration(300);

        ObjectAnimator scaleYanimator = ObjectAnimator.ofFloat(v, "scaleY", 1f,1.5f,1f);
        scaleYanimator.setDuration(300);

        scaleXanimator.start();
        scaleYanimator.start();
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
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

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
        if(shopId==null)
            shopId = "";

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .getShopDetails(shopId,number,accessToken)
                    .enqueue(new Callback<ShopDetailsModel>() {
                        @Override
                        public void onResponse(Call<ShopDetailsModel> call, Response<ShopDetailsModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                if ("ok".equals(response.body().getData().getMessage())) {

                                    if (!response.body().getData().getLat().isEmpty()) {
                                        if (!ImageLoader.getInstance().isInited()) {
                                            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
                                            ImageLoader.getInstance().init(config);
                                        }
                                        staticMap.setVisibility(View.VISIBLE);
                                        getStaticMap(response.body().getData().getLat(), response.body().getData().getLon());
                                        staticMap.setOnClickListener(View -> showBigMap(response.body().getData().getName(), response.body().getData().getLat(), response.body().getData().getLon()));
                                    }
                                    parseData(response.body());
                                } else {
                                    MyToast.Companion.create(getContext(), getString(R.string.general_error));
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            }else{
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        }

                        @Override
                        public void onFailure(Call<ShopDetailsModel> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
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
        ultraViewPager.setPageTransformer(true, new UltraDepthScaleTransformer());
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
        categoryId=response.getData().getCategoryId();
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

        int likes = response.getData().getLikes();
        int dislikes = response.getData().getDislikes();
        int comments = response.getData().getComments();
        if(likes>999 && likes<1000000 )
            likeCount.setText(likes/1000+"k");
        else if(likes>=1000000)
            likeCount.setText(likes/1000000+"m");
        else
            likeCount.setText(likes+"");

        if(dislikes>999 && dislikes<1000000 )
            dislikeCount.setText(dislikes/1000+"k");
        else if(dislikes>=1000000)
            dislikeCount.setText(dislikes/1000000+"m");
        else
            dislikeCount.setText(dislikes+"");

        if(comments>999 && comments<1000000 )
            commentsCount.setText(comments/1000+"k");
        else if(comments>=1000000)
            commentsCount.setText(comments/1000000+"m");
        else
            commentsCount.setText(comments+"");


        switch (response.getData().getUser()){
            case 1:
                like.setImageResource(R.drawable.tup);
                dislike.setImageResource(R.drawable.tdownd);
                disliked=false;
                liked=true;
                break;
            case -1:
                like.setImageResource(R.drawable.tupd);
                dislike.setImageResource(R.drawable.tdown);
                disliked=true;
                liked=false;
                break;
        }

        initViewPager(response.getData().getPicNum()-1);


        List<PlanData> list = response.getPlans();
        switch (list.size()){
            case 5:
                String des5 = list.get(4).getDescription();
                String d5 = list.get(4).getDiscount();
                discount5.setVisibility(View.VISIBLE);
                discount5.setText(d5+"%");
                discount5title.setVisibility(View.VISIBLE);
                discount5layout.setVisibility(View.VISIBLE);
                discount4line.setVisibility(View.VISIBLE);
                discount5title.setText(des5);
                maxDiscount = Integer.parseInt(d5);

            case 4:
                String des4 = list.get(3).getDescription();
                String d4 = list.get(3).getDiscount();
                discount4.setVisibility(View.VISIBLE);
                discount4.setText(d4+"%");
                discount4title.setVisibility(View.VISIBLE);
                discount4layout.setVisibility(View.VISIBLE);
                discount3line.setVisibility(View.VISIBLE);
                discount4title.setText(des4);
                if(Integer.parseInt(d4)>maxDiscount)
                    maxDiscount=Integer.parseInt(d4);

            case 3:
                String des3 = list.get(2).getDescription();
                String d3 = list.get(2).getDiscount();
                discount3.setVisibility(View.VISIBLE);
                discount3.setText(d3+"%");
                discount3title.setVisibility(View.VISIBLE);
                discount2line.setVisibility(View.VISIBLE);
                discount3layout.setVisibility(View.VISIBLE);
                discount3title.setText(des3);
                if(Integer.parseInt(d3)>maxDiscount)
                    maxDiscount=Integer.parseInt(d3);

            case 2:
                String des2 = list.get(1).getDescription();
                String d2 = list.get(1).getDiscount();
                discount2.setVisibility(View.VISIBLE);
                discount2.setText(d2+"%");
                discount2title.setVisibility(View.VISIBLE);
                discount1line.setVisibility(View.VISIBLE);
                discount2layout.setVisibility(View.VISIBLE);
                discount2title.setText(des2);
                if(Integer.parseInt(d2)>maxDiscount)
                    maxDiscount=Integer.parseInt(d2);

            case 1:
                String des = list.get(0).getDescription();
                String d = list.get(0).getDiscount();
                discount1.setVisibility(View.VISIBLE);
                discount1.setText(d+"%");
                discount1title.setVisibility(View.VISIBLE);
                discount1layout.setVisibility(View.VISIBLE);
                discount1title.setText(des);
                if(Integer.parseInt(d)>maxDiscount)
                    maxDiscount=Integer.parseInt(d);

        }
        rateListener();
    }
    private void updateBookmark(){
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();
        if(shopId==null)
            shopId = "";


        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
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

        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();
        if(shopId==null)
            shopId = "";
        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
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
                                    rateTtile.setText(getString(R.string.shop_rate_title,String.valueOf(mean).substring(0,3),count));

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

    private void doLike(int status){
        like.setClickable(false);
        dislike.setClickable(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .doLike(number,accessToken,shopId,status)
                    .enqueue(new Callback<ShopDetailsModel>() {
                        @Override
                        public void onResponse(Call<ShopDetailsModel> call, Response<ShopDetailsModel> response) {
                            if(response.isSuccessful()){
                                int likes = response.body().getData().getLikes();
                                int dislikes = response.body().getData().getDislikes();
                                int comments = response.body().getData().getComments();
                                if(likes>999 && likes<1000000 )
                                    likeCount.setText(likes/1000+"k");
                                else if(likes>=1000000)
                                    likeCount.setText(likes/1000000+"m");
                                else
                                    likeCount.setText(likes+"");

                                if(dislikes>999 && dislikes<1000000 )
                                    dislikeCount.setText(dislikes/1000+"k");
                                else if(dislikes>=1000000)
                                    dislikeCount.setText(dislikes/1000000+"m");
                                else
                                    dislikeCount.setText(dislikes+"");

                                if(comments>999 && comments<1000000 )
                                    commentsCount.setText(comments/1000+"k");
                                else if(comments>=1000000)
                                    commentsCount.setText(comments/1000000+"m");
                                else
                                    commentsCount.setText(comments+"");
                                switch (response.body().getData().getUser()){
                                    case 1:
                                        like.setImageResource(R.drawable.tup);
                                        dislike.setImageResource(R.drawable.tdownd);
                                        disliked=false;
                                        liked=true;
                                        break;
                                    case -1:
                                        like.setImageResource(R.drawable.tupd);
                                        dislike.setImageResource(R.drawable.tdown);
                                        disliked=true;
                                        liked=false;
                                        break;
                                }
                            }
                            like.setClickable(true);
                            dislike.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<ShopDetailsModel> call, Throwable t) {
                            like.setClickable(true);
                            dislike.setClickable(true);
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
        builder.setPositiveButton(getString(R.string.permission_allow),(DialogInterface dialog, int which)->
                requestPermission());
        builder.setNegativeButton(getString(R.string.permission_dismiss),(DialogInterface dialog, int which)->
                dialog.dismiss()
        );

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showBigMap(String name,String lat,String lon){

        Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("categoryId",categoryId);
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lon);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);


//        MapActivity upgradeDialog = new MapActivity(getContext(),name,lat,lon);
//
//        upgradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
//        upgradeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        upgradeDialog.show();
//        Window window = upgradeDialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);




    }
    private void requestCamera(){

        if(checkPermission()!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)){
                showExplanation();
            }

            else if(!MySharedPreference.Companion.getInstance(getContext()).getCameraPermission()){
                requestPermission();
                MySharedPreference.Companion.getInstance(getContext()).setCameraPermission();

            }else{
                MyToast.Companion.create(getContext(),getString(R.string.permission_camera_toast));
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
        Intent intent = new Intent(getContext(), QrcodeActivity.class);
        intent.putExtra("shopid",shopId);
        getActivity().startActivityForResult(intent,QR_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                readQr();
            }
        }
    }

    private void showUpgradeDialog() {
        UpgradeDialog upgradeDialog = new UpgradeDialog(getActivity());

        Objects.requireNonNull(upgradeDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        upgradeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(Bitmap response) {
        staticMap.setVisibility(View.VISIBLE);
        staticMap.setImageBitmap(response);

    }

    private void getStaticMap(String lat,String lon) {
        new ServiceHelper().getStaticMap(Double.valueOf(lat), Double.valueOf(lon), 11, this);
    }

    @Override
    public void onError(MaptexError error) {
        staticMap.setVisibility(View.GONE);
    }
}
