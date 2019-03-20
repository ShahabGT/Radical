package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.fragment.app.FragmentManager;
import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.JustifiedTextView;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.dialogs.ProfileDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutFragment extends Fragment {

    private JustifiedTextView text;
    private MaterialButton email,bug,call;
    private ImageView telegram,instagram,website;
    private LoadingDialog dialog;
    private TextView tradeMark;



    public AboutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

       init(v);

        return v;
    }

    private void init(View v){
        text = v.findViewById(R.id.about_text);
        tradeMark = v.findViewById(R.id.about_trademark2);
        String tradeMarkText = getContext().getString(R.string.about_trademark2);
        SpannableString spannableString = new SpannableString(tradeMarkText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://shahabazimi.ir"));
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan,36,46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tradeMark.setText(spannableString);
        tradeMark.setMovementMethod(LinkMovementMethod.getInstance());

        email = v.findViewById(R.id.about_email);
        bug = v.findViewById(R.id.about_issue);
        call = v.findViewById(R.id.about_call);

        telegram = v.findViewById(R.id.about_telegram);
        instagram = v.findViewById(R.id.about_instagram);
        website = v.findViewById(R.id.about_website);


        getData();
        onClicks();
    }

    private void onClicks(){
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","support@radical-app.ir", null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@radical-app.ir"});
                startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل از طریق"));
            }
        });

        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MySharedPreference.getInstance(getContext()).getName().isEmpty())
                    setFragment(new SupportFragment());
                else
                    showProfileDialog();

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("tel:08337290216");

            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("https://t.me/radical_app");

            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("https://instagram.com/radical_app");

            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAction("https://radical-app.ir");

            }
        });
    }

    private void loadingDialog(boolean cancel){
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
    private void setFragment(Fragment fragment){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        while(fm.getBackStackEntryCount()>1)
            fm.popBackStackImmediate();


        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                .add(R.id.main_container,fragment)
                .addToBackStack(null)
                .commit();
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
                    .getAbout()
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                if(response.body().getMessage().equals("ok")){
                                    text.setText(response.body().getDescription());
                                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                                    text.setLineSpacing(15);
                                }else{
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

    private void showProfileDialog() {
        ProfileDialog profileDialog = new ProfileDialog(getActivity());

        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        profileDialog.show();
        Window window = profileDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void intentAction(String id){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(id));
        startActivity(intent);
    }


}
