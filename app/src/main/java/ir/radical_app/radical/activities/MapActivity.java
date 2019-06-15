package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import ir.map.sdk_map.annotations.IconFactory;
import ir.map.sdk_map.annotations.MarkerOptions;
import ir.map.sdk_map.camera.CameraUpdateFactory;
import com.mapbox.android.core.location.LocationEngineListener;
import ir.map.sdk_map.geometry.LatLng;
import ir.map.sdk_map.location.LocationComponent;
import ir.map.sdk_map.maps.MapirMap;
import ir.map.sdk_map.maps.SupportMapFragment;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MyToast;


public class MapActivity extends AppCompatActivity implements LocationEngineListener {

    private MapirMap mapirMap;
    private Location myLocation;
    private LocationManager manager;
    private String lat,lon,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        try {
            lat = getIntent().getExtras().getString("lat", "");
            lon = getIntent().getExtras().getString("lon", "");
            name = getIntent().getExtras().getString("name", "");
        }catch (NullPointerException e){
            onBackPressed();
        }



        ((TextView)findViewById(R.id.map_shop_name)).setText(name);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView))
                .getMapAsync(Map -> {

                    MapActivity.this.mapirMap = Map;
                    if (MapActivity.this.mapirMap != null) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                    MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                    (Activity) MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationComponent component = MapActivity.this.mapirMap.getLocationComponent();
                            component.activateLocationComponent(MapActivity.this);
                            component.setLocationComponentEnabled(true);
                            if (component.getLocationEngine() != null) {
                                component.getLocationEngine().addLocationEngineListener(MapActivity.this);
                            }
                            LatLng storePosition = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                            mapirMap.addMarker(new MarkerOptions().setTitle(name).position(storePosition).icon(IconFactory.getInstance(MapActivity.this).fromResource(R.mipmap.ic_pin)));

                            mapirMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storePosition,15),1000);

                        }
                    }
                });


        findViewById(R.id.map_myloc).setOnClickListener(View->{

            if(myLocation!=null) mapirMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
            else
            if(!checkIfLocationEnabled()) {
                trunGPS();
                MyToast.Companion.create(MapActivity.this,getString(R.string.txt_gps));
            }else
                MyToast.Companion.createShort(MapActivity.this,getString(R.string.txt_gps_loading));

        });


    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }

    private boolean checkIfLocationEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void trunGPS(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


}
