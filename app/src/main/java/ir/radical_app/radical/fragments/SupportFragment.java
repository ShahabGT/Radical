package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.ChatAdapter;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.ChatModel;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SupportFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ImageView send;
    private EditText text;
    private MyDatabase myDatabase;
    private LoadingDialog dialog;
    private RecyclerView.LayoutManager layoutManager;



    public SupportFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_support, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        recyclerView = v.findViewById(R.id.support_recycler);
        send = v.findViewById(R.id.support_send);
        text = v.findViewById(R.id.support_text);
        myDatabase = new MyDatabase(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        getData();
        onClicks();
    }

    private void onClicks(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().length()>0){
                    sendData(text.getText().toString());
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
                    .getMessages(number,accessToken)
                    .enqueue(new Callback<ChatModel>() {
                        @Override
                        public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        myDatabase.deleteMessageTable();
                                        for(int i=0;i<response.body().getData().size();i++){
                                            myDatabase.saveMessage(response.body().getData().get(i));
                                        }

                                        adapter = new ChatAdapter(getContext(),myDatabase.getChatMessages());
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        layoutManager.scrollToPosition(adapter.getItemCount()-1);

                                        adapter.notifyDataSetChanged();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;
                                    case "empty":

                                        break;

                                    default:
                                        MyToast.Create(getContext(),getString(R.string.general_error));
                                }


                            }else{
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<ChatModel> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Create(getContext(),getString(R.string.general_error));
                        }
                    });

        }
    }

    private void sendData(String message){
        text.setEnabled(false);
        send.setEnabled(false);
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .sendMessage(number,accessToken,message)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            send.setEnabled(true);
                            text.setEnabled(true);

                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        text.setText("");
                                        MessageModel model = new MessageModel();
                                        model.setDate(response.body().getNowdate());
                                        model.setSender("me");
                                        model.setTitle("");
                                        model.setMessage(message);
                                        myDatabase.saveMessage(model);
                                        if(adapter==null){
                                            adapter = new ChatAdapter(getContext(),new ArrayList<>());
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                                            layoutManager.scrollToPosition(adapter.getItemCount()-1);
                                        }

                                        adapter.addMessageItem(model);
                                        layoutManager.scrollToPosition(adapter.getItemCount()-1);
                                        break;
                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        MyToast.Create(getContext(),getString(R.string.general_error));
                                }
                            }else {
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            send.setEnabled(true);
                            text.setEnabled(true);
                            MyToast.Create(getContext(),getString(R.string.general_error));

                        }
                    });
        }


    }

}
