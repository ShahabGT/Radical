package ir.radical_app.radical.activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.card.MaterialCardView;
import com.mapbox.android.core.location.LocationEngineListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_common.MapirLatLng;
import ir.map.sdk_map.annotations.IconFactory;
import ir.map.sdk_map.annotations.MarkerOptions;
import ir.map.sdk_map.annotations.Polyline;
import ir.map.sdk_map.annotations.PolylineOptions;
import ir.map.sdk_map.camera.CameraUpdate;
import ir.map.sdk_map.camera.CameraUpdateFactory;
import ir.map.sdk_map.geometry.LatLng;
import ir.map.sdk_map.geometry.LatLngBounds;
import ir.map.sdk_map.location.LocationComponent;
import ir.map.sdk_map.maps.MapirMap;
import ir.map.sdk_map.maps.SupportMapFragment;
import ir.map.sdk_services.RouteMode;
import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.MaptexError;
import ir.map.sdk_services.models.MaptexManeuver;
import ir.map.sdk_services.models.MaptexRouteResponse;
import ir.map.sdk_services.models.MaptexStepsItem;
import ir.map.sdk_services.models.base.ResponseListener;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.route.MapirPolyUtil;
import ir.radical_app.radical.route.MapirSphericalUtil;

public class MapActivity extends AppCompatActivity implements LocationEngineListener, ResponseListener<MaptexRouteResponse> {

    private MapirMap mapirMap;
    private Location myLocation;
    private LocationManager manager;
    private String lat, lon, name,categoryId;
    private ImageView directions;
    private MaterialCardView routingCard;
    private TextView routingDetails, routingName;
    private int errorCounter = 0;
    private final int Location_REQUEST_CODE = 658;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        init();

    }

    private void init() {
        directions = findViewById(R.id.map_direction);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lat = bundle.getString("lat", "");
            lon = getIntent().getExtras().getString("lon", "");
            name = getIntent().getExtras().getString("name", "");
            categoryId = getIntent().getExtras().getString("categoryId", "1");
            ((TextView) findViewById(R.id.map_shop_name)).setText(name);

        } else {
            MyToast.Companion.create(this, getString(R.string.general_error));
            onBackPressed();
        }

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        routingCard = findViewById(R.id.map_routing_card);
        routingDetails = findViewById(R.id.map_routing_details);
        routingName = findViewById(R.id.map_routing_name);


        mapInit();
        onClicks();


    }

    private void mapInit() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView))
                .getMapAsync(Map -> {

                    MapActivity.this.mapirMap = Map;
                    if (MapActivity.this.mapirMap != null) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                    MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_REQUEST_CODE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(
                                     MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Location_REQUEST_CODE);

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationComponent component = MapActivity.this.mapirMap.getLocationComponent();
                            component.activateLocationComponent(MapActivity.this);
                            component.setLocationComponentEnabled(true);
                            if (component.getLocationEngine() != null) {
                                component.getLocationEngine().addLocationEngineListener(MapActivity.this);
                            }
                            LatLng storePosition = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                            mapirMap.addMarker(new MarkerOptions()
                                    .setTitle(name)
                                    .position(storePosition)
                                    .icon(IconFactory.getInstance(MapActivity.this)
                                            .fromResource(Const.Companion.getCategories().get(Integer.parseInt(categoryId)-1))));

                            mapirMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storePosition, 15), 1000);

                        }
                    }

                });
    }

    private void onClicks() {
        findViewById(R.id.map_myloc).setOnClickListener(View -> {

            if (myLocation != null)
                mapirMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
            else if (!checkIfLocationEnabled()) {
                turnGPS();
                MyToast.Companion.create(MapActivity.this, getString(R.string.txt_gps));
            } else
                MyToast.Companion.createShort(MapActivity.this, getString(R.string.txt_gps_loading));

        });


        directions.setOnClickListener(View -> {

            if (myLocation != null) {
                route();
            } else if (!checkIfLocationEnabled()) {
                turnGPS();
                MyToast.Companion.create(MapActivity.this, getString(R.string.txt_gps));
            } else
                MyToast.Companion.createShort(MapActivity.this, getString(R.string.txt_gps_loading));

        });
    }

    private void route() {
        MapirLatLng latLng1 = new MapirLatLng(Double.valueOf(lat), Double.valueOf(lon));
        MapirLatLng latLng2 = new MapirLatLng(myLocation.getLatitude(), myLocation.getLongitude());

        new ServiceHelper()
                .getRouteInfo(latLng1, latLng2, RouteMode.BASIC, MapActivity.this);
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

    private void turnGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void onSuccess(MaptexRouteResponse response) {
        updateRoutingInfo(response);
        directions.setVisibility(View.GONE);
    }

    @Override
    public void onError(MaptexError error) {
        if (errorCounter++ != 3)
            route();
        else
            MyToast.Companion.create(this, getString(R.string.general_error));

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
//            if (latLngsRouteListAlternative.size() > 0) {
//                mapirMap.addPolyline(new PolylineOptions().width(5).color(getResources().getColor(R.color.primary))
//                        .addAll(latLngsRouteListAlternative));
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                for (LatLng latLng : latLngsRouteListAlternative) {
//                    builder.include(latLng);
//                }
//                LatLngBounds bounds = builder.build();
//                int padding = 50; // offset from edges of the map in pixels
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mapirMap.animateCamera(cu);
//
//            }

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
            routingCard.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("####.#");
            decimalFormat.setRoundingMode(RoundingMode.UP);
            String distance = decimalFormat.format(routingInfo.routes.get(0).distance / 1000);
            String duration = ((int) routingInfo.routes.get(0).duration / 60) + "";
            routingDetails.setText(getString(R.string.bottomsheet_model, duration, distance));
            routingName.setText(name);
            //  routingCard.startAnimation(AnimationUtils.loadAnimation(MapActivity.this,R.anim.bottom_up));


        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==Location_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapInit();
            }
        }
    }
}
