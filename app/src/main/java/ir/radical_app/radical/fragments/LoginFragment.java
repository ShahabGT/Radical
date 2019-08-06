package ir.radical_app.radical.fragments;


import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment {

    private static final int RESOLVE_HINT = 581;
    private Button fLogin;
    private EditText fNumber;


    public LoginFragment() {
    }





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        init(v);
        return v;
    }

    private void init(View v){
        fLogin = v.findViewById(R.id.login_login);
        fNumber = v.findViewById(R.id.login_number);

        try {
            requestHint();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onClicks();
    }

    private void requestHint() throws Exception {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(getContext()).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0,null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String number = credential.getId();
                if(number.startsWith("+98"))
                    number="0"+number.substring(3);
                else if(number.startsWith("0098"))
                    number="0"+number.substring(4);
                fNumber.setText(number);
            }
        }
    }

    private void onClicks(){
        fLogin.setOnClickListener(View-> {
                MyUtils.Companion.hideKeyboard(getActivity());
                String n = fNumber.getText().toString();
                if(n.length()<11 || !n.startsWith("09") ){
                    MyToast.Companion.create(getContext(),getString(R.string.number_error));
                }else{
                    if(!MyUtils.Companion.checkInternet(getContext()))
                        MyToast.Companion.create(getContext(),getString(R.string.internet_error));
                    else
                        doLogin(n);
                }
        });
    }

    private void doLogin(final String number) {
        fNumber.setEnabled(false);
        fLogin.setEnabled(false);
        fLogin.setText(getString(R.string.txt_loading));
        SmsRetriever.getClient(getActivity()).startSmsUserConsent("98300077");
        RetrofitClient.Companion.getInstance().getApi().doLogin(number)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        fLogin.setText(getString(R.string.login_login));

                        fNumber.setEnabled(true);
                        fLogin.setEnabled(true);
                        if (response.isSuccessful()){
                            if(response.body().getMessage().equals("ok")){
                                MySharedPreference.Companion.getInstance(getContext()).setNumber(number);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                                        .replace(R.id.login_container,new CodeFragment())
                                        .commit();

                            }else{
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }

                        }else{
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {
                        fLogin.setText(getString(R.string.login_login));

                        fNumber.setEnabled(true);
                        fLogin.setEnabled(true);
                        MyToast.Companion.create(getContext(),getString(R.string.general_error));

                    }
                });



    }

}
