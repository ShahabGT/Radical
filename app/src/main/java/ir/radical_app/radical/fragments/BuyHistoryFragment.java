package ir.radical_app.radical.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.arch.BuyHistory.BuyHistoryAdapter;
import ir.radical_app.radical.arch.BuyHistory.BuyHistoryViewModel;
import ir.radical_app.radical.arch.BuyHistory.BuyItem;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.R;



public class BuyHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static ConstraintLayout emptyView;
    private static TextView emptyViewText;


    public BuyHistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_buy_history, container, false);

        init(v);
        return v;
    }

    private void init(View v){
        emptyView = v.findViewById(R.id.empty_view);
        emptyViewText = v.findViewById(R.id.empty_text);
        recyclerView = v.findViewById(R.id.buy_history_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        showData();
    }

    private void showData(){
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();
        BuyHistoryViewModel viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T)new BuyHistoryViewModel(number,accessToken);
            }
        }).get(BuyHistoryViewModel.class);
        final BuyHistoryAdapter adapter = new BuyHistoryAdapter(getContext());

        viewModel.itemPagedList.observe(this, new Observer<PagedList<BuyItem>>() {
            @Override
            public void onChanged(PagedList<BuyItem> searchItems) {
                adapter.submitList(searchItems);
            }
        });
        recyclerView.setAdapter(adapter);
    }
    public static void showEmpty(String text){
        emptyView.setVisibility(View.VISIBLE);
        emptyViewText.setText(text);
    }


}
