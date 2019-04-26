package ir.radical_app.radical.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.adapters.ChatAdapter;
import ir.radical_app.radical.adapters.MessagesAdapter;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;

import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_MESSAGE_EXTRA;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_SUPPORT_EXTRA;


public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private MyDatabase myDatabase;
    private ConstraintLayout emptyView;

    public MessagesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_messages, container, false);
        emptyView = v.findViewById(R.id.empty_view);

        recyclerView = v.findViewById(R.id.messages_recycler);
        myDatabase = new MyDatabase(getContext());

        return v;
    }
    private void showEmpty(){
        emptyView.setVisibility(View.VISIBLE);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(RADICAL_BROADCAST.equals(intent.getAction())){
                String extra = intent.getStringExtra(RADICAL_BROADCAST_MESSAGE_EXTRA);
                if(extra!=null){
                    if(getActivity()!=null)
                        MyUtils.Companion.removeNotification(getActivity());

                    adapter = new MessagesAdapter(getContext(),myDatabase.getMessages());
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(RADICAL_BROADCAST);
        if(getActivity()!=null)
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        ArrayList<MessageModel> list = myDatabase.getMessages();
        if(list.isEmpty())
            showEmpty();
        else {
            adapter = new MessagesAdapter(getContext(), myDatabase.getMessages());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()!=null)
            getActivity().unregisterReceiver(broadcastReceiver);

    }
}
