package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.CategoryAdapter;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.SpacesItemDecoration;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.CategoryModel;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private LoadingDialog dialog;
    private MyDatabase myDatabase;


    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_category, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        myDatabase = new MyDatabase(getContext());
        recyclerView = v.findViewById(R.id.category_recycler);
        //getData();
        loadData();
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

    private void loadData(){
        adapter = new CategoryAdapter(getContext(),myDatabase.getCategories(),getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL,false));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager( 3, RecyclerView.VERTICAL));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setAdapter(adapter);

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
                    .getCategories().enqueue(new Callback<CategoryModel>() {
                @Override
                public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                    if(response.isSuccessful()){
                        loadingDialog(true);
                        if(response.body().getList().size()>5){
                         //   adapter = new CategoryAdapter(getContext(),response.body().getList());
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL,false));
                            recyclerView.setAdapter(adapter);
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
                public void onFailure(Call<CategoryModel> call, Throwable t) {
                    loadingDialog(true);
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    MyToast.Create(getContext(),getString(R.string.general_error));
                }
            });
        }
    }

}
