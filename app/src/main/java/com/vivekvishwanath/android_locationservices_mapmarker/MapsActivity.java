package com.vivekvishwanath.android_locationservices_mapmarker;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private GoogleMap mMap;
    private static final int FINE_LOCATION_REQUEST_CODE = 1;
    private static final int MAP_CAMERA_ZOOM = 10;
    Context context;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button buttonCenterMap;
    Button buttonAddMarker;
    Button showButton;
    LatLng myLocation;
    EditText latText;
    EditText longText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        buttonAddMarker = findViewById(R.id.button_add_marker);
        buttonCenterMap = findViewById(R.id.button_center_map);
        showButton = findViewById(R.id.button_custom_center);
        latText = findViewById(R.id.lat_text);
        longText = findViewById(R.id.long_text);

        buttonCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // need to request permission
                    ActivityCompat.requestPermissions((Activity) context
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
                } else {
                    //permission already granted
                    getLocation();
                }
            }
        });

        buttonAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    mMap.addMarker(new MarkerOptions().position(myLocation));
                }
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(latText.getText().toString())
                        || TextUtils.isEmpty(longText.getText().toString())) {
                    Toast.makeText(context, "Please enter a valid latitude and longitude"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    double latitude = Double.parseDouble(latText.getText().toString());
                    double longitude = Double.parseDouble(longText.getText().toString());
                    if ( (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180)) {
                        LatLng customLocation = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customLocation, MAP_CAMERA_ZOOM));
                        mMap.addMarker(new MarkerOptions().position(customLocation));
                    } else {
                        Toast.makeText(context, "Please enter a valid latitude and longitude"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, MAP_CAMERA_ZOOM));
                }
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
    }
}
