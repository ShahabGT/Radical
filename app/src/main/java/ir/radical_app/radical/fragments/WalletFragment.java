package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import ir.radical_app.radical.R;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.models.JsonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WalletFragment extends Fragment {

    private TextView walletAmount;
    private MaterialButton walleyBuy;
    private LoadingDialog dialog;

    private TextView profile3, invite3;
    private ImageView profile1, invite1, social1;
    private ImageView instagram, telegram;
    private MaterialButton profile2, invite2, buy2;

    private boolean fromInstagram = false;
    private boolean fromTelegram = false;


    public WalletFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        walletAmount = v.findViewById(R.id.wallet_amount);
        walleyBuy = v.findViewById(R.id.wallet_buy);

        profile1 = v.findViewById(R.id.wallet_info_checked1);
        invite1 = v.findViewById(R.id.wallet_info_checked2);
        social1 = v.findViewById(R.id.wallet_info_checked4);
        profile2 = v.findViewById(R.id.wallet_info_text1);
        profile3 = v.findViewById(R.id.wallet_info_text1_sub);
        invite2 = v.findViewById(R.id.wallet_info_text2);
        invite3 = v.findViewById(R.id.wallet_info_text2_sub);
        buy2 = v.findViewById(R.id.wallet_info_text3);


        telegram = v.findViewById(R.id.wallet_telegram);
        instagram = v.findViewById(R.id.wallet_instagram);

        if (MyUtils.Companion.checkInternet(getContext()))
            getData();
        else {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            MyToast.Companion.create(getContext(), getString(R.string.internet_error));

        }

        onClicks();
    }

    private void onClicks() {
        walleyBuy.setOnClickListener(View ->
                showUpgradeDialog()
        );

        profile1.setOnClickListener(View ->
                showProfile()
        );
        profile2.setOnClickListener(View ->
                showProfile()
        );
        profile3.setOnClickListener(View ->
                showProfile()
        );

        invite1.setOnClickListener(View ->
                showInvite()

        );
        invite2.setOnClickListener(View ->
                showInvite()

        );
        invite3.setOnClickListener(View ->
                showInvite()
        );

        buy2.setOnClickListener(View ->
                getActivity().getSupportFragmentManager().popBackStack()
        );

        telegram.setOnClickListener(View -> {
            intentAction("https://t.me/radical_app");
            fromTelegram = true;
        });

        instagram.setOnClickListener(View -> {
            intentAction("https://instagram.com/radical_app");
            fromInstagram = true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (fromInstagram)
            getSocialGift();
        if (fromTelegram)
            getSocialGift2();

        fromInstagram = false;
        fromTelegram = false;
    }

    private void intentAction(String id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(id));
        startActivity(intent);
    }

    private void getSocialGift() {
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if (number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(getContext(), getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        } else {
            RetrofitClient.Companion.getInstance().getApi()
                    .getSocialGift(number, accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("ok")) {
                                    getData();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {

                        }
                    });
        }
    }

    private void getSocialGift2() {
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if (number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(getContext(), getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        } else {
            RetrofitClient.Companion.getInstance().getApi()
                    .getSocialGift2(number, accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("ok")) {
                                    getData();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {

                        }
                    });
        }
    }

    private void showInvite() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("")
                .add(R.id.main_container, new ShareFragment())
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .commit();

    }

    private void showProfile() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("")
                .add(R.id.main_container, new ProfileFragment())
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .commit();

    }


    private void loadingDialog(boolean cancel) {
        if (!cancel) {
            dialog = new LoadingDialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(false);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    private void showUpgradeDialog() {
        UpgradeDialog upgradeDialog = new UpgradeDialog(getActivity());

        upgradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        upgradeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void getData() {
        loadingDialog(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if (number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(getContext(), getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        } else {

            RetrofitClient.Companion.getInstance().getApi().getWallet(number, accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if (response.isSuccessful()) {
                                String res = response.body().getMessage();
                                switch (res) {
                                    case "ok":
                                        String money = MyUtils.Companion.moneySeparator(response.body().getAmount());
                                        walletAmount.setText(getString(R.string.amount_model, money));
                                        if (!MySharedPreference.Companion.getInstance(getContext()).getName().isEmpty())
                                            profile1.setImageDrawable(getResources().getDrawable(R.drawable.checked));
                                        if (response.body().getInviteCount().equals("7"))
                                            invite1.setImageDrawable(getResources().getDrawable(R.drawable.checked));
                                        if (response.body().getSocialCount().equals("1"))
                                            social1.setImageDrawable(getResources().getDrawable(R.drawable.checked));

                                        break;
                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(), getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                        MyToast.Companion.create(getContext(), getString(R.string.general_error));
                                }

                            } else {
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Companion.create(getContext(), getString(R.string.general_error));
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Companion.create(getContext(), getString(R.string.general_error));

                        }
                    });

        }


    }

}
