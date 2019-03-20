package ir.radical_app.radical.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import ir.radical_app.radical.R;

public class ShopsImagesAdapter extends PagerAdapter {

    private int size;
    private String shopId;
    private Context context;

    public ShopsImagesAdapter(Context context ,String shopId, int size){
        this.context =context;
        this.size = size;
        this.shopId = shopId;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleDraweeView imageView = new SimpleDraweeView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        String pos= (position+1)+"";
        Uri uri = Uri.parse(context.getString(R.string.shop_image_url,shopId,pos));
        imageView.setImageURI(uri);
        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((SimpleDraweeView) object);
    }
}
