package com.example.patrickjmartin.mapmarker;

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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static android.view.View.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int FINE_LOCATION_REQUEST_CODE = 1;
    Context context;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private Double mLongtitude, mLatitude;
    private NumberFormat stringFormat;
    private EditText latitudeEditText, longtitudeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        context = this;
        stringFormat = new DecimalFormat("###.#####");
        latitudeEditText = findViewById(R.id.latitude_edit_text);
        longtitudeEditText = findViewById(R.id.longtitude_edit_text);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.user_location_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            FINE_LOCATION_REQUEST_CODE);
                } else {
                    getLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                mLatitude = location.getLatitude();
                                mLongtitude = location.getLongitude();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        latitudeEditText.setText(stringFormat.format(mLatitude));
                                        longtitudeEditText.setText(stringFormat.format(mLongtitude));
                                    }
                                });

                                mMap.animateCamera(CameraUpdateFactory.newLatLng(
                                        new LatLng(mLatitude,
                                                mLongtitude)));
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.go_to_location_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mLatitude = Double.valueOf(Double.parseDouble(latitudeEditText.getText().toString()));
                } catch (Exception e) {
                    mLatitude = null;
                }

                try {
                    mLongtitude = Double.valueOf(Double.parseDouble(longtitudeEditText.getText().toString()));
                } catch (Exception e) {
                    mLatitude = null;
                }

                if (mLatitude != null && mLatitude != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongtitude)));
                }



            }
        });

        findViewById(R.id.add_location_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng latLong = mMap.getCameraPosition().target;

                mLatitude = latLong.latitude;
                mLongtitude = latLong.longitude;

                mMap.addMarker(new MarkerOptions().position(latLong));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        latitudeEditText.setText(stringFormat.format(mLatitude));
                        longtitudeEditText.setText(stringFormat.format(mLongtitude));
                    }
                });
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Permission was Granted!", Toast.LENGTH_SHORT).show();

                getLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        }
                    }
                });
            }
        }
    }

    private void getLocation(OnSuccessListener<Location> onSuccessListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, onSuccessListener);
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
        LatLng sydney = new LatLng(-34.234234, 151.345345);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mLatitude = marker.getPosition().latitude;
                mLongtitude = marker.getPosition().longitude;

                latitudeEditText.setText(stringFormat.format(mLatitude));
                longtitudeEditText.setText(stringFormat.format(mLongtitude));

                return true;
            }
        });
    }




}
