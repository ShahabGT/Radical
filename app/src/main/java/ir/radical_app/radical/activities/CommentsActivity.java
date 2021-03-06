package ir.radical_app.radical.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.widget.EmojiEditText;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.R;
import ir.radical_app.radical.arch.Comments.CommentsAdapter;
import ir.radical_app.radical.arch.Comments.CommentsViewModel;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.models.JsonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsViewModel itemViewModel;
    private LinearLayoutManager layoutManager;
    private EmojiEditText commentText;
    private CommentsAdapter adapter;
    private ImageView send;
    private String shopid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        init();

    }

    private void init() {

        commentText = findViewById(R.id.comments_text);
        send = findViewById(R.id.comments_send);

        recyclerView = findViewById(R.id.comments_recycler);
        layoutManager = new LinearLayoutManager(CommentsActivity.this);
       //layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        onClicks();

        showData();
        shopid=getIntent().getExtras().getString("shopid","");
        if(shopid.isEmpty())
            onBackPressed();

        recyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.post(() -> {
                    if (adapter != null)
                        recyclerView.scrollToPosition(0);

                });
            }
        });




    }

    private void onClicks() {

        send.setOnClickListener(v -> {
            String comment = commentText.getText().toString();
            if (comment.isEmpty())
                MyToast.Companion.create(CommentsActivity.this, getString(R.string.comment_error));
            else {
                try {
                    byte[] data = commentText.getText().toString().getBytes("UTF-8");
                    sendComment(Base64.encodeToString(data, Base64.DEFAULT),shopid);
                }catch (Exception e){ }
            }


        });
    }

    private void sendComment(String comment,String shopId) {
        commentText.setEnabled(false);
        String number = MySharedPreference.Companion.getInstance(CommentsActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(CommentsActivity.this).getAccessToken();

        if (number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(CommentsActivity.this, getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(CommentsActivity.this).clear();
            startActivity(new Intent(CommentsActivity.this, SplashActivity.class));
            CommentsActivity.this.finish();
        } else {
            RetrofitClient.Companion.getInstance().getApi()
                    .comment(number, accessToken, shopId, comment)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            commentText.setEnabled(true);
                            if (response.isSuccessful()) {
                                itemViewModel.invalidateData();
                                commentText.setText("");
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        MyToast.Companion.create(CommentsActivity.this, getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(CommentsActivity.this).clear();
                                        startActivity(new Intent(CommentsActivity.this, SplashActivity.class));
                                        CommentsActivity.this.finish();
                                        break;

                                    default:
                                        MyToast.Companion.create(CommentsActivity.this, getString(R.string.general_error));
                                        break;
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            commentText.setEnabled(true);
                            MyToast.Companion.create(CommentsActivity.this, getString(R.string.general_error));

                        }
                    });
        }
    }

    private void showData() {
        itemViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);

        adapter = new CommentsAdapter(CommentsActivity.this);
        itemViewModel.itemPagedList.observe(this, adapter::submitList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }
}
