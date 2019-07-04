package ir.radical_app.radical.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MyListener;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.InfoDialog;
import ir.radical_app.radical.dialogs.IntroDialog;
import ir.radical_app.radical.dialogs.NewVersionDialog;
import ir.radical_app.radical.dialogs.ProfileDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.fragments.AboutFragment;
import ir.radical_app.radical.fragments.BuyFragment;
import ir.radical_app.radical.fragments.BuyHistoryFragment;
import ir.radical_app.radical.fragments.CategoryFragment;
import ir.radical_app.radical.fragments.HelpFragment;
import ir.radical_app.radical.fragments.HomeFragment;
import ir.radical_app.radical.fragments.MessagesFragment;
import ir.radical_app.radical.fragments.ProfileFragment;
import ir.radical_app.radical.fragments.ShareFragment;
import ir.radical_app.radical.fragments.ShopFragment;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import ir.radical_app.radical.fragments.StatsFragment;
import ir.radical_app.radical.fragments.SupportFragment;
import ir.radical_app.radical.fragments.UpgradeHistoryFragment;
import ir.radical_app.radical.fragments.WalletFragment;
import ir.radical_app.radical.models.JsonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce;
    private Drawer drawer;
    private MaterialSearchView searchView;
    private ImageView readQr;
    private final int QR_REQUEST_CODE = 668;
    private final int CAMERA_REQUEST_CODE = 658;
    private MyDatabase myDatabase;
    private TextView toolbarDiscount;
    private Toolbar appToolbar;
    private ConstraintLayout messages;
    private TextView messagesBadge;
    private FloatingActionButton nearMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignToolbar();
        init();

    }

    private void showInfoDialog() {
        if (MySharedPreference.Companion.getInstance(this).getFirstSearch()) {
            MySharedPreference.Companion.getInstance(this).setFirstSearch();
        } else {
            return;
        }


        InfoDialog introDialog = new InfoDialog(this, "از این قسمت بر اساس آدرس، اسم و... مراکز مورد نظرتو جستجو کن");
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void showIntroDialog() {
        IntroDialog introDialog = new IntroDialog(this, (boolean isClicked)-> {
                if (isClicked)
                    setFragment(new HelpFragment());
        });
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void showNewVersionDialog(String urgent) {

        NewVersionDialog newVersionDialog = new NewVersionDialog(this, urgent);
        if (urgent.equals("0"))
            newVersionDialog.setCancelable(true);
        else
            newVersionDialog.setCancelable(false);
        newVersionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newVersionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        newVersionDialog.show();
        Window window = newVersionDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void init() {
        myDatabase = new MyDatabase(MainActivity.this);
        searchView = findViewById(R.id.search_view);
        toolbarDiscount = findViewById(R.id.toolbar_amount);
        readQr = findViewById(R.id.toolbar_qr);
        messages = findViewById(R.id.toolbar_messages);
        messagesBadge = findViewById(R.id.toolbar_messages_badge);
        doubleBackToExitPressedOnce = false;
        nearMe=findViewById(R.id.main_nearme);

        search();
        onClicks();

        navigationDrawer();
        validate();

    }

    private void showGuide() {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.vazir);

        TapTargetView.showFor(MainActivity.this,                 // `this` is an Activity
                TapTarget.forView(readQr, "بارکد اسکنر", "تو هر مرکز یه تابلو هست که با اسکن کردن QR کد میتونی مراحل تخفیف گرفتنت رو به راحتی انجام بدی")
                        .outerCircleColor(R.color.yellow)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.yellow)  // Specify the color of the description text
                        .textColor(R.color.black)            // Specify a color for both the title and description text
                        .textTypeface(typeface)  // Specify a typeface for the text
                        .descriptionTypeface(typeface)
                        .titleTypeface(typeface)
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }

                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        showGuide2();
                    }
                });

    }

    private void showGuide2() {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.vazir);

        TapTargetView.showFor(MainActivity.this,                 // `this` is an Activity
                TapTarget.forToolbarMenuItem(appToolbar, R.id.action_menu, "نوار منو", "امکانات و نحوه استفاده از رادیکال را از این قسمت دنبال کنید")
                        .outerCircleColor(R.color.yellow)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.yellow)  // Specify the color of the description text
                        .textColor(R.color.black)            // Specify a color for both the title and description text
                        .textTypeface(typeface)  // Specify a typeface for the text
                        .descriptionTypeface(typeface)
                        .titleTypeface(typeface)
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }

                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        showCase();
                    }
                });

    }

    private void showCase() {
        drawer.openDrawer();
        drawer.getRecyclerView().postDelayed(()-> {

                View v = drawer.getRecyclerView().getChildAt(2);
                new SimpleTooltip.Builder(MainActivity.this)
                        .anchorView(v)
                        .onDismissListener(SimpleTooltip->
                                showCase2()
                        )
                        .dismissOnOutsideTouch(true)
                        .focusable(true)
                        .modal(true)
                        .overlayMatchParent(true)
                        .transparentOverlay(false)
                        .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                        .text("از این قسمت میتونید تعداد روز دلخواه خودتون رو فعال و از تمام مراکز رادیکال تخفیف بگیرید")
                        .gravity(Gravity.BOTTOM)
                        .animated(true)
                        .build()
                        .show();
            }
        , 200);
    }

    private void showCase2() {
        drawer.getRecyclerView().post(()-> {
                View v = drawer.getRecyclerView().getChildAt(3);
                new SimpleTooltip.Builder(MainActivity.this)
                        .anchorView(v)
                        .onDismissListener(SimpleTooltip->
                                showCase3()
                        )
                        .dismissOnOutsideTouch(true)
                        .focusable(true)
                        .modal(true)
                        .transparentOverlay(false)
                        .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                        .text("از این قسمت میتونید کیف پول رادیکالیتونو رو شارژ و از هدایای رادیکال برای خرید روز استفاده کنید")
                        .gravity(Gravity.BOTTOM)
                        .animated(true)
                        .build()
                        .show();
            }
        );
    }

    private void showCase3() {
        drawer.getRecyclerView().post(()-> {
                View v = drawer.getRecyclerView().getChildAt(6);
                new SimpleTooltip.Builder(MainActivity.this)
                        .anchorView(v)
                        .dismissOnOutsideTouch(true)
                        .onDismissListener(SimpleTooltip-> {
                                drawer.closeDrawer();
                                showIntroDialog();
                        })
                        .focusable(true)
                        .modal(true)
                        .transparentOverlay(false)
                        .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                        .text("تمام چیزایی که بابتش پول پرداخت میکنی تو رادیکال موجوده")
                        .gravity(Gravity.TOP)
                        .animated(true)
                        .build()
                        .show();

        });
    }

    private void onClicks() {
        readQr.setOnClickListener(View-> {

                if (MySharedPreference.Companion.getInstance(MainActivity.this).getPlan() != 1)
                    requestCamera();
                else
                    showUpgradeDialog();

        });

        messages.setOnClickListener(v-> {
                MyUtils.Companion.removeNotification(MainActivity.this);
                setFragment(new MessagesFragment());
        });

        toolbarDiscount.setOnClickListener(v->
                setFragment(new StatsFragment())
        );

        nearMe.setOnClickListener(v->startActivity(new Intent(MainActivity.this,NearMeActivity.class)));
    }

    private void search() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    MyUtils.Companion.hideKeyboard(MainActivity.this);
                    FragmentManager fm = getSupportFragmentManager();
                    while (fm.getBackStackEntryCount() > 1)
                        fm.popBackStackImmediate();
                    Const.Companion.setWord(query);
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
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        if (!MyUtils.Companion.checkInternet(this)) {
            MyToast.Companion.create(this, getString(R.string.internet_error));
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1)
            fm.popBackStackImmediate();


        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void goHome() {
        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 0)
            fm.popBackStackImmediate();


        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .add(R.id.main_container, new HomeFragment())
                .addToBackStack(null)
                .commit();
        drawer.setSelection(1, false);
    }

    private void assignToolbar() {
        appToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(appToolbar);
    }

    private void initNavigationHeader(View v) {
        ImageView edit = v.findViewById(R.id.header_edit);
        edit.setOnClickListener(View-> {
                setFragment(new ProfileFragment());
                drawer.closeDrawer();
        });
        String name = MySharedPreference.Companion.getInstance(MainActivity.this).getName();
        if (name.isEmpty()) {
            name = "کاربر رادیکال";
        }
        String number = MySharedPreference.Companion.getInstance(MainActivity.this).getNumber();
        TextView hName = v.findViewById(R.id.header_name);
        hName.setText(name);
        TextView hNumber = v.findViewById(R.id.header_number);
        hNumber.setText(number);


    }

    private void navigationDrawer() {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withStickyHeader(R.layout.drawer_header)
                .withHasStableIds(true)
                .withDrawerGravity(Gravity.RIGHT)
                .withHeaderDivider(true)
                .withSliderBackgroundColor(Color.parseColor("#ECEFF1"))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_mainpage)).withIcon(R.mipmap.navigation_mainpage).withSelectable(false).withIdentifier(1),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_profile)).withIcon(R.mipmap.navigation_profile).withSelectable(false).withIdentifier(2),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_plan)).withIcon(R.mipmap.navigation_plan).withSelectable(false).withIdentifier(6),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_wallet)).withIcon(R.mipmap.navigation_wallet).withSelectable(false).withIdentifier(3),

                        new ExpandableBadgeDrawerItem().withName(getString(R.string.navigation_history)).withIcon(R.mipmap.navigation_history).withIdentifier(4).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub1)).withLevel(1).withIcon(R.mipmap.navigation_history).withIdentifier(41),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub2)).withLevel(1).withIcon(R.mipmap.navigation_history_shop).withIdentifier(42),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_history_sub3)).withLevel(1).withIcon(R.mipmap.navigation_history_chart).withIdentifier(43)
                        ),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_catagory)).withIcon(R.mipmap.navigation_catagory).withSelectable(false).withIdentifier(5),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_favorites)).withIcon(R.mipmap.navigation_favorites).withSelectable(false).withIdentifier(7),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_share)).withIcon(R.mipmap.navigation_share).withSelectable(false).withIdentifier(8),
                        new DividerDrawerItem(),
                        new ExpandableBadgeDrawerItem().withName(getString(R.string.navigation_support)).withIcon(R.mipmap.navigation_support).withIdentifier(9).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red)).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_support_sub1)).withLevel(1).withIcon(R.mipmap.navigation_support_message).withIdentifier(91).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red)),
                                new SecondaryDrawerItem().withName(getString(R.string.navigation_support_sub2)).withLevel(1).withIcon(R.mipmap.navigation_support_chat).withIdentifier(92).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red))
                        ),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_coop)).withIcon(R.mipmap.navigation_coop).withSelectable(false).withIdentifier(10),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_help)).withIcon(R.mipmap.navigation_help).withSelectable(false).withIdentifier(11).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red)),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_about)).withIcon(R.mipmap.navigation_about).withSelectable(false).withIdentifier(12),
                        new PrimaryDrawerItem().withName(getString(R.string.navigation_exit)).withIcon(R.mipmap.navigation_exit).withSelectable(false).withIdentifier(13)
                )
                .withOnDrawerItemClickListener((View view, int position, IDrawerItem drawerItem)-> {

                        if ((int) drawerItem.getIdentifier() != 4 && (int) drawerItem.getIdentifier() != 9)
                            drawer.getExpandableExtension().collapse();
                        switch ((int) drawerItem.getIdentifier()) {
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
                                setFragment(new UpgradeHistoryFragment());
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
                                MyUtils.Companion.hideKeyboard(MainActivity.this);
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

                                break;
                            case 91:
                                setFragment(new MessagesFragment());
                                MyUtils.Companion.removeNotification(MainActivity.this);
                                updateBadge();

                                break;
                            case 92:
                                if (!MySharedPreference.Companion.getInstance(MainActivity.this).getName().isEmpty()) {
                                    setFragment(new SupportFragment());
                                    myDatabase.updateSupportDB();
                                    updateBadge();
                                    MyUtils.Companion.removeNotification(MainActivity.this);
                                } else
                                    showProfileDialog();

                                break;
                            case 10:
                                openCoOp();
                                break;
                            case 11:
                                setFragment(new HelpFragment());
                                MySharedPreference.Companion.getInstance(MainActivity.this).setFirstHelp();
                                drawer.updateBadge(11, null);

                                break;
                            case 12:
                                setFragment(new AboutFragment());
                                break;
                            case 13:
                                showAlertDialog();
                                break;

                        }
                        return false;

                })
                .build();
        drawer.setSelection(1, true);
        initNavigationHeader(drawer.getStickyHeader());
        if (MySharedPreference.Companion.getInstance(MainActivity.this).getFirstHelp())
            drawer.updateBadge(11, new StringHolder("جدید"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==R.id.action_menu ){
            drawer.openDrawer();
            return true;
        }else
            return super.onOptionsItemSelected(item);

    }

    private int checkPermission() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
    }

    private void showExplanation() {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_camera));
        builder.setMessage(getString(R.string.permission_camera_message));
        builder.setPositiveButton(getString(R.string.permission_allow), (DialogInterface dialog, int which)->
                requestPermission()
        );
        builder.setNegativeButton(getString(R.string.permission_dismiss), (DialogInterface dialog, int which)->
                dialog.dismiss()
        );

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestCamera() {
        if (checkPermission() != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                showExplanation();
            }else if(!MySharedPreference.Companion.getInstance(MainActivity.this).getCameraPermission()){
                requestPermission();
                MySharedPreference.Companion.getInstance(MainActivity.this).setCameraPermission();
            } else {
                MyToast.Companion.create(MainActivity.this, getString(R.string.permission_camera_toast));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            readQr();
        }

    }

    private void readQr() {
        startActivityForResult(new Intent(MainActivity.this, QrcodeActivity.class), QR_REQUEST_CODE);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BuyFragment buyFragment = new BuyFragment();
                buyFragment.setQrId(data.getStringExtra("qrCode"));
                buyFragment.setpShopId(data.getStringExtra("shopid") + "");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, buyFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readQr();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return;
        }
        if (drawer.isDrawerOpen()) {
            drawer.getExpandableExtension().collapse();
            drawer.closeDrawer();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            doubleBackToExitPressedOnce = true;
            MyToast.Companion.create(MainActivity.this, getString(R.string.txt_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else
            getSupportFragmentManager().popBackStackImmediate();


    }

    @Override
    protected void onResume() {
        super.onResume();
        externalLinks();
        updateBadge();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RADICAL_BROADCAST.equals(intent.getAction())) {
                updateBadge();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(RADICAL_BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    private void updateBadge() {
        boolean haveUnreadMessage = false;

        if (myDatabase.getReadMessages() != 0) {
            haveUnreadMessage = true;
            drawer.updateBadge(91, new StringHolder("1"));
            messagesBadge.setVisibility(View.VISIBLE);
        } else {
            drawer.updateBadge(91, null);
            messagesBadge.setVisibility(View.GONE);
        }

        if (myDatabase.getReadSupport() != 0) {
            haveUnreadMessage = true;
            drawer.updateBadge(92, new StringHolder("1"));
        } else {
            drawer.updateBadge(92, null);
        }

        if (haveUnreadMessage) {
            drawer.updateBadge(9, new StringHolder("جدید"));

        } else {
            drawer.updateBadge(9, null);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void validate() {
        String number = MySharedPreference.Companion.getInstance(MainActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(MainActivity.this).getAccessToken();
        String platformVersion = Build.VERSION.SDK_INT + "";

        if (number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(MainActivity.this, getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(MainActivity.this).clear();
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            MainActivity.this.finish();
        } else {
            RetrofitClient.Companion.getInstance().getApi()
                    .validate("android", getVersionCode(), platformVersion, number, accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("ok")) {
                                    if (response.body().getAmount() != null && !response.body().getAmount().isEmpty()) {
                                        String amount = getString(R.string.amount_model, MyUtils.Companion.moneySeparator(response.body().getAmount()));
                                        toolbarDiscount.setText(getString(R.string.toolbar_discount, amount));
                                        toolbarDiscount.setVisibility(View.VISIBLE);
                                    }

                                    MySharedPreference.Companion.getInstance(MainActivity.this).setPlan(Integer.parseInt(response.body().getPlanid()));
                                    if (response.body().getName() != null && !response.body().getName().isEmpty()) {
                                        MySharedPreference.Companion.getInstance(MainActivity.this).setName(response.body().getName());
                                        View v = drawer.getStickyHeader();
                                        TextView headerName = v.findViewById(R.id.header_name);
                                        headerName.setText(response.body().getName());

                                    }
                                    int newVersion = Integer.parseInt(response.body().getVersion());
                                    MySharedPreference.Companion.getInstance(MainActivity.this).setNewVersion(newVersion);

                                    boolean externalLinkRes=externalLinks();

                                    if (!externalLinkRes) {
                                        int myVersion = getVersionCode();
                                        if (MySharedPreference.Companion.getInstance(MainActivity.this).getFirstBoot()) {
                                            MySharedPreference.Companion.getInstance(MainActivity.this).setFirstBoot();
                                            showGuide();
                                        } else {
                                            if (myVersion < newVersion)
                                                showNewVersionDialog(response.body().getUrgent());
                                        }
                                    }


                                } else if (response.body().getMessage().equals("wrongaccess")) {
                                    MyToast.Companion.create(MainActivity.this, getString(R.string.access_error));
                                    MySharedPreference.Companion.getInstance(MainActivity.this).clear();
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


    private boolean externalLinks() {

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String id;

            if (intent.getData().toString().startsWith("https")) {
                id = intent.getData().toString().substring(32);
            } else {
                id = intent.getData().toString().substring(27);
            }
            if (!id.equals("0"))
                goToShop(id);

            setIntent(null);
            return true;

        }else{
            try {
                if (getIntent() != null) {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        if (!bundle.getString("notidata", "").isEmpty()) {
                            String data = bundle.getString("notidata", "");
                            if(data.equals("notification"))
                                setFragment(new MessagesFragment());
                            else if (data.equals("support"))
                                setFragment(new SupportFragment());
                            setIntent(null);
                            return true;
                        }
                    }
                }

            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private int getVersionCode() {
        int version = 0;
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
        upgradeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void showProfileDialog() {
        ProfileDialog profileDialog = new ProfileDialog(MainActivity.this);

        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        profileDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        profileDialog.show();
        Window window = profileDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void showAlertDialog() {
        ir.radical_app.radical.dialogs.AlertDialog alertDialog = new ir.radical_app.radical.dialogs.AlertDialog(MainActivity.this, getString(R.string.txt_exit_profile),
                (boolean isClicked)-> {
                MySharedPreference.Companion.getInstance(MainActivity.this).clear();
                myDatabase.deleteMessageTable();
                myDatabase.deleteSupportTable();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Const.FCM_TOPIC);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                MainActivity.this.finish();
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }


    private void goToShop(String id) {
        if (!MyUtils.Companion.checkInternet(this)) {
            MyToast.Companion.create(this, getString(R.string.internet_error));
            return;
        }
        ShopFragment shopFragment = new ShopFragment();
        shopFragment.setShopId(id);
        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1)
            fm.popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                .add(R.id.main_container, shopFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCoOp() {
        String urlString = "https://radical-app.ir/cooperation";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            startActivity(intent);
        }
    }
}
