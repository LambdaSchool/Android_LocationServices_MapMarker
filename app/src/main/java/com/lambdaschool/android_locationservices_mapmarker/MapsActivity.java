package com.lambdaschool.android_locationservices_mapmarker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final static int[] GOOGLE_MAP_TYPE_INT_ARRAY = new int[]{GoogleMap.MAP_TYPE_NONE, GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN, GoogleMap.MAP_TYPE_HYBRID};
    private static final int LOCATION_REQUEST_CODE = 33;
    private GoogleMap googleMap;
    Context context;
    ArrayList<MarkerOptions> markerOptionsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;
        markerOptionsArrayList = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.image_button_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                } else {
                    getLocation();
                }
            }
        });

        findViewById(R.id.image_button_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextLatLng = findViewById(R.id.edit_text_lat_lng);

                String latCommaLng = editTextLatLng.getText().toString();

                LatLng target = googleMap.getCameraPosition().target;

                if (!latCommaLng.equals("")) {
                    String[] split = latCommaLng.split(",");
                    target = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                }

                markerOptionsArrayList.add(new MarkerOptions().position(target).title("Marker at " + target.toString()));
                googleMap.addMarker(markerOptionsArrayList.get(markerOptionsArrayList.size() - 1));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Toast.makeText(context, marker.getTitle(), Toast.LENGTH_SHORT).show();
                        googleMap.setMapType(GOOGLE_MAP_TYPE_INT_ARRAY[(int) (Math.random() * 4)]);
                        marker.remove();
                        return true;
                    }
                });
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(target));

                editTextLatLng.setText("");
                editTextLatLng.clearFocus();
            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())), 2000, null);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Seattle and move the camera
        LatLng seattle = new LatLng(47.6, -122.3);
        this.googleMap.addMarker(new MarkerOptions().position(seattle).title("Marker in Seattle"));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(seattle, 5f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                googleMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
