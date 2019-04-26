package ir.radical_app.radical.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import ir.radical_app.radical.models.SliderResponse;

public class MainViewPager extends PagerAdapter {

    private FragmentActivity context;
    private ArrayList<SliderResponse> slider;

    public MainViewPager(FragmentActivity context, ArrayList<SliderResponse> slider){
        this.context=context;
        this.slider=slider;



    }

    @Override
    public int getCount() {
        return slider.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        SliderResponse response = slider.get(position);

        SimpleDraweeView imageView = new SimpleDraweeView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        Uri uri = Uri.parse(context.getString(R.string.slider_image_url,response.getPic()));
        imagePipeline.evictFromDiskCache(uri);

        imageView.setImageURI(uri);

        String link = response.getLink()+"";

        imageView.setOnClickListener(View->{

            if(link.startsWith("shop=")){
                openShop(link.replace("shop=",""));
            }else if(link.startsWith("category=")){
                openCategory(link.replace("category=",""));
            }else if (link.startsWith("http")||link.startsWith("www")){
                openLinks(link);
            }

        }
        );
        container.addView(imageView);
        return imageView;
    }

    private void openLinks(String link){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    private void openShop(String id){
        if (!MyUtils.Companion.checkInternet(context)) {
            MyToast.Companion.create(context, context.getString(R.string.internet_error));
            return;
        }
        ShopFragment shopFragment = new ShopFragment();
        shopFragment.setShopId(id);
        FragmentManager fm = context.getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1)
            fm.popBackStackImmediate();


        context.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .add(R.id.main_container, shopFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCategory(String id){
        if(!MyUtils.Companion.checkInternet(context)){
            MyToast.Companion.create(context,context.getString(R.string.internet_error));
            return;
        }

        FragmentManager fm = context.getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1)
            fm.popBackStackImmediate();


        Const.Companion.setCategory(id);
        context.getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, new SpecificCategoryFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
