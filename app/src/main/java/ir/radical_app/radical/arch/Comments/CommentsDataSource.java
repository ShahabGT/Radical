package ir.radical_app.radical.arch.Comments;


import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsDataSource extends PageKeyedDataSource<Integer, CommentItem> {

    static final int SIZE = 10;
    private static final int START = 0;




    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, CommentItem> callback) {
        RetrofitClient.Companion.getInstance()
                .getApi()
                .getComments(START,SIZE, Const.Companion.getShopid())
                .enqueue(new Callback<CommentsResponse>() {
                    @Override
                    public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {

                        if(response.body() != null){

                            callback.onResult(response.body().getData(), null, START + SIZE);

                        }
                    }

                    @Override
                    public void onFailure(Call<CommentsResponse> call, Throwable t) {
                    }
                });

    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, CommentItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, CommentItem> callback) {

        RetrofitClient.Companion.getInstance()
                .getApi()
                .getComments(params.key,SIZE,Const.Companion.getShopid())
                .enqueue(new Callback<CommentsResponse>() {
                    @Override
                    public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {

                        if(response.body() != null){
                            Integer key = params.key + SIZE;
                            callback.onResult(response.body().getData(), key);
                        }

                    }

                    @Override
                    public void onFailure(Call<CommentsResponse> call, Throwable t) {

                    }
                });


    }
}
