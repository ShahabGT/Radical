package ir.radical_app.radical.arch.Upgrade;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.fragments.BuyHistoryFragment;
import ir.radical_app.radical.fragments.UpgradeHistoryFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeDataSource extends PageKeyedDataSource<Integer, UpgradeItem> {

    static final int SIZE = 10;
    private static final int START = 0;
    private String number;
    private String accessToken;


    UpgradeDataSource(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, UpgradeItem> callback) {
        RetrofitClient.Companion.getInstance()
                .getApi()
                .getUpgradeHistory(number,accessToken,START,SIZE )
                .enqueue(new Callback<UpgradeResponse>() {
                    @Override
                    public void onResponse(Call<UpgradeResponse> call, Response<UpgradeResponse> response) {

                        if(response.body() != null){

                            callback.onResult(response.body().getData(), null, START + SIZE);

                        }else{
                            UpgradeHistoryFragment.showEmpty("خریدی انجام نداده اید!");
                        }
                    }

                    @Override
                    public void onFailure(Call<UpgradeResponse> call, Throwable t) {
                        UpgradeHistoryFragment.showEmpty("خریدی انجام نداده اید!");

                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UpgradeItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, UpgradeItem> callback) {

        RetrofitClient.Companion.getInstance()
                .getApi()
                .getUpgradeHistory(number,accessToken,params.key,SIZE)
                .enqueue(new Callback<UpgradeResponse>() {
                    @Override
                    public void onResponse(Call<UpgradeResponse> call, Response<UpgradeResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<UpgradeResponse> call, Throwable t) {

                    }
                });


    }
}
