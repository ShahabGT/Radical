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
import java.util.ArrayList;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import ir.radical_app.radical.activities.ErrorActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.CategoryAdapter2;
import ir.radical_app.radical.adapters.MainViewPager;
import ir.radical_app.radical.arch.Shops.ShopsAdapter;
import ir.radical_app.radical.arch.Shops.ShopsItem;
import ir.radical_app.radical.arch.Shops.ShopsViewModel;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.CategoryModel;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import ir.radical_app.radical.models.SliderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LoadingDialog dialog;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter2 categoryAdapter;
    private MyDatabase myDatabase;
    private View v;



    public HomeFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_home, container, false);
        myDatabase = new MyDatabase(getContext());

        init();
        return v;
    }

    private void init(){
        recyclerView = v.findViewById(R.id.home_recycler);
        categoryRecyclerView = v.findViewById(R.id.home_category_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        getCategories();



    }

    private void showData(PagerAdapter pagerAdapter){
        ShopsViewModel itemViewModel = ViewModelProviders.of(this).get(ShopsViewModel.class);
        final ShopsAdapter adapter = new ShopsAdapter(getContext(),getActivity(),pagerAdapter);
        itemViewModel.itemPagedList.observe(this, new Observer<PagedList<ShopsItem>>() {

            @Override
            public void onChanged(PagedList<ShopsItem> shopsItems) {
                adapter.submitList(shopsItems);
            }
        });
        recyclerView.setAdapter(adapter);
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
    private void getCategories(){
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
                    .getCategories().enqueue(new Callback<CategoryModel>() {
                @Override
                public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                    if(response.isSuccessful()){
                        loadingDialog(true);
                        if(response.body().getList().size()>5){

                            ArrayList<JsonResponse> list = response.body().getList();
                            ArrayList<SliderResponse> slider = response.body().getSlider();

                            categoryAdapter = new CategoryAdapter2(getContext(),list,getActivity());
                            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,true));
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            showData(new MainViewPager(getActivity(),slider));
                            myDatabase.deleteCategoriesTable();
                            myDatabase.saveCategories(list);
                        }else{
                            startActivity(new Intent(getActivity(), ErrorActivity.class));
                            getActivity().finish();
                        }
                    }else{
                        startActivity(new Intent(getActivity(), ErrorActivity.class));
                        getActivity().finish();
                    }
                }

                @Override
                public void onFailure(Call<CategoryModel> call, Throwable t) {
                    loadingDialog(true);
                    startActivity(new Intent(getActivity(), ErrorActivity.class));
                    getActivity().finish();
                }
            });
        }
    }

}
