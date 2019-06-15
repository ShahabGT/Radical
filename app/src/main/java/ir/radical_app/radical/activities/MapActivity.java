package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import ir.map.sdk_common.MapirLatLng;
import ir.map.sdk_map.annotations.IconFactory;
import ir.map.sdk_map.annotations.MarkerOptions;
import ir.map.sdk_map.annotations.Polyline;
import ir.map.sdk_map.annotations.PolylineOptions;
import ir.map.sdk_map.camera.CameraUpdate;
import ir.map.sdk_map.camera.CameraUpdateFactory;
import com.mapbox.android.core.location.LocationEngineListener;

import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_map.geometry.LatLng;
import ir.map.sdk_map.geometry.LatLngBounds;
import ir.map.sdk_map.location.LocationComponent;
import ir.map.sdk_map.maps.MapirMap;
import ir.map.sdk_map.maps.SupportMapFragment;
import ir.map.sdk_services.RouteMode;
import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.base.ResponseListener;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.MyToast;

import ir.map.sdk_services.models.*;
import ir.radical_app.radical.route.MapirPolyUtil;
import ir.radical_app.radical.route.MapirSphericalUtil;

public class MapActivity extends AppCompatActivity implements LocationEngineListener, ResponseListener<MaptexRouteResponse> {

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

            if(myLocation!=null) {
                mapirMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
                MapirLatLng latLng1 = new MapirLatLng(Double.valueOf(lat), Double.valueOf(lon));
                MapirLatLng latLng2 = new MapirLatLng(myLocation.getLatitude(), myLocation.getLongitude());
                new ServiceHelper()
                        .getRouteInfo(latLng1, latLng2, RouteMode.BASIC, MapActivity.this);
            }else
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


    @Override
    public void onSuccess(MaptexRouteResponse response) {
        updateRoutingInfo(response);
    }

    @Override
    public void onError(MaptexError error) {
        MyToast.Companion.create(this,getString(R.string.general_error));

    }

    public void updateRoutingInfo(MaptexRouteResponse routingInfo) {

        Polyline mainRouteLine = null;
        Polyline alternateRouteLine = null;
        List<LatLng> latLngsRouteListMain;
        List<LatLng> latLngsRouteListAlternative;
        List<MaptexManeuver> mainRouteManeuver = new ArrayList<>();
        List<MaptexManeuver> alternateRouteManeuver = new ArrayList<>();
        List<MaptexStepsItem> steps = new ArrayList<>();
        List<LatLng> mainIntersectionsPoints = new ArrayList<>();
        List<Polyline> fullMainIntersectionsLines = new ArrayList<>();

        if (mainRouteLine != null) {
            mainRouteLine.remove();
        }
        if (alternateRouteLine != null) {
            alternateRouteLine.remove();
        }

        latLngsRouteListMain = new ArrayList<>();
        latLngsRouteListAlternative = new ArrayList<>();
        //Check Route Info Null
        //Check And Init Main Route If Exists
        if (routingInfo != null && routingInfo.routes != null) {
            for (int i = 0; i < routingInfo.routes.size(); i++) {
                if (routingInfo.routes.get(i).legs != null) {
                    for (int j = 0; j < routingInfo.routes.get(i).legs.size(); j++) {
                        if (routingInfo.routes.get(i).legs.get(j).steps != null) {
                            for (int k = 0; k < routingInfo.routes.get(i).legs.get(j).steps.size(); k++) {
                                if (routingInfo.routes.get(i).legs.get(j).steps.get(k) != null) {
                                    if (i == 0) {
                                        steps.add(routingInfo.routes.get(i).legs.get(j).steps.get(k));
                                        mainRouteManeuver.add(routingInfo.routes.get(i).legs.get(j).steps.get(k).maneuver);
                                        latLngsRouteListMain.addAll(MapirPolyUtil.decode(routingInfo.routes.get(i).legs.get(j).steps.get(k).geometry));
                                    } else if (i == 1) {
                                        alternateRouteManeuver.add(routingInfo.routes.get(i).legs.get(j).steps.get(k).maneuver);
                                        latLngsRouteListAlternative.addAll(MapirPolyUtil.decode(routingInfo.routes.get(i).legs.get(j).steps.get(k).geometry));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Create Main Line And Show It On Map
            if (latLngsRouteListMain.size() > 0) {
                mapirMap.addPolyline(new PolylineOptions().width(10).addAll(latLngsRouteListMain)
                        .color((getResources().getColor(R.color.colorAccent))));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : latLngsRouteListMain) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mapirMap.animateCamera(cu);
            }

            //Create Alternate Line And Show It On Map
            if (latLngsRouteListAlternative.size() > 0) {
                mapirMap.addPolyline(new PolylineOptions().width(5).color(getResources().getColor(R.color.primary))
                        .addAll(latLngsRouteListAlternative));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : latLngsRouteListAlternative) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mapirMap.animateCamera(cu);

            }

            //Draw Intersections Lines
//            SphericalUtil.computeOffsetOrigin()
            if (mainRouteManeuver != null && mainRouteManeuver.size() > 0) {
                for (int i = 0; i < mainRouteManeuver.size(); i++) {
                    mainIntersectionsPoints.clear();
                    fullMainIntersectionsLines.clear();
                    LatLng base = new LatLng(mainRouteManeuver.get(i).location.get(1), mainRouteManeuver.get(i).location.get(0));
                    LatLng basePrevious = MapirSphericalUtil.computeOffset(base, 5, mainRouteManeuver.get(i).bearingBefore + 180);
                    LatLng baseNext = MapirSphericalUtil.computeOffset(base, 5, mainRouteManeuver.get(i).bearingAfter);

                    switch (mainRouteManeuver.get(i).type) {
                        case "depart":

                            break;
                        case "turn":
                            mainIntersectionsPoints.add(basePrevious);
                            mainIntersectionsPoints.add(base);
                            mainIntersectionsPoints.add(baseNext);
                            fullMainIntersectionsLines.add(mapirMap.addPolyline(
                                    new PolylineOptions().color(Color.YELLOW)
                                            .addAll(mainIntersectionsPoints)));
                            break;
                    }
                }
            }
        }

    }

}
