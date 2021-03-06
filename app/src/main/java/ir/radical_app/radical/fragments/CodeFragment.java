package ir.radical_app.radical.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private BroadcastReceiver rec = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String code = intent.getExtras().getString("code");
            if(code!=null && code.length()==4){
                String FBToken = MySharedPreference.Companion.getInstance(getContext()).getFBToken();
                if(FBToken.length()<3)
                    FBToken = FirebaseInstanceId.getInstance().getToken();
                doAuth(code, fNumber,FBToken);
            }
        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_code, container, false);

        getContext().registerReceiver(rec,new IntentFilter("codeReceived"));
        init(v);

        return v;
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(rec);
        super.onDestroy();
    }

    private void init(View v){
        fCommit = v.findViewById(R.id.code_reg);
        fCode = v.findViewById(R.id.code_code);
        fResend = v.findViewById(R.id.code_resend);
        fResend.setEnabled(false);
        fNumber = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        initTimer();
        onClicks();

        fCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==4)
                    fCommit.callOnClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        fCommit.setOnClickListener(View-> {
               String c = fCode.getText().toString();
                MyUtils.Companion.hideKeyboard(getActivity());
                if(c.length()<4 || c.startsWith("0") ){
                    MyToast.Companion.create(getContext(),getString(R.string.wrongcode_error));
                }else{
                    if(!MyUtils.Companion.checkInternet(getContext()))
                        MyToast.Companion.create(getContext(),getString(R.string.internet_error));
                    else {
                        fCommit.setEnabled(false);
                        String FBToken = MySharedPreference.Companion.getInstance(getContext()).getFBToken();
                        if(FBToken.length()<3)
                            FBToken = FirebaseInstanceId.getInstance().getToken();
                        doAuth(c, fNumber,FBToken);
                    }
                }
        });

        fResend.setOnClickListener(View-> {
                if(!MyUtils.Companion.checkInternet(getContext())){
                    MyToast.Companion.create(getContext(),getString(R.string.internet_error));

                }else {
                    fResend.setEnabled(false);
                    initTimer();
                    resendSMS(fNumber);
                }
        });


    }

    private void doAuth(final String code, String number,String FBToken){
        fCode.setEnabled(false);
        fCommit.setText(getString(R.string.txt_loading));

        RetrofitClient.Companion.getInstance().getApi().doAuth(number,FBToken,code)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                        if (response.isSuccessful()){
                            String res = response.body().getMessage();
                            if(res.contains(code)){
                                MyToast.Companion.create(getContext(),getString(R.string.txt_welcome));
                                FirebaseMessaging.getInstance().subscribeToTopic(Const.FCM_TOPIC);

                                MySharedPreference.Companion.getInstance(getContext()).setAccessToken(res);
                                MySharedPreference.Companion.getInstance(getContext()).setIsLogin();
                                startActivity(new Intent(getActivity(),MainActivity.class));

                                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                getActivity().finish();

                            }else if(res.equals("noreg")){
                                MyToast.Companion.create(getContext(),getString(R.string.noreg_error));
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.login_container,new LoginFragment())
                                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                                        .commitNow();

                            }else if(res.equals("wrongcode")){
                                MyToast.Companion.create(getContext(),getString(R.string.wrongcode_error));

                            }else{
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }else{
                            fCode.setEnabled(true);
                            fCommit.setEnabled(true);
                            fCommit.setText(getString(R.string.code_reg));
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {
                        fCode.setEnabled(true);
                        fCommit.setEnabled(true);
                        fCommit.setText(getString(R.string.code_reg));
                        MyToast.Companion.create(getContext(),getString(R.string.general_error));

                    }
                });




    }
    private void resendSMS(String Number){
        RetrofitClient.Companion.getInstance().getApi()
                .resendSMS(Number)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        if(response.isSuccessful()){
                            if(response.body().getMessage().equals("ok")){
                                MyToast.Companion.create(getContext(),getString(R.string.txt_sent));
                            }else if(response.body().getMessage().equals("noreg")){
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
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

    private String convertToTimeFormat(long millisecond){
        Date date = new Date(millisecond);
        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }
}
