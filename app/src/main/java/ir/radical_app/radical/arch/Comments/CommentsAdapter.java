package ir.radical_app.radical.arch.Comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.DateConverter;

public class CommentsAdapter extends PagedListAdapter<CommentItem, CommentsAdapter.ItemViewHolder> {

    private Context mCtx;

    public CommentsAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.row_comments, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final CommentItem item = getItem(position);

        if (item != null) {
            holder.id.setText(item.getShopId());
            holder.user.setText(item.getName());
            String date = item.getDate();
            DateConverter dateConverter = new DateConverter();
            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.date.setText(mCtx.getString(R.string.messages_model3,dateConverter.toString(),date.substring(11)));
            holder.comment.setText(item.getComment());
        } else {
            Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show();
        }


    }


    private static DiffUtil.ItemCallback<CommentItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CommentItem>() {
                @Override
                public boolean areItemsTheSame(CommentItem oldItem, CommentItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(CommentItem oldItem, CommentItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView id, comment, user, date;

        ItemViewHolder(View v) {
            super(v);
            user = v.findViewById(R.id.comments_items_name);
            comment = v.findViewById(R.id.comments_items_comment);
            date = v.findViewById(R.id.comments_items_time);
            id=v.findViewById(R.id.comments_items_id);

        }
    }
}
