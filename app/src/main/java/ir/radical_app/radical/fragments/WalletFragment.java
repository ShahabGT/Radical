package ir.radical_app.radical.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WalletFragment extends Fragment {

    private TextView walletAmount;
    private MaterialButton walleyBuy;
    private LoadingDialog dialog;



    public WalletFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        walletAmount = v.findViewById(R.id.wallet_amount);
        walleyBuy = v.findViewById(R.id.wallet_buy);
        if(MyUtils.checkInternet(getContext()))
            getData();
        else{
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            MyToast.Create(getContext(),getString(R.string.internet_error));

        }

        onClicks();
    }

    private void onClicks(){
        walleyBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpgradeDialog();
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

    private void showUpgradeDialog() {
        UpgradeDialog upgradeDialog = new UpgradeDialog(getContext());

        upgradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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

            RetrofitClient.getInstance().getApi().getWallet(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        String money = MyUtils.moneySeparator(response.body().getAmount());
                                        walletAmount.setText(getString(R.string.amount_model,money));
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

}
