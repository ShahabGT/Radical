package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.adapters.IntroPagerAdapter;
import ir.radical_app.radical.fragments.IntroFragment;
import ir.radical_app.radical.R;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

public class IntroActivity extends AppCompatActivity {


    private IntroPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        adapter = new IntroPagerAdapter(getSupportFragmentManager());

        IntroFragment fg1 = new IntroFragment();
        fg1.setTitle("صرفه جویی در هزینه ها");
        fg1.setMessage("مخارج ماهیانه و سالانه خود را به طرز عجیبی تا 40% کاهش دهید!");
        fg1.setLogo(R.drawable.fi1);

        IntroFragment fg2 = new IntroFragment();
        fg2.setTitle("هرآنچه که نیاز دارید");
        fg2.setMessage("تمام مراکز خدماتی و فروشگاهی که به آن نیاز دارید در اختیار شماست");
        fg2.setLogo(R.drawable.fi2);


        IntroFragment fg3 = new IntroFragment();
        fg3.setTitle("گلچین شده از بهترین ها");
        fg3.setMessage("مراکزی که بهترین فاکتور ها را برای یک خرید خوب در خود دارند و برای شما رادیکالی ها همیشه در تخفیف هستند");
        fg3.setLogo(R.drawable.fi3);

        fg3.setShowBtn();

        adapter.addFragment(fg1);
        adapter.addFragment(fg2);
        adapter.addFragment(fg3);
        initViewPager();

    }
    private void initViewPager(){
        UltraViewPager ultraViewPager = findViewById(R.id.intro_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);

        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setMargin(0,16,0,16)
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(0xFFFCD736)
                .setNormalColor(0xFFFFFFFF)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        ultraViewPager.getIndicator().build();
    }
}
