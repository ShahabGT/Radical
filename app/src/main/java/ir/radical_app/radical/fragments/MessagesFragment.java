package ir.radical_app.radical.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.adapters.MessagesAdapter;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;


public class MessagesFragment extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private MyDatabase myDatabase;
    private ConstraintLayout emptyView;

    public MessagesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_messages, container, false);
        emptyView = v.findViewById(R.id.empty_view);

        recyclerView = v.findViewById(R.id.messages_recycler);
        myDatabase = new MyDatabase(getContext());

        return v;
    }
    private void showEmpty(){
        emptyView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
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
}
