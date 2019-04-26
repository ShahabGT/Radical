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
import ir.radical_app.radical.R;
import ir.radical_app.radical.arch.Upgrade.UpgradeHistoryAdapter;
import ir.radical_app.radical.arch.Upgrade.UpgradeItem;
import ir.radical_app.radical.arch.Upgrade.UpgradeViewModel;
import ir.radical_app.radical.classes.MySharedPreference;


public class UpgradeHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static ConstraintLayout emptyView;
    private static TextView emptyViewText;

    public UpgradeHistoryFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_upgrade_history, container, false);
        init(v);

        return v;
    }
    private void init(View v){
        emptyView = v.findViewById(R.id.empty_view);
        emptyViewText = v.findViewById(R.id.empty_text);
        recyclerView = v.findViewById(R.id.upgrade_history_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        showData();
    }

    private void showData(){
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();
        UpgradeViewModel viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T)new UpgradeViewModel(number,accessToken);
            }
        }).get(UpgradeViewModel.class);
        final UpgradeHistoryAdapter adapter = new UpgradeHistoryAdapter(getContext());

        viewModel.itemPagedList.observe(this, new Observer<PagedList<UpgradeItem>>() {
            @Override
            public void onChanged(PagedList<UpgradeItem> searchItems) {
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
