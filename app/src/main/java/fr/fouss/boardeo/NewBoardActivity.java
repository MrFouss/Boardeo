package fr.fouss.boardeo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.utils.UserUtils;

public class NewBoardActivity extends AppCompatActivity {

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    private UserUtils userUtils;

    private String boardKey;
    private Board board;

    private Double latitude;
    private Double longitude;
    private float minAccuracy = 5.0f;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.INFO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUtils = new UserUtils(this);

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0f, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(location.hasAccuracy() && location.getAccuracy() <= minAccuracy) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        minAccuracy = location.getAccuracy();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        }

        // Setup the toolbar
        setSupportActionBar(findViewById(R.id.NewBoardToolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup validation button
        findViewById(R.id.validateBoardEditionButton).setOnClickListener(this::onValidateButtonClick);

        // Get the board's key if it exists, null otherwise
        boardKey = getIntent().getStringExtra(Board.KEY_FIELD);

        if (boardKey != null) {
            DatabaseReference dataReference = mDatabase.child("boards").child(boardKey);
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    board = dataSnapshot.getValue(Board.class);

                    // Setup fields based on request intent
                    EditText title = findViewById(R.id.boardNameField);
                    title.setText(board.getName());
                    EditText shortDescription = findViewById(R.id.shortDescriptionField);
                    shortDescription.setText(board.getShortDescription());
                    EditText fullDescription = findViewById(R.id.fullDescriptionField);
                    fullDescription.setText(board.getFullDescription());
                    CheckBox isPublic = findViewById(R.id.isPublicCheckbox);
                    isPublic.setChecked(board.getPublic());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(NewBoardActivity.this,
                            "Board info couldn't be retrieved",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    ///// EVENTS /////

    /**
     * When validation floating button is clicked
     * @param v
     */
    public void onValidateButtonClick(View v) {
        // retrieve fields
        EditText name = findViewById(R.id.boardNameField);
        EditText shortDescription = findViewById(R.id.shortDescriptionField);
        EditText fullDescription = findViewById(R.id.fullDescriptionField);

        boolean filled = true;
        if (name.length() == 0) {
            name.setError("Missing");
            filled = false;
        }
        if (shortDescription.length() == 0) {
            shortDescription.setError("Missing");
            filled = false;
        }
        if (fullDescription.length() == 0) {
            fullDescription.setError("Missing");
            filled = false;
        }
        if (filled) {
            validateResult();
        }
    }

    /**
     * Validates data and updates the database
     */
    public void validateResult() {
        // retrieve fields
        String name = ((EditText) findViewById(R.id.boardNameField)).getText().toString();
        String shortDescription = ((EditText) findViewById(R.id.shortDescriptionField)).getText().toString();
        String fullDescription = ((EditText) findViewById(R.id.fullDescriptionField)).getText().toString();
        Boolean isPublic = ((CheckBox) findViewById(R.id.isPublicCheckbox)).isChecked();

        // If this is an update
        if (boardKey != null) {
            board.setName(name);
            board.setShortDescription(shortDescription);
            board.setFullDescription(fullDescription);
            board.setIsPublicField(isPublic);

            mDatabase.child("boards").child(boardKey).setValue(board);

        // If this is a board creation
        } else {
            if (latitude != null && longitude != null) {
                board = new Board(name, userUtils.getUserUid(), latitude, longitude, isPublic);
                board.setShortDescription(shortDescription);
                board.setFullDescription(fullDescription);
                mDatabase.child("boards").push().setValue(board);
            } else {
                Toast.makeText(this, "A more precise location is required to create a new board.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // return without any result
//        setResult(MiscUtil.CANCEL_NEW_BOARD_RESULT);
        finish();
        return true;
    }
}
