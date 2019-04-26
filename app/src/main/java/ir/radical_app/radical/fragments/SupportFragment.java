package ir.radical_app.radical.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.ChatAdapter;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.InfoDialog;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.ChatModel;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_SUPPORT_EXTRA;


public class SupportFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ImageView send;
    private EditText text;
    private MyDatabase myDatabase;
    private LoadingDialog dialog;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;



    public SupportFragment() {
    }

    private void showInfoDialog() {
        if(MySharedPreference.Companion.getInstance(getContext()).getFirstSupport()){
            MySharedPreference.Companion.getInstance(getContext()).setFirstSupport();
        }else{
            return;
        }


        InfoDialog introDialog = new InfoDialog(getContext(),"از این قسمت پیام های رادیکال رو میتونی بخونی و با پشتیبانی رادیکال در ارتباط باشی");
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
        View v= inflater.inflate(R.layout.fragment_support, container, false);

        init(v);

        return v;
    }



    private void init(View v){
        recyclerView = v.findViewById(R.id.support_recycler);
        fab = v.findViewById(R.id.support_call);
        send = v.findViewById(R.id.support_send);
        text = v.findViewById(R.id.support_text);
        myDatabase = new MyDatabase(getContext());
        layoutManager = new LinearLayoutManager(getContext());

        onClicks();

       recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
           @Override
           public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
               if ( bottom < oldBottom) {
                   recyclerView.post(new Runnable() {
                       @Override
                       public void run() {
                           if(adapter!=null)
                               recyclerView.scrollToPosition(adapter.getItemCount()-1);

                       }
                   });
               }
           }
       });
    }

    private void onClicks(){
        send.setOnClickListener(View-> {
                if(text.getText().toString().length()>0){
                    sendData(text.getText().toString());
                }
        });

        fab.setOnClickListener(View-> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:08337290216"));
                startActivity(intent);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
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
                    .getMessages(number,accessToken)
                    .enqueue(new Callback<ChatModel>() {
                        @Override
                        public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        myDatabase.deleteSupportTable();
                                        for(int i=0;i<response.body().getData().size();i++){
                                            myDatabase.saveSupport(response.body().getData().get(i));
                                        }
                                        showData();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;
                                    case "empty":

                                        break;

                                    default:
                                        MyToast.Companion.create(getContext(),getString(R.string.general_error));
                                }


                            }else{
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<ChatModel> call, Throwable t) {
                            loadingDialog(true);
                            if(getActivity()!=null)
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    });

        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(RADICAL_BROADCAST.equals(intent.getAction())){
                String extra = intent.getStringExtra(RADICAL_BROADCAST_SUPPORT_EXTRA);
                if(extra!=null){
                    if(getActivity()!=null)
                        MyUtils.Companion.removeNotification(getActivity());

                    adapter = new ChatAdapter(getContext(),myDatabase.getChatMessages());
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    layoutManager.scrollToPosition(adapter.getItemCount()-1);
                }

            }
        }
    };

    private void showData(){
        adapter = new ChatAdapter(getContext(),myDatabase.getChatMessages());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager.scrollToPosition(adapter.getItemCount()-1);

        adapter.notifyDataSetChanged();
        showInfoDialog();
        MyUtils.Companion.removeNotification(getContext());

    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(RADICAL_BROADCAST);
        if(getActivity()!=null)
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        if(myDatabase.getChatMessages().isEmpty())
            getData();
        else
            showData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()!=null)
            getActivity().unregisterReceiver(broadcastReceiver);

    }

    private void sendData(String message){
        text.setEnabled(false);
        send.setEnabled(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
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
                                        model.setRead("1");
                                        myDatabase.saveSupport(model);
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
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        MyToast.Companion.create(getContext(),getString(R.string.general_error));
                                }
                            }else {
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            send.setEnabled(true);
                            text.setEnabled(true);
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));

                        }
                    });
        }


    }

}
