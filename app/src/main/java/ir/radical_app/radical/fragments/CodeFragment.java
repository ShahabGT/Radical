package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CodeFragment extends Fragment {

    private Button fCommit,fResend;
    private EditText fCode;
    private String fNumber;
    private CountDownTimer timer;


    public CodeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_code, container, false);
        bringToFront(v);
        init(v);
      //  backgroundAnimation(v);

        return v;
    }

    private void init(View v){
        fCommit = v.findViewById(R.id.code_reg);
        fCode = v.findViewById(R.id.code_code);
        fResend = v.findViewById(R.id.code_resend);
        fResend.setEnabled(false);
        fNumber = MySharedPreference.getInstance(getContext()).getNumber();
        initTimer();
        onClicks();
    }

    private void initTimer(){
        timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                fResend.setText(convertToTimeFormat(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                try {
                    fResend.setEnabled(true);
                    fResend.setText(getString(R.string.code_resend));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
    }

    private void onClicks(){
        fCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String c = fCode.getText().toString();
                MyUtils.hideKeyboard(getActivity());
                if(c.length()<4 || c.startsWith("0") ){
                    MyToast.Create(getContext(),getString(R.string.wrongcode_error));
                }else{
                    if(!MyUtils.checkInternet(getContext()))
                        MyToast.Create(getContext(),getString(R.string.internet_error));
                    else {
                        String FBToken = MySharedPreference.getInstance(getContext()).getFBToken();
                        if(FBToken.length()<3)
                            FBToken = FirebaseInstanceId.getInstance().getToken();
                        doAuth(c, fNumber,FBToken);
                    }
                }
            }
        });

        fResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyUtils.checkInternet(getContext())){
                    MyToast.Create(getContext(),getString(R.string.internet_error));

                }else {
                    fResend.setEnabled(false);
                    initTimer();
                    resendSMS(fNumber);
                }
            }
        });


    }

    private void doAuth(final String code, String number,String FBToken){
        fCode.setEnabled(false);
        fCommit.setEnabled(false);
        fCommit.setText(getString(R.string.txt_loading));

        RetrofitClient.getInstance().getApi().doAuth(number,FBToken,code)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        fCode.setEnabled(true);
                        fCommit.setEnabled(true);
                        fCommit.setText(getString(R.string.code_reg));
                        if (response.isSuccessful()){
                            String res = response.body().getMessage();
                            if(res.contains(code)){
                                MyToast.Create(getContext(),getString(R.string.txt_welcome));
                                FirebaseMessaging.getInstance().subscribeToTopic(Const.FCM_TOPIC);

                                MySharedPreference.getInstance(getContext()).setAccessToken(res);
                                MySharedPreference.getInstance(getContext()).setIsLogin(true);
                                startActivity(new Intent(getActivity(),MainActivity.class));

                                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fade_out);
                                getActivity().finish();

                            }else if(res.equals("noreg")){
                                MyToast.Create(getContext(),getString(R.string.noreg_error));
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.login_container,new LoginFragment())
                                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                                        .commitNow();

                            }else if(res.equals("wrongcode")){
                                MyToast.Create(getContext(),getString(R.string.wrongcode_error));

                            }else{
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }else{
                            MyToast.Create(getContext(),getString(R.string.general_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {
                        fCode.setEnabled(true);
                        fCommit.setEnabled(true);
                        fCommit.setText(getString(R.string.code_reg));
                        MyToast.Create(getContext(),getString(R.string.general_error));

                    }
                });




    }
    private void resendSMS(String Number){
        RetrofitClient.getInstance().getApi()
                .resendSMS(Number)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        if(response.isSuccessful()){
                            if(response.body().getMessage().equals("ok")){
                                MyToast.Create(getContext(),getString(R.string.txt_sent));
                            }else if(response.body().getMessage().equals("noreg")){
                                MyToast.Create(getContext(),getString(R.string.general_error));
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction().replace(R.id.login_container,new LoginFragment())
                                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                                        .commitNow();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {

                    }
                });
    }
    private void backgroundAnimation(View v){
        ConstraintLayout constraintLayout = v.findViewById(R.id.code_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.start();
    }
    private void bringToFront(View v){
        ImageView imageView = v.findViewById(R.id.code_logo);
        imageView.bringToFront();
        imageView.requestLayout();
        imageView.invalidate();
    }
    private String convertToTimeFormat(long millisecond){
        Date date = new Date(millisecond);
        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }
}
