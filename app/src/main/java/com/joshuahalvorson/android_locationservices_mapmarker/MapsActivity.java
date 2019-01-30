package com.joshuahalvorson.android_locationservices_mapmarker;

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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    public static final int ACCESS_LOCATION_REQUEST_CODE = 1;

    private GoogleMap mMap;

    private double currentLat, currentLon;

    Context context;
    FusedLocationProviderClient fusedLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.center_on_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_LOCATION_REQUEST_CODE);

                } else {
                    Log.i(TAG, "Location permission already granted");
                    getLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            currentLat = location.getLatitude();
                            currentLon = location.getLongitude();

                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLat,
                                    currentLon)));
                        }
                    });
                }
            }
        });

        findViewById(R.id.add_pin_at_location_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LatLng latLng = mMap.getCameraPosition().target;
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        final EditText userLat = findViewById(R.id.custom_lat);
        final EditText userLon = findViewById(R.id.custom_lon);

        findViewById(R.id.new_loc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(Double.parseDouble(userLat.getText().toString()),
                        Double.parseDouble(userLon.getText().toString()));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                mMap.addMarker(new MarkerOptions().position(latLng));

                InputMethodManager inputManager =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "permission was granted");

                getLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLat, currentLon)));
                    }
                });

            }
        }
    }

    private void getLocation(OnSuccessListener<Location> onSuccessListener) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationProvider
                .getLastLocation()
                .addOnSuccessListener(onSuccessListener);
    }
}
