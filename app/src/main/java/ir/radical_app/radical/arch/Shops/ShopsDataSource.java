package ir.radical_app.radical.arch.Shops;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import ir.radical_app.radical.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopsDataSource extends PageKeyedDataSource<Integer, ShopsItem> {

    static final int SIZE = 10;
    private static final int START = 0;




    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, ShopsItem> callback) {
        RetrofitClient.getInstance()
                .getApi()
                .getShops(START,SIZE, "")
                .enqueue(new Callback<ShopsResponse>() {
                    @Override
                    public void onResponse(Call<ShopsResponse> call, Response<ShopsResponse> response) {

                        if(response.body() != null){

                            callback.onResult(response.body().getData(), null, START + SIZE);

                        }
                    }

                    @Override
                    public void onFailure(Call<ShopsResponse> call, Throwable t) {
                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, ShopsItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, ShopsItem> callback) {

        RetrofitClient.getInstance()
                .getApi()
                .getShops(params.key,SIZE,"")
                .enqueue(new Callback<ShopsResponse>() {
                    @Override
                    public void onResponse(Call<ShopsResponse> call, Response<ShopsResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<ShopsResponse> call, Throwable t) {

                    }
                });


    }
}
