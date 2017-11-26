package fr.fouss.boardeo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import fr.fouss.boardeo.sign_in.SignInActivity;
import fr.fouss.boardeo.utils.UserUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                OnMapReadyCallback, View.OnClickListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES";
    private static final int REQUEST_CODE_LOCATION_ALLOWANCE = 6942;
    private static final int REQUEST_CODE_LOCATION_ACTIVATE = 7357;

    private UserUtils userUtils;

    private TextView usernameLabel;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Boolean mRequestingLocationUpdates;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        updateValuesFromBundle(savedInstanceState);

        userUtils = new UserUtils(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        usernameLabel = navHeaderView.findViewById(R.id.username);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Setup the location activation and allowance buttons
        findViewById(R.id.allow_location_button).setOnClickListener(this);
        findViewById(R.id.activate_location_button).setOnClickListener(this);

        // Setup all location based settings
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Get the location provider
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create the location callback
        mLocationCallback = new BoardeoLocationCallback();

        mRequestingLocationUpdates = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userUtils.isSignedIn()) {
            usernameLabel.setText(userUtils.getUserName());

            // Check for location permission
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                findViewById(R.id.allow_location_button).setVisibility(View.VISIBLE);
                return;
            }

            if (mLocationManager == null)
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager == null || !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                findViewById(R.id.activate_location_button).setVisibility(View.VISIBLE);
                return;
            }

            startLocationUpdates();

        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState != null &&
                savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_notifications) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_boards:

                break;
            case R.id.nav_boards:
                startActivity(new Intent(this, BoardListActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_sign_out:
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(getString(R.string.dialog_content_sign_out))
                        .setTitle(getString(R.string.dialog_title_sign_out));

                // Add the buttons
                builder.setPositiveButton("Yes", (dialog, id1) -> {
                    userUtils.signOut();
                    startActivity(new Intent(this, SignInActivity.class));
                });
                builder.setNegativeButton("No", (dialog, id1) -> {
                });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void updateCameraPosition(Location location) {

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(15.0f)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void startLocationUpdates() {
        if (mRequestingLocationUpdates
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
            mRequestingLocationUpdates = false;
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allow_location_button:
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_ALLOWANCE);
                break;

            case R.id.activate_location_button:
                startActivityForResult(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                REQUEST_CODE_LOCATION_ACTIVATE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_LOCATION_ALLOWANCE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findViewById(R.id.allow_location_button).setVisibility(View.GONE);
                startLocationUpdates();
            } else {
                Toast.makeText(this,
                        "The goal of this app is to use geolocation, so trust us! ;)",
                        Toast.LENGTH_LONG).show();
                findViewById(R.id.allow_location_button).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_ACTIVATE:
                if (mLocationManager == null)
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    findViewById(R.id.activate_location_button).setVisibility(View.GONE);
                break;
        }
    }

    private class BoardeoLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationResult.getLastLocation();
                updateCameraPosition(location);
            }
        }
    }
}
