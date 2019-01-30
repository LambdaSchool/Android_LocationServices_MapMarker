package com.example.android_locationservices_mapmarker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.w3c.dom.Text;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int LOCATION_REQUEST_CODE = 1;
    public static final String LAT_LONG_TAG = "Latitude/Longitude";
    public static GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Need to grant permission to use location.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Toast.makeText(context, "Location permission was granted", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.button_center_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Need to grant permission to use location.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())), 2000, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, new GoogleMap.CancelableCallback() {

                                        @Override
                                        public void onFinish() {

                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    });
                }
            }
        });

        findViewById(R.id.button_add_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Need to grant permission to use location.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            LatLng latLng = mMap.getCameraPosition().target;
                            mMap.addMarker(new MarkerOptions().position(latLng));
                        }
                    });

                }
            }
        });

        findViewById(R.id.button_add_pin_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Need to grant permission to use location.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    LatLonDialogFragment fragment = new LatLonDialogFragment();
                    fragment.show(getSupportFragmentManager(), LAT_LONG_TAG);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public static class LatLonDialogFragment extends DialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LinearLayout view = new LinearLayout(getActivity());
            view.setOrientation(LinearLayout.VERTICAL);
            TextView latTextView = new TextView(getActivity());
            latTextView.setText("Latitude");
            latTextView.setTextSize(32);
            latTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
            view.addView(latTextView);
            final EditText editLat = new EditText(getActivity());
            view.addView(editLat);
            TextView lonTextView = new TextView(getActivity());
            lonTextView.setText("Longitude");
            lonTextView.setTextSize(32);
            lonTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
            view.addView(lonTextView);
            final EditText editLon = new EditText(getActivity());
            view.addView(editLon);
            final Button dialogButton = new Button(getActivity());
            dialogButton.setText("Apply");
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Double lat = Double.valueOf(editLat.getText().toString());
                    Double lon = Double.valueOf(editLon.getText().toString());
                    LatLng latLng = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    dismiss();
                }
            });
            view.addView(dialogButton);
            return view;
        }


    }


}
