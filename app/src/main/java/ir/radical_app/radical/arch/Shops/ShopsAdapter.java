package ir.radical_app.radical.arch.Shops;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.willy.ratingbar.ScaleRatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.R;

public class ShopsAdapter extends PagedListAdapter<ShopsItem, ShopsAdapter.ItemViewHolder> {

    private Context mCtx;
    private FragmentActivity activity;

    public ShopsAdapter(Context mCtx, FragmentActivity activity) {
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

        final ShopsItem item = getItem(position);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShopActivity.shopId=holder.id.getText().toString();
//                mCtx.startActivity(new Intent(mCtx, ShopActivity.class));
                ShopFragment shopFragment = new ShopFragment();
                shopFragment.setShopId(holder.id.getText().toString());
                FragmentManager fm = activity.getSupportFragmentManager();
                while(fm.getBackStackEntryCount()>1)
                    fm.popBackStackImmediate();


                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                        .add(R.id.main_container,shopFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



    }


    private static DiffUtil.ItemCallback<ShopsItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ShopsItem>() {
                @Override
                public boolean areItemsTheSame(ShopsItem oldItem, ShopsItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @Override
                public boolean areContentsTheSame(ShopsItem oldItem, ShopsItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

            private ScaleRatingBar rate;
            private TextView title,category,discount,region,id;
            private SimpleDraweeView pic;
            private ImageView share;

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