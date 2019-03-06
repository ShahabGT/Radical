package ir.radical_app.radical.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.classes.PermissionUtil;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.IntroDialog;
import ir.radical_app.radical.dialogs.NewVersionDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.fragments.AboutFragment;
import ir.radical_app.radical.fragments.BuyFragment;
import ir.radical_app.radical.fragments.BuyHistoryFragment;
import ir.radical_app.radical.fragments.CategoryFragment;
import ir.radical_app.radical.fragments.ShareFragment;
import ir.radical_app.radical.fragments.HelpFragment;
import ir.radical_app.radical.fragments.HomeFragment;
import ir.radical_app.radical.fragments.MessagesFragment;
import ir.radical_app.radical.fragments.PlanHistoryFragment;
import ir.radical_app.radical.fragments.ProfileFragment;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import ir.radical_app.radical.fragments.StatsFragment;
import ir.radical_app.radical.fragments.SupportFragment;
import ir.radical_app.radical.fragments.WalletFragment;
import ir.radical_app.radical.listeners.DialogListener;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce;
    private Drawer drawer;
    private ActionBar actionBar;
    private MaterialSearchView searchView;
    private ImageView readQr;
    private final int QR_REQUEST_CODE = 668;

    private final int CAMERA_REQUEST_CODE = 658;
    private GuideView mGuideView;
    private View hamBtn;
    private MyDatabase myDatabase;

    private GuideView.Builder builder;
    private TextView toolbarDiscount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignToolbar();
        init();


    }

    private void showIntroDialog() {
        IntroDialog introDialog = new IntroDialog(this, new DialogListener() {
            @Override
            public void buttonClicked(boolean isClicked) {
                if(isClicked)
                    setFragment(new HelpFragment());
                setToolbarTitle(getString(R.string.navigation_help));

            }
        });
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    private void showNewVersionDialog() {
        NewVersionDialog newVersionDialog = new NewVersionDialog(this);
        newVersionDialog.setCancelable(true);
        newVersionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newVersionDialog.show();
        Window window = newVersionDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    private void init(){
        myDatabase = new MyDatabase(MainActivity.this);
        searchView = findViewById(R.id.search_view);
        toolbarDiscount = findViewById(R.id.toolbar_amount);
        readQr = findViewById(R.id.toolbar_qr);
        doubleBackToExitPressedOnce = false;

      //  showGuide();
        search();
        onClicks();
        validate();
        navigationDrawer();
    }
    private void onClicks(){
        readQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCamera();
            }
        });

        toolbarDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new StatsFragment());
            }
        });
    }
    private void search(){
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!query.isEmpty()) {
                    MyUtils.hideKeyboard(MainActivity.this);
                    FragmentManager fm = getSupportFragmentManager();
                    while (fm.getBackStackEntryCount() > 1)
                        fm.popBackStackImmediate();
                    Const.word=query;
                    SpecificCategoryFragment searchFragment = new SpecificCategoryFragment();
                    searchFragment.setSearch(true);
                    searchFragment.setSearch(true);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                            .add(R.id.main_container, searchFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void setFragment(Fragment fragment){

        FragmentManager fm = getSupportFragmentManager();
        while(fm.getBackStackEntryCount()>1)
            fm.popBackStackImmediate();


        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                .add(R.id.main_container,fragment)
                .addToBackStack(null)
                .commit();
    }
    private void goHome(){
        FragmentManager fm = getSupportFragmentManager();
        while(fm.getBackStackEntryCount()>0)
            fm.popBackStackImmediate();


        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                .add(R.id.main_container,new HomeFragment())
                .addToBackStack(null)
                .commit();
        setToolbarTitle(getString(R.string.navigation_mainpage));
        drawer.setSelection(1,false);
    }
    private void assignToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar =getSupportActionBar();

    }
    private void setToolbarTitle(String title){
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview_layout, null);
        ((TextView)v.findViewById(R.id.title)).setText(title);
        ((TextView)v.findViewById(R.id.title)).setGravity(Gravity.CENTER);
        actionBar.setCustomView(v);
    }
    private AccountHeader navigationHeader(){

        String name=MySharedPreference.getInstance(MainActivity.this).getName();
        if(name.isEmpty()){
            name="کاربر رادیکال";
        }
        String number=MySharedPreference.getInstance(MainActivity.this).getNumber();
        Drawable icon = ResourcesCompat.getDrawable(getResources(),R.drawable.male,null);
        if(MySharedPreference.getInstance(MainActivity.this).getSex().equals("female"))
        {
            icon = ResourcesCompat.getDrawable(getResources(),R.drawable.female,null);

        }
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg3)
                .addProfiles(
                        new ProfileDrawerItem().withDisabledTextColorRes(R.color.green).withName(name).withEmail(number).withIcon(icon)
                )
                .withTranslucentStatusBar(true)
                .build();

    }
    private void navigationDrawer(){
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withHasStableIds(true)
                .withDrawerGravity(Gravity.RIGHT)
                .withAccountHeader(navigationHeader(),true)
                .withHeaderDivider(true)
                .withSliderBackgroundColor(Color.parseColor("#ECEFF1"))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_mainpage)).withIcon(R.mipmap.navigation_mainpage).withSelectable(false).withIdentifier(1),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_profile)).withIcon(R.mipmap.navigation_profile).withSelectable(false).withIdentifier(2),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_wallet)).withIcon(R.mipmap.navigation_wallet).withSelectable(false).withIdentifier(3),

                        new ExpandableDrawerItem().withName(getString(R.string.navigation_history)).withIcon(R.mipmap.navigation_history).withIdentifier(4).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub1)).withLevel(1).withIcon(R.mipmap.navigation_history).withIdentifier(41),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub2)).withLevel(1).withIcon(R.mipmap.navigation_history_shop).withIdentifier(42),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub3)).withLevel(1).withIcon(R.mipmap.navigation_history_chart).withIdentifier(43)
                        ),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_catagory)).withIcon(R.mipmap.navigation_catagory).withSelectable(false).withIdentifier(5),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_plan)).withIcon(R.mipmap.navigation_plan).withSelectable(false).withIdentifier(6),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_favorites)).withIcon(R.mipmap.navigation_favorites).withSelectable(false).withIdentifier(7),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_share)).withIcon(R.mipmap.navigation_share).withSelectable(false).withIdentifier(8),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName(getString(R.string.navigation_support)).withIcon(R.mipmap.navigation_support).withIdentifier(9).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_support_sub1)).withLevel(1).withIcon(R.mipmap.navigation_support_message).withIdentifier(91),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_support_sub2)).withLevel(1).withIcon(R.mipmap.navigation_support_chat).withIdentifier(92)
                        ),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_coop)).withIcon(R.mipmap.navigation_coop).withSelectable(false).withIdentifier(10),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_help)).withIcon(R.mipmap.navigation_help).withSelectable(false).withIdentifier(11),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_about)).withIcon(R.mipmap.navigation_about).withSelectable(false).withIdentifier(12),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_exit)).withIcon(R.mipmap.navigation_exit).withSelectable(false).withIdentifier(13)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if((int)drawerItem.getIdentifier()!=4 && (int)drawerItem.getIdentifier()!=9)
                            drawer.getExpandableExtension().collapse();
                        switch ((int)drawerItem.getIdentifier()){
                            case 1:
                                goHome();
                                break;
                            case 2:
                                setFragment(new ProfileFragment());
                                break;
                            case 3:
                                setFragment(new WalletFragment());
                                break;
                            case 41:
                                setFragment(new PlanHistoryFragment());
                                break;
                            case 42:
                                setFragment(new BuyHistoryFragment());
                                break;
                            case 43:
                                setFragment(new StatsFragment());
                                break;
                            case 5:
                                setFragment(new CategoryFragment());
                                break;
                            case 6:
                                showUpgradeDialog();
                                break;
                            case 7:
                                MyUtils.hideKeyboard(MainActivity.this);
                                FragmentManager fm = getSupportFragmentManager();
                                while (fm.getBackStackEntryCount() > 1)
                                    fm.popBackStackImmediate();
                                SpecificCategoryFragment searchFragment = new SpecificCategoryFragment();
                                searchFragment.setBookmark(true);
                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                                        .add(R.id.main_container, searchFragment)
                                        .addToBackStack(null)
                                        .commit();
                                break;
                            case 8:
                                setFragment(new ShareFragment());
                                setToolbarTitle(getString(R.string.navigation_share));

                                break;
                            case 91:
                                setFragment(new MessagesFragment());

                                break;
                            case 92:
                                setFragment(new SupportFragment());

                                break;
                            case 10:
                                break;
                            case 11:
                                setFragment(new HelpFragment());
                                setToolbarTitle(getString(R.string.navigation_help));

                                break;
                            case 12:
                                setFragment(new AboutFragment());
                                setToolbarTitle(getString(R.string.navigation_about));

                                break;
                            case 13:
                                MySharedPreference.getInstance(MainActivity.this).clear();
                                myDatabase.deleteMessageTable();
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(Const.FCM_TOPIC);
                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                MainActivity.this.finish();
                                break;

                        }
                        return false;
                    }
                })
                .build();
        drawer.setSelection(1,true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        hamBtn = menu.findItem(R.id.action_menu).getActionView();
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_menu:
                drawer.openDrawer();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    private int checkPermission(){
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
    }
    private void showExplanation(){
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_camera));
        builder.setMessage(getString(R.string.permission_camera_message));
        builder.setPositiveButton(getString(R.string.permission_allow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });
        builder.setNegativeButton(getString(R.string.permission_dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }
    private void requestCamera(){
        PermissionUtil permissionUtil = new PermissionUtil(MainActivity.this);

        if(checkPermission()!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)){
                showExplanation();
            }

            else if(!permissionUtil.check("camera")){
                requestPermission();
                permissionUtil.update("camera");
            }else{
                MyToast.Create(MainActivity.this,getString(R.string.permission_camera_toast));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }
        }else{
            readQr();
        }

    }
    private void readQr() {
        startActivityForResult(new Intent(MainActivity.this,QrcodeActivity.class),QR_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== QR_REQUEST_CODE){
            if(resultCode== RESULT_OK){
                BuyFragment buyFragment = new BuyFragment();
                buyFragment.setQrId( data.getStringExtra("qrCode"));
//                setFragment(buyFragment);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container,buyFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    readQr();
                }
        }
    }

    @Override
    public void onBackPressed() {
         if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            else if(drawer.isDrawerOpen()){
                drawer.getExpandableExtension().collapse();

                drawer.closeDrawer();
            }else{
                if (doubleBackToExitPressedOnce) {
                    MainActivity.this.finish();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                MyToast.Create(MainActivity.this,getString(R.string.txt_exit));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);}
        } else {
             if (searchView.isSearchOpen()) {
                 searchView.closeSearch();
             }
            getSupportFragmentManager().popBackStackImmediate();
            if(getSupportFragmentManager().getBackStackEntryCount()==1) {
                setToolbarTitle(getString(R.string.navigation_mainpage));
                drawer.setSelection(1, false);
            }


        }

    }
    private void showGuide(){
        builder = new GuideView.Builder(this)
                .setTitle("جستجو")
                .setContentText("برای جستجوی مکان مورد نظر از این قسمت استفاده کنید")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.center)
                .setDismissType(DismissType.outside)
                .setTargetView(searchView)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        showGuide2();
                    }
                });

        mGuideView = builder.build();
        mGuideView.show();
    }
    private void showGuide2(){
        builder = new GuideView.Builder(this)
                .setTitle("بارکد خوان")
                .setContentText("برای خواندن مستقیم بارکد فروشگاه مورد نظر از این قسمت استفاده کنید")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.center)
                .setDismissType(DismissType.outside)
                .setTargetView(readQr)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        showGuide3();

                    }
                });

        mGuideView = builder.build();
        mGuideView.show();
    }
    private void showGuide3(){
        builder = new GuideView.Builder(this)
                .setTitle("بارکد خوان")
                .setContentText("برای خواندن مستقیم بارکد فروشگاه مورد نظر از این قسمت استفاده کنید")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.center)
                .setDismissType(DismissType.outside)
                .setTargetView(hamBtn)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                    }
                });

        mGuideView = builder.build();
        mGuideView.show();
    }
    private void validate(){
        String number = MySharedPreference.getInstance(MainActivity.this).getNumber();
        String accessToken = MySharedPreference.getInstance(MainActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(MainActivity.this,getString(R.string.data_error));
            MySharedPreference.getInstance(MainActivity.this).clear();
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            MainActivity.this.finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .validate("android",number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if(response.isSuccessful()){
                                if(response.body().getMessage().equals("ok")){
                                    if(response.body().getAmount()!=null && !response.body().getAmount().isEmpty() ){
                                        String amount = getString(R.string.amount_model,MyUtils.moneySeparator(response.body().getAmount()));
                                        toolbarDiscount.setText(getString(R.string.toolbar_discount,amount));
                                    }

                                    int myVersion =getVersionCode();
                                    int newVersion = Integer.parseInt(response.body().getVersion());
                                    Intent intent = getIntent();

                                    if(response.body().getName()!=null && !response.body().getName().isEmpty()){
                                        MySharedPreference.getInstance(MainActivity.this).setName(response.body().getName());

                                    }

                                    if(intent!=null && intent.getData()!=null){
                                        String id;
                                        if (intent.getData().toString().startsWith("https")){
                                            id = intent.getData().toString().substring(32);
                                        }
                                        else{
                                            id = intent.getData().toString().substring(27);

                                        }

                                        goToShop(id);

                                    }else{
                                        if(MySharedPreference.getInstance(MainActivity.this).getFirstBoot()){
                                            MySharedPreference.getInstance(MainActivity.this).setFirstBoot(false);
                                            showIntroDialog();
                                        }else{
                                            if(myVersion<newVersion)
                                                showNewVersionDialog();
                                        }
                                    }
                                }else if(response.body().getMessage().equals("wrongaccess")){
                                    MyToast.Create(MainActivity.this,getString(R.string.access_error));
                                    MySharedPreference.getInstance(MainActivity.this).clear();
                                    startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                    MainActivity.this.finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {

                        }
                    });


        }
    }
    private int getVersionCode(){
        int version=0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = (int) PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
    private void showUpgradeDialog() {
        UpgradeDialog upgradeDialog = new UpgradeDialog(MainActivity.this);

        upgradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void goToShop(String id){
        ShopFragment shopFragment = new ShopFragment();
        shopFragment.setShopId(id);
        FragmentManager fm = getSupportFragmentManager();
        while(fm.getBackStackEntryCount()>1)
            fm.popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout)
                .add(R.id.main_container,shopFragment)
                .addToBackStack(null)
                .commit();
    }
}
