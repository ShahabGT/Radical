package ir.radical_app.radical.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngineListener;

import java.util.List;
import java.util.function.Consumer;

import ir.map.sdk_map.annotations.IconFactory;
import ir.map.sdk_map.annotations.Marker;
import ir.map.sdk_map.annotations.MarkerOptions;
import ir.map.sdk_map.camera.CameraUpdateFactory;
import ir.map.sdk_map.geometry.LatLng;
import ir.map.sdk_map.location.LocationComponent;
import ir.map.sdk_map.maps.MapirMap;
import ir.map.sdk_map.maps.SupportMapFragment;
import ir.radical_app.radical.R;
import ir.radical_app.radical.arch.Shops.ShopsItem;
import ir.radical_app.radical.arch.Shops.ShopsResponse;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearMeActivity extends AppCompatActivity implements LocationEngineListener {

    private MapirMap mapirMap;
    private Location myLocation;
    private LocationManager manager;
    private Double newLat=0.0, newLon=0.0;
    private Double startLat =0.0, startLon =0.0;
    private final int Location_REQUEST_CODE = 658;
    private int[] categories= {R.mipmap.ic_loc1,
            R.mipmap.ic_loc2,
            R.mipmap.ic_loc3,
            R.mipmap.ic_loc4,
            R.mipmap.ic_loc5,
            R.mipmap.ic_loc6,
            R.mipmap.ic_loc7,
            R.mipmap.ic_loc8,
            R.mipmap.ic_loc9,
            R.mipmap.ic_loc10,
            R.mipmap.ic_loc11,
            R.mipmap.ic_loc12,
            R.mipmap.ic_loc13,
            R.mipmap.ic_loc14,
            R.mipmap.ic_loc15,
            R.mipmap.ic_loc16,
            R.mipmap.ic_loc17,
            R.mipmap.ic_loc18,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);

        init();
    }

    private void init(){
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapInit();



    }

    private void mapInit() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView))
                .getMapAsync(Map -> {

                    NearMeActivity.this.mapirMap = Map;
                    if (NearMeActivity.this.mapirMap != null) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                    NearMeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_REQUEST_CODE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                    NearMeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Location_REQUEST_CODE);

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationComponent component = NearMeActivity.this.mapirMap.getLocationComponent();
                            component.activateLocationComponent(NearMeActivity.this);
                            component.setLocationComponentEnabled(true);
                            if (component.getLocationEngine() != null) {
                                component.getLocationEngine().addLocationEngineListener(NearMeActivity.this);
                            }

                            mapirMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.3277,47.0778), 11), 1000);
                            getMyLocation();
                        }
                    }

                });
        findViewById(R.id.nearme_btn).setOnClickListener(v->getMyLocation());
    }

    @Override
    public void onConnected() {
    }
    private void getMyLocation(){
        if (myLocation != null) {
            mapirMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));

        }else if (!checkIfLocationEnabled()) {
            turnGPS();
            MyToast.Companion.create(NearMeActivity.this, getString(R.string.txt_gps));
        } else
            MyToast.Companion.createShort(NearMeActivity.this, getString(R.string.txt_gps_loading));
    }

    private void turnGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
    private boolean checkIfLocationEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if(startLat ==0) {
            mapirMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
            startLat =myLocation.getLatitude();
            startLon =myLocation.getLongitude();
            getNearMePlaces();
        }

        newLat=myLocation.getLatitude();
        newLon=myLocation.getLongitude();

        if(MyUtils.Companion.getDistanceFromLatLonInKm(startLat, startLon,newLat,newLon)>120) {

            startLat =newLat;
            startLon =newLon;
            getNearMePlaces();

        }
    }


    private void getNearMePlaces(){
        String number = MySharedPreference.Companion.getInstance(NearMeActivity.this).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(NearMeActivity.this).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(NearMeActivity.this,getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(NearMeActivity.this).clear();
            startActivity(new Intent(NearMeActivity.this, SplashActivity.class));
            NearMeActivity.this.finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
                    .getNearMe(number,accessToken,startLat,startLon)
                    .enqueue(new Callback<ShopsResponse>() {
                        @Override
                        public void onResponse(Call<ShopsResponse> call, Response<ShopsResponse> response) {
                            if(response.isSuccessful()){
                                mapirMap.clear();
                                List<ShopsItem> data = response.body().getData();
                                for(ShopsItem item : data){
                                    LatLng storePosition = new LatLng(Double.valueOf(item.getLat()), Double.valueOf(item.getLon()));
                                    mapirMap.addMarker(
                                            new MarkerOptions()
                                                    .snippet(item.getCategoryName())
                                                    .setTitle(item.getName())
                                                    .position(storePosition)
                                                    .icon(IconFactory.getInstance(NearMeActivity.this).fromResource(categories[Integer.parseInt(item.getCategoryId())-1])));

                                }

                                mapirMap.setOnMarkerClickListener(marker->{
                                   // Toast.makeText(NearMeActivity.this, marker.getTitle()+"\n"+marker.getPosition(), Toast.LENGTH_SHORT).show();

                                    return false;

                                });


                            }
                        }

                        @Override
                        public void onFailure(Call<ShopsResponse> call, Throwable t) {

                        }
                    });

        }
    }
}
