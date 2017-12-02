package fr.fouss.boardeo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.TreeMap;

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
    private float minAccuracy = 15.0f;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        // data base
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUtils = new UserUtils(this);

        // geolocation
        retrieveLocation();

        // Setup the toolbar
        setSupportActionBar(findViewById(R.id.NewBoardToolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup validation button
        findViewById(R.id.validateBoardEditionButton).setOnClickListener(this::onValidateButtonClick);

        // Get the board's key if it exists, null otherwise
        boardKey = getIntent().getStringExtra(Board.KEY_FIELD);
        updateFields();
    }

    private void updateFields() {
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
                    isPublic.setChecked(board.getIsPublic());

                    Button deleteButton = findViewById(R.id.board_delete_button);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(
                            v -> onDeleteButtonClick(v)
                    );
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

    private void retrieveLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
        } else {
            Toast.makeText(this, "The location permission is needed", Toast.LENGTH_SHORT).show();
        }
    }

    ///// EVENTS /////

    public void onDeleteButtonClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you really want to delete this board ?")
                .setTitle("Deletion")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> deleteBoard())
                .setNegativeButton("No", (dialog, which) -> {});
        alert.create().show();
    }

    public void deleteBoard() {
        DatabaseReference boardRef = mDatabase
                .child("boards")
                .child(boardKey);

        // unlink user subscriptions
        boardRef.child("subscriptions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot subscriber : dataSnapshot.getChildren()) {
                    String subscriberKey = subscriber.getKey();
                    mDatabase
                            .child("users")
                            .child(subscriberKey)
                            .child("subscriptions")
                            .child(boardKey)
                            .removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewBoardActivity.this,
                        "Board subscriptions info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // unlink and delete posts
        boardRef.child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            String postKey = post.getKey();
                            mDatabase
                                    .child("posts")
                                    .child(postKey)
                                    .removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(NewBoardActivity.this,
                                "Board posts info couldn't be retrieved",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // delete board
        boardRef.removeValue();
    }

    /**
     * When validation floating button is clicked
     * @param v view
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
            TreeMap<String, Object> updates = new TreeMap<>();
            updates.put("name", name);
            updates.put("shortDescription", shortDescription);
            updates.put("fullDescription", fullDescription);
            updates.put("isPublic", isPublic);
            mDatabase.child("boards")
                    .child(boardKey)
                    .updateChildren(updates);

        // If this is a board creation
        } else {
            if (latitude != null && longitude != null) {
                board = new Board(name, userUtils.getUserUid(), latitude, longitude, isPublic);
                board.setShortDescription(shortDescription);
                board.setFullDescription(fullDescription);
                String newBoardKey = mDatabase.child("boards").push().getKey();
                mDatabase.child("boards")
                        .child(newBoardKey)
                        .setValue(board);
                mDatabase.child("boards")
                        .child(newBoardKey)
                        .child("subscriptions")
                        .child(userUtils.getUserUid())
                        .setValue("true");
                mDatabase.child("users")
                        .child(userUtils.getUserUid())
                        .child("subscriptions")
                        .child(newBoardKey)
                        .setValue("true");
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
        finish();
        return true;
    }
}
