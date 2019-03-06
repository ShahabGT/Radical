package ir.radical_app.radical.fragments;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private Button fLogin;
    private EditText fNumber;


    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
      //  backgroundAnimation(v);
        bringToFront(v);
        init(v);

        return v;
    }


    private void backgroundAnimation(View v){
        ConstraintLayout constraintLayout = v.findViewById(R.id.login_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.start();
    }


    private void bringToFront(View v){
        ImageView imageView = v.findViewById(R.id.login_logo);
        imageView.bringToFront();
        imageView.requestLayout();
        imageView.invalidate();
    }

    private void init(View v){
        fLogin = v.findViewById(R.id.login_login);
        fNumber = v.findViewById(R.id.login_number);

        onClicks();
    }

    private void onClicks(){
        fLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.hideKeyboard(getActivity());
                String n = fNumber.getText().toString();
                if(n.length()<11 || !n.startsWith("09") ){
                    MyToast.Create(getContext(),getString(R.string.number_error));
                }else{
                    if(!MyUtils.checkInternet(getContext()))
                        MyToast.Create(getContext(),getString(R.string.internet_error));
                    else
                        doLogin(n);
                }
            }
        });
    }

    private void doLogin(final String number) {
        fNumber.setEnabled(false);
        fLogin.setEnabled(false);
        fLogin.setText(getString(R.string.txt_loading));

        RetrofitClient.getInstance().getApi().doLogin(number)
                .enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        fLogin.setText(getString(R.string.login_login));

                        fNumber.setEnabled(true);
                        fLogin.setEnabled(true);
                        if (response.isSuccessful()){
                            switch (response.body().getMessage()){
                                case "ok":
                                    MySharedPreference.getInstance(getContext()).setNumber(number);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.login_container,new CodeFragment())
                                            .setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                                            .commit();
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
                        fLogin.setText(getString(R.string.login_login));

                        fNumber.setEnabled(true);
                        fLogin.setEnabled(true);
                        MyToast.Create(getContext(),getString(R.string.general_error));

                    }
                });



    }

}
