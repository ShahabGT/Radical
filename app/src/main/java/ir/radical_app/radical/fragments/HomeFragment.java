package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import java.util.ArrayList;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.activities.ErrorActivity;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.adapters.CategoryAdapter;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private MainViewPager adapter;
    int[] images = new int[]{R.drawable.t1,R.drawable.t2,R.drawable.t3,R.drawable.t4,R.drawable.t5};
    private RecyclerView recyclerView;
    private  UltraViewPager ultraViewPager;
    private LinearLayoutManager layoutManager;
    private LoadingDialog dialog;
    private  RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private MyDatabase myDatabase;



    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        myDatabase = new MyDatabase(getContext());
        getCategories();
      //  initViewPager(v);

        //showData(v);
        init(v);
        return v;
    }

    private void init(View v){
        recyclerView = v.findViewById(R.id.home_recycler);
        categoryRecyclerView = v.findViewById(R.id.home_category_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //   recyclerView.setHasFixedSize(true);

        //     recyclerView.setNestedScrollingEnabled(false);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (layoutManager.findFirstVisibleItemPosition() > 0) {
//                    ultraViewPager.setVisibility(View.GONE);
//                } else {
//                    ultraViewPager.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
        showData();

    }

    private void showData(){
        ShopsViewModel itemViewModel = ViewModelProviders.of(this).get(ShopsViewModel.class);
        final ShopsAdapter adapter = new ShopsAdapter(getContext(),getActivity());
//        itemViewModel.itemPagedList.observeForever(new Observer<PagedList<ShopsItem>>() {
//            @Override
//            public void onChanged(PagedList<ShopsItem> shopsItems) {
//                adapter.submitList(shopsItems);
//
//            }
//        });
        itemViewModel.itemPagedList.observe(this, new Observer<PagedList<ShopsItem>>() {

            @Override
            public void onChanged(PagedList<ShopsItem> shopsItems) {
                adapter.submitList(shopsItems);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void initViewPager(View v){
        ultraViewPager = v.findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        adapter = new MainViewPager(getContext(),images);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().build();
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAutoScroll(5000);

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
    private void getCategories(){
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

                            ArrayList<JsonResponse> list = response.body().getList();
//                            JsonResponse jsonResponse = new JsonResponse();
//                            jsonResponse.setName("همه");
//                            jsonResponse.setCategoryId("0");
//                            list.add(jsonResponse);
//                            list.addAll(response.body().getList());
                            categoryAdapter = new CategoryAdapter(getContext(),list,getActivity());
                            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
                            categoryRecyclerView.setAdapter(categoryAdapter);
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
