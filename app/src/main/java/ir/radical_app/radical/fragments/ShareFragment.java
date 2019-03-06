package ir.radical_app.radical.fragments;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShareFragment extends Fragment {

    private ImageView share;
    private TextView code,count,get;
    private TextView three,five,seven;
    private TextView warning;
    private LoadingDialog dialog;
    private RoundCornerProgressBar progressBar;
    private float fCount=0f;


    public ShareFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_share, container, false);

        init(v);
        onClicks();
    //    animation();

        return v;
    }

    private void init(View v){
        share = v.findViewById(R.id.share_share);
        code = v.findViewById(R.id.share_code);
        count = v.findViewById(R.id.share_count);
        get = v.findViewById(R.id.share_get);
        progressBar= v.findViewById(R.id.share_progress);

     //   warning = v.findViewById(R.id.giftcode_warning);

        three = v.findViewById(R.id.share_three);
        five = v.findViewById(R.id.share_five);
        seven = v.findViewById(R.id.share_seven);

        code.setText(MySharedPreference.getInstance(getContext()).getNumber());
        getData();
    }

    private void onClicks(){
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.shareCode(getActivity(),getString(R.string.giftcode_share,code.getText().toString()));
            }
        });
        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.shareCode(getActivity(),getString(R.string.giftcode_share,code.getText().toString()));
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fCount<3){
                    MyToast.Create(getContext(),"تعداد دوستان شما به حد نساب نرسیده!");

                }else{
                    MyToast.Create(getContext(),"جایزه دریافت شد");
                }
            }
        });

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
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .getInvites(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){

                                switch (response.body().getMessage()){
                                    case "ok":
                                        fCount = Float.parseFloat(response.body().getCount());
                                        count.setText(getContext().getString(R.string.giftcode_count,(int)fCount+""));
                                        progressBar.setProgress(fCount);

                                        if(fCount>=7){
                                            seven.setTextColor(getResources().getColor(R.color.green));
                                            five.setTextColor(getResources().getColor(R.color.green));
                                            three.setTextColor(getResources().getColor(R.color.green));
                                        }else if (fCount>=5){
                                            five.setTextColor(getResources().getColor(R.color.green));
                                            three.setTextColor(getResources().getColor(R.color.green));
                                        }else if(fCount>=3){
                                            three.setTextColor(getResources().getColor(R.color.green));
                                        }
                                        break;

                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                        default:

                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                            MyToast.Create(getContext(),getString(R.string.general_error));


                                }



                            }else{
                                loadingDialog(true);
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Create(getContext(),getString(R.string.general_error));
                        }
                    });
        }
    }


//    private void animation(){
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(
//                warning,
//                "alpha",
//                1f,0.2f
//                );
//        alpha.setDuration(1000);
//        alpha.setRepeatMode(ValueAnimator.REVERSE);
//        alpha.setRepeatCount(ValueAnimator.INFINITE);
//        alpha.start();
//
//    }


}
