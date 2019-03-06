package ir.radical_app.radical.arch.Bookmark;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import ir.radical_app.radical.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkDataSource extends PageKeyedDataSource<Integer, BookmarkItem> {

    static final int SIZE = 10;
    private static final int START = 0;
    private String number;
    private String accessToken;


    public BookmarkDataSource(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, BookmarkItem> callback) {
        RetrofitClient.getInstance()
                .getApi()
                .getBookmarks(START,SIZE, number,accessToken)
                .enqueue(new Callback<BookmarkResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkResponse> call, Response<BookmarkResponse> response) {

                        if(response.body() != null){

                            callback.onResult(response.body().getData(), null, START + SIZE);

                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkResponse> call, Throwable t) {
                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, BookmarkItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, BookmarkItem> callback) {

        RetrofitClient.getInstance()
                .getApi()
                .getBookmarks(params.key,SIZE,number,accessToken)
                .enqueue(new Callback<BookmarkResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkResponse> call, Response<BookmarkResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<BookmarkResponse> call, Throwable t) {

                    }
                });


    }
}
