package ir.radical_app.radical.fragments;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.InfoDialog;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShareFragment extends Fragment {

    private ImageView share;
    private TextView code,count,get;
    private LoadingDialog dialog;
    private RoundCornerProgressBar progressBar;
    private float fCount=0f;


    public ShareFragment() {
    }

    private void showInfoDialog() {
        if(MySharedPreference.Companion.getInstance(getContext()).getFirstShare()){
            MySharedPreference.Companion.getInstance(getContext()).setFirstShare();
        }else{
            return;
        }


        InfoDialog introDialog = new InfoDialog(getContext(),"رادیکال رو به دوستان معرفی کن و هدیه بگیر");
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_share, container, false);

        init(v);
        onClicks();

        return v;
    }

    private void init(View v){
        share = v.findViewById(R.id.share_share);
        code = v.findViewById(R.id.share_code);
        count = v.findViewById(R.id.share_count);
        get = v.findViewById(R.id.share_get);
        progressBar= v.findViewById(R.id.share_progress);

        code.setText(MySharedPreference.Companion.getInstance(getContext()).getNumber());
        getData();
    }

    private void onClicks(){
        share.setOnClickListener(View->
                MyUtils.Companion.shareCode(getActivity(),getString(R.string.giftcode_share,
                        code.getText().toString()))
        );
        code.setOnClickListener(View->
                MyUtils.Companion.shareCode(getActivity(),getString(R.string.giftcode_share,
                        code.getText().toString()))

        );

        get.setOnClickListener(View-> {

                if(fCount==0){
                    MyToast.Companion.create(getContext(),"تعداد دوستان شما به حد نساب نرسیده!");

                }else{
                   getGift();
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

    private void getGift(){
        loadingDialog(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .getGift(number,accessToken,((int)fCount)+"")
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                switch (response.body().getMessage()){
                                    case "ok":
                                        MyToast.Companion.create(getContext(),getString(R.string.txt_share_ok));
                                        break;

                                    case "nonew":
                                        MyToast.Companion.create(getContext(),getString(R.string.txt_share_nonew));
                                        break;

                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                        default:
                                            MyToast.Companion.create(getContext(),getString(R.string.general_error));

                                }



                            }else{
                                loadingDialog(true);
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }


                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    });

        }
    }

    private void getData(){
        loadingDialog(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .getInvites(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){

                                switch (response.body().getMessage()){
                                    case "ok":
                                        fCount = Float.parseFloat(response.body().getCount());
                                        int remaning = 7 - (int)fCount;
                                        count.setText(getContext().getString(R.string.giftcode_star_title,remaning));
                                        progressBar.setProgress(fCount);
                                        showInfoDialog();
                                        break;

                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                        default:

                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                            MyToast.Companion.create(getContext(),getString(R.string.general_error));


                                }



                            }else{
                                loadingDialog(true);
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    });
        }
    }

}
