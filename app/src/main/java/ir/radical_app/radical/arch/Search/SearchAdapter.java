package ir.radical_app.radical.arch.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.willy.ratingbar.ScaleRatingBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.R;

public class SearchAdapter extends PagedListAdapter<SearchItem, SearchAdapter.ItemViewHolder> {

    private Context mCtx;
    private FragmentActivity activity;

    public SearchAdapter(Context mCtx, FragmentActivity activity) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
        this.activity=activity;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.row_shops, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final SearchItem item = getItem(position);

        if (item != null) {
            holder.id.setText(item.getShopId());
            holder.title.setText(item.getName());
            holder.category.setText(item.getCategoryName());
            holder.discount.setText(item.getMaxDiscount()+"%");
            holder.region.setText(item.getRegion());
            Uri uri = Uri.parse(mCtx.getString(R.string.main_image_url,item.getShopId()));
            holder.pic.setImageURI(uri);
            int count = Integer.parseInt(item.getCount()+"");
            float sum = Float.parseFloat(item.getSum()+"");
            float mean = 0;
            if(count!=0 || sum!=0){
                 mean =  sum/count;
            }

            holder.rate.setRating(mean);

        } else {
            Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show();
        }

        holder.itemView.setOnClickListener(View-> {
                if(!MyUtils.Companion.checkInternet(mCtx)){
                    MyToast.Companion.create(mCtx,mCtx.getString(R.string.internet_error));
                    return;
                }
                ShopFragment shopFragment = new ShopFragment();
                shopFragment.setShopId(holder.id.getText().toString());

                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                        .add(R.id.main_container,shopFragment)
                        .addToBackStack(null)
                        .commit();
        });

    }


    private static DiffUtil.ItemCallback<SearchItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SearchItem>() {
                @Override
                public boolean areItemsTheSame(SearchItem oldItem, SearchItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(SearchItem oldItem, SearchItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

            private ScaleRatingBar rate;
            private TextView title,category,discount,region,id;
            private SimpleDraweeView pic;

         ItemViewHolder(View v) {
            super(v);
             title = v.findViewById(R.id.shops_row_title);
             category = v.findViewById(R.id.shops_row_category);
             discount = v.findViewById(R.id.shops_row_discount);
             id = v.findViewById(R.id.shops_row_id);
             pic = v.findViewById(R.id.shops_row_image);
             pic = v.findViewById(R.id.shops_row_image);
             region = v.findViewById(R.id.shops_row_region);
             rate = v.findViewById(R.id.shops_row_rate);
        }
    }
}
