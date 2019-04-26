package ir.radical_app.radical.fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.adapters.CategoryAdapter;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.R;



public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private MyDatabase myDatabase;


    public CategoryFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_category, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        myDatabase = new MyDatabase(getContext());
        recyclerView = v.findViewById(R.id.category_recycler);
        loadData();
    }


    private void loadData(){
        adapter = new CategoryAdapter(getContext(),myDatabase.getCategories(),getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);

    }

}
