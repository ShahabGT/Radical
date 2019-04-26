package ir.radical_app.radical.arch.Shops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tmall.ultraviewpager.UltraViewPager;
import com.willy.ratingbar.ScaleRatingBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.R;

public class ShopsAdapter extends PagedListAdapter<ShopsItem, ShopsAdapter.ItemViewHolder> {

    private Context mCtx;
    private FragmentActivity activity;
    private static int HEADER_TYPE=0;
    private static int LIST_TYPE=1;
    private PagerAdapter pagerAdapter;

    public ShopsAdapter(Context mCtx, FragmentActivity activity,PagerAdapter pagerAdapter) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
        this.activity=activity;
        this.pagerAdapter = pagerAdapter;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return HEADER_TYPE;
        return LIST_TYPE;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =null;
        if(viewType==LIST_TYPE)
            view = LayoutInflater.from(mCtx).inflate(R.layout.row_shops, parent, false);
        else if (viewType == HEADER_TYPE)
            view = LayoutInflater.from(mCtx).inflate(R.layout.slider_layout, parent, false);

        return new ItemViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {


        if(holder.viewType==LIST_TYPE) {
            final ShopsItem item = getItem(position);

            if (item != null) {
                holder.id.setText(item.getShopId());
                holder.title.setText(item.getName());
                holder.category.setText(item.getCategoryName());
                holder.discount.setText(item.getMaxDiscount() + "%");
                holder.region.setText(item.getRegion());
                Uri uri = Uri.parse(mCtx.getString(R.string.main_image_url, item.getShopId()));
                holder.pic.setImageURI(uri);
                int count = Integer.parseInt(item.getCount() + "");
                float sum = Float.parseFloat(item.getSum() + "");
                float mean = 0;
                if (count != 0 || sum != 0) {
                    mean = sum / count;
                }

                holder.rate.setRating(mean);

            } else {
                Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show();
            }

            holder.itemView.setOnClickListener(View-> {
                    if (!MyUtils.Companion.checkInternet(mCtx)) {
                        MyToast.Companion.create(mCtx, mCtx.getString(R.string.internet_error));
                        return;
                    }
                    ShopFragment shopFragment = new ShopFragment();
                    shopFragment.setShopId(holder.id.getText().toString());
                    FragmentManager fm = activity.getSupportFragmentManager();
                    while (fm.getBackStackEntryCount() > 1)
                        fm.popBackStackImmediate();


                    activity.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                            .add(R.id.main_container, shopFragment)
                            .addToBackStack(null)
                            .commit();
            });
        }else if (holder.viewType==HEADER_TYPE){
            holder.ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
            holder.ultraViewPager.setAdapter(pagerAdapter);
            holder.ultraViewPager.initIndicator();
            holder.ultraViewPager.setAutoMeasureHeight(true);
            holder.ultraViewPager.getIndicator()
                    .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                    .setMargin(0,0,0,16)
                    .setFocusColor(0xFFFCD736)
                    .setNormalColor(0xFFECEFF1)
                    .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mCtx.getResources().getDisplayMetrics()))
                    .build();

            holder.ultraViewPager.setInfiniteLoop(true);
            holder.ultraViewPager.setAutoScroll(5000);
        }



    }


    private static DiffUtil.ItemCallback<ShopsItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ShopsItem>() {
                @Override
                public boolean areItemsTheSame(ShopsItem oldItem, ShopsItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(ShopsItem oldItem, ShopsItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

            private ScaleRatingBar rate;
            private TextView title,category,discount,region,id;
            private SimpleDraweeView pic;
            private UltraViewPager ultraViewPager;
            private int viewType;

         ItemViewHolder(View v, int type) {
             super(v);

             if(type==LIST_TYPE) {
                 title = v.findViewById(R.id.shops_row_title);
                 category = v.findViewById(R.id.shops_row_category);
                 discount = v.findViewById(R.id.shops_row_discount);
                 id = v.findViewById(R.id.shops_row_id);
                 pic = v.findViewById(R.id.shops_row_image);
                 pic = v.findViewById(R.id.shops_row_image);
                 region = v.findViewById(R.id.shops_row_region);
                 rate = v.findViewById(R.id.shops_row_rate);
                 viewType=1;
             }else if(type==HEADER_TYPE){
                 ultraViewPager=v.findViewById(R.id.slider_viewpager);
                 viewType=0;
             }
        }
    }
}
