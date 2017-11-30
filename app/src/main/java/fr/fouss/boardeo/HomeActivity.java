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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.sign_in.SignInActivity;
import fr.fouss.boardeo.utils.UserUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES";
    private static final int REQUEST_CODE_LOCATION_ALLOWANCE = 6942;
    private static final int REQUEST_CODE_LOCATION_ACTIVATE = 7357;

    private static final double RADIUS = 100.0;

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;
    private ChildEventListener mNearbyBoardsListener;
    private Map<String, Marker> markerList;
    private Map<String, Board> boardList;
    private Circle mCircle;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUtils = new UserUtils(this);
        markerList = new HashMap<>();
        boardList = new HashMap<>();

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

            // Updates the user's name in database
            mDatabase.child("users").child(userUtils.getUserUid()).child("username").setValue(userUtils.getUserName());

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

    private void displayNearbyBoards(Double latitude, Double longitude) {
        DatabaseReference dataReference = mDatabase.child("boards");

        if (mNearbyBoardsListener != null)
            dataReference.removeEventListener(mNearbyBoardsListener);

        mNearbyBoardsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMarker(dataSnapshot.getKey(), dataSnapshot.getValue(Board.class), latitude, longitude);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateMarker(dataSnapshot.getKey(), dataSnapshot.getValue(Board.class), latitude, longitude);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMarker(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,
                        "Board info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        };

        dataReference.addChildEventListener(mNearbyBoardsListener);

        if (mCircle != null)
            mCircle.remove();

        mCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .clickable(false)
                .radius(RADIUS)
                .fillColor(getResources().getColor(R.color.mapCircleFillColor))
                .strokeColor(getResources().getColor(R.color.mapCircleStrokeColor))
                .strokeWidth(5.0f));
    }

    private Boolean isInRadius(Double centerLatitude,
                               Double centerLongitude,
                               Double radius,
                               Double objectLatitude,
                               Double objectLongitude) {
        Double objectDistance = Math.sqrt(
                Math.pow((centerLatitude - objectLatitude), 2)
                        + Math.pow((centerLongitude - objectLongitude), 2));
        return (objectDistance * 111000.0) <= radius;
    }

    private void addMarker(String key, Board board, Double currentLatitude, Double currentLongitude) {
        if (!markerList.containsKey(key)
                && isInRadius(currentLatitude, currentLongitude, RADIUS, board.getLatitude(), board.getLongitude())) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(board.getLatitude(), board.getLongitude()))
                    .title(board.getName())
                    .snippet(board.getShortDescription()));
            marker.setTag(key);
            markerList.put(key, marker);
            boardList.put(key, board);
        }
    }

    private void updateMarker(String key, Board board, Double currentLatitude, Double currentLongitude) {
        Marker marker = markerList.get(key);

        // If the marker already exists
        if (marker != null) {
            // If it is still in range (it should be updated)
            if (isInRadius(currentLatitude, currentLongitude, RADIUS, board.getLatitude(), board.getLongitude())) {
                marker.setPosition(new LatLng(board.getLatitude(), board.getLongitude()));
                marker.setTitle(board.getName());
                marker.setSnippet(board.getShortDescription());

            // If it is not anymore in range (it should be deleted)
            } else {
                removeMarker(key);
            }

        // If it doesn't exist (it should maybe be created)
        } else {
            addMarker(key, board, currentLatitude, currentLongitude);
        }
    }

    private void removeMarker(String key) {
        Marker marker = markerList.get(key);
        marker.setTag(null);
        marker.remove();
        markerList.remove(key);
        boardList.remove(key);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String key = (String) marker.getTag();
        Board board = boardList.get(key);

        // Launch board detail activity
        Intent intent = new Intent(this, BoardDetailsActivity.class);
        board.fillIntentExtras(intent);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(17.5f);
        mMap.setMaxZoomPreference(17.5f);
        mMap.setInfoWindowAdapter(new BoardeoInfoWindowAdapter());
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnInfoWindowClickListener(this);
    }

    private void updateCameraPosition(Location location) {

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(17.5f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                displayNearbyBoards(location.getLatitude(), location.getLongitude());
            }
        }
    }

    /** Demonstrates customizing the info window and/or its contents. */
    class BoardeoInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mContents;

        BoardeoInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.maps_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // This means that getInfoContents will be called.
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
//            int bannerId = 0;
//            ((ImageView) view.findViewById(R.id.banner)).setImageResource(bannerId);

            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.title);
            if (title != null) {
                titleUi.setText(title);

                String key = (String) marker.getTag();
                Boolean isEditable = boardList.get(key).getIsPublic();
                if (isEditable)
                    titleUi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_public, 0, 0, 0);
                else
                    titleUi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_locked, 0, 0, 0);
//                SpannableString titleText = new SpannableString(title);
//                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = view.findViewById(R.id.snippet);
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }
        }
    }
}
