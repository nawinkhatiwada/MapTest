package com.braindigit.brain.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brain.test.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    LocationManager mLocationManager;
    TextView tvCurrentLocation;
    SupportMapFragment mapFragment;
    Toolbar mToolBar;
    Button setAddress;
    String address;
    String city;
    SearchView searchLocation;
    Marker mMarker;
    double lat = 0;
    double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentLocation = (TextView) findViewById(R.id.currentLocation);
        searchLocation = (SearchView) findViewById(R.id.searchLocation);


        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle("SET YOUR LOCATION");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, getResources().getString(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        setAddress = (Button) findViewById(R.id.setAddress);
        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (address != null && city != null) {
                    if (address != null && city != null) {
                        String finalLocation = address + ", " + city;
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", finalLocation);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        });

        searchLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchedLocation();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //method used for placing my location button at bottom right corner
        placeMyLocationButton();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                getCurrentLocation(location);
                mMap.setOnMyLocationChangeListener(null);
            }

        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latlng) {
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(latlng.latitude,
                                        latlng.longitude))
                        .draggable(true).visible(true));

                Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();


//                        System.out.println(addresses.get(0).getLocality());
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(" " + address + "\n" + city));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (address != null && city != null) {
                    mMarker.setTitle("" + address + ", " + city);
                }

            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMarker != null && mMarker.isVisible()) {
                    mMarker.remove();
                }
            }
        });
    }


    public void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.gps_disabled))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btnYes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.btnNo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void placeMyLocationButton() {
        // change position of my location button
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        LatLng latLong = new LatLng(lat, lng);
        getLocationFromAddress();
        if (address != null && city != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16));
            tvCurrentLocation.setText("" + address + ", " + city);
        }

    }

    private void getSearchedLocation() {

        List<Address> addressList = null;
        CharSequence SEARCHLOC = searchLocation.getQuery();
        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addressList = geoCoder.getFromLocationName(String.valueOf(SEARCHLOC), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        if (address != null) {
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("" + address.getLatitude() + "," + address.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        }
    }

    void getLocationFromAddress() {

        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();


//                        System.out.println(addresses.get(0).getLocality());
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(" " + address + "\n" + city));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
