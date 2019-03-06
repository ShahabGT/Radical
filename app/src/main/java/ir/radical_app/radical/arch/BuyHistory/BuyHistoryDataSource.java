package ir.radical_app.radical.arch.BuyHistory;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import ir.radical_app.radical.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyHistoryDataSource extends PageKeyedDataSource<Integer, BuyItem> {

    static final int SIZE = 10;
    private static final int START = 0;
    private String number;
    private String accessToken;


    public BuyHistoryDataSource(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, BuyItem> callback) {
        RetrofitClient.getInstance()
                .getApi()
                .getBuyHistory(number,accessToken,START,SIZE )
                .enqueue(new Callback<BuyHistoryResponse>() {
                    @Override
                    public void onResponse(Call<BuyHistoryResponse> call, Response<BuyHistoryResponse> response) {

                        if(response.body() != null){

                            callback.onResult(response.body().getData(), null, START + SIZE);

                        }
                    }

                    @Override
                    public void onFailure(Call<BuyHistoryResponse> call, Throwable t) {
                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, BuyItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, BuyItem> callback) {

        RetrofitClient.getInstance()
                .getApi()
                .getBuyHistory(number,accessToken,params.key,SIZE)
                .enqueue(new Callback<BuyHistoryResponse>() {
                    @Override
                    public void onResponse(Call<BuyHistoryResponse> call, Response<BuyHistoryResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<BuyHistoryResponse> call, Throwable t) {

                    }
                });


    }
}
