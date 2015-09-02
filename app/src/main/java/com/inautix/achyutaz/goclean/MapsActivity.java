package com.inautix.achyutaz.goclean;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng currentPosition;
    DBHelper db;
    Button capture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        capture=(Button)findViewById(R.id.button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        setUpMapIfNeeded();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        db = new DBHelper(this);
        String time= MapsActivity.getTimeStamp();
        String loc = currentPosition.toString();
       // Bundle extras = getIntent().getExtras();

            if(db.insertContact(loc,time))
                Toast.makeText(getApplicationContext(), "UPLOAD SUCCESSFUL!", Toast.LENGTH_SHORT).show();
            else
            Toast.makeText(getApplicationContext(), "TRY AGAIN!", Toast.LENGTH_SHORT).show();



    }

    public static String getTimeStamp() {
        SimpleDateFormat serverFormat;
        Calendar c = Calendar.getInstance();
        serverFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return serverFormat.format(c).toString();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
   //     myLocationClient = new LocationClient(getApplicationContext(), this, this);
// once we have the reference to the client, connect it

      //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        if( mGoogleApiClient!=null)
        {
            mGoogleApiClient.connect();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            String provider = locationManager.getBestProvider(criteria, true);

            android.location.LocationListener locationListener = new android.location.LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    showCurrentLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

            };

            locationManager.requestLocationUpdates(provider,2000,0,locationListener);

            // Getting initial Location
            Location location = locationManager.getLastKnownLocation(provider);
            // Show the initial location
            if(location != null)
            {
                showCurrentLocation(location);
            }
        }
    }
    private void showCurrentLocation(Location location){

        mMap.clear();

         currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.wastebin))
                .flat(true)
                .title("I'm here!"));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
    }

    @Override
    protected void onStart() {
        super.onStart();

       if( mGoogleApiClient!=null)
       {
           mGoogleApiClient.connect();

       }

    }


    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.wastebin))
                .flat(true)
                .title("I'm here!"));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
    }
}
