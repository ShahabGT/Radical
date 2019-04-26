package ir.radical_app.radical.arch.Category;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopsCategoryDataSource extends PageKeyedDataSource<Integer, ShopsCategoryItem> {

    static final int SIZE = 10;
    private static final int START = 0;




    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, ShopsCategoryItem> callback) {
        RetrofitClient.Companion.getInstance()
                .getApi()
                .getCategoryShops(START,SIZE, Const.Companion.getCategory())
                .enqueue(new Callback<ShopsCategoryResponse>() {
                    @Override
                    public void onResponse(Call<ShopsCategoryResponse> call, Response<ShopsCategoryResponse> response) {
                        if(response.body() != null){
                                callback.onResult(response.body().getData(), null, START + SIZE);
                        }else{
                            SpecificCategoryFragment.showEmpty("فروشگاهی موجود نیست!");
                        }
                    }

                    @Override
                    public void onFailure(Call<ShopsCategoryResponse> call, Throwable t) {
                        SpecificCategoryFragment.showEmpty("فروشگاهی موجود نیست!");

                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, ShopsCategoryItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, ShopsCategoryItem> callback) {

        RetrofitClient.Companion.getInstance()
                .getApi()
                .getCategoryShops(params.key,SIZE,Const.Companion.getCategory())
                .enqueue(new Callback<ShopsCategoryResponse>() {
                    @Override
                    public void onResponse(Call<ShopsCategoryResponse> call, Response<ShopsCategoryResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<ShopsCategoryResponse> call, Throwable t) {

                    }
                });


    }
}
