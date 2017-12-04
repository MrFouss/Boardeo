package fr.fouss.boardeo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
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

    private Long color;
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

        // Setup color button
        findViewById(R.id.colorButton).setOnClickListener(v -> ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose a color")
                .initialColor(color.intValue())
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(10)
                .showAlphaSlider(false)
                .showLightnessSlider(false)
                .showColorEdit(false)
                .showColorPreview(false)
                .setOnColorSelectedListener(selectedColor -> {})
                .setPositiveButton("Ok", (dialog, selectedColor, allColors) -> {
                    color = (long) selectedColor;
                    findViewById(R.id.colorButton).setBackgroundColor(selectedColor);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .build()
                .show());

        // Get the board's key if it exists, null otherwise
        boardKey = getIntent().getStringExtra(Board.KEY_FIELD);
        updateFields();
    }

    private void updateFields() {

        Toolbar toolbar = findViewById(R.id.NewBoardToolbar);

        if (boardKey != null) {

            DatabaseReference dataReference = mDatabase.child("boards").child(boardKey);
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    board = dataSnapshot.getValue(Board.class);
                    assert board != null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        toolbar.setTitle(board.getName());
                        toolbar.setSubtitle(R.string.edit_board);
                    }

                    // Setup fields based on request intent
                    EditText title = findViewById(R.id.boardNameField);
                    title.setText(board.getName());
                    color = board.getColor();
                    findViewById(R.id.appbar).setBackgroundColor(color.intValue());
                    toolbar.setBackgroundColor(color.intValue());
                    Button colorButton = findViewById(R.id.colorButton);
                    colorButton.setBackgroundColor(color.intValue());
                    EditText shortDescription = findViewById(R.id.shortDescriptionField);
                    shortDescription.setText(board.getShortDescription());
                    EditText fullDescription = findViewById(R.id.fullDescriptionField);
                    fullDescription.setText(board.getFullDescription());
                    CheckBox isPublic = findViewById(R.id.isPublicCheckbox);
                    isPublic.setChecked(board.getIsPublic());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(NewBoardActivity.this,
                            "Board info couldn't be retrieved",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                toolbar.setSubtitle(R.string.create_board);

            color = (long) 0xff555555;
            Button colorButton = findViewById(R.id.colorButton);
            colorButton.setBackgroundColor(color.intValue());
            findViewById(R.id.appbar).setBackgroundColor(color.intValue());
            toolbar.setBackgroundColor(color.intValue());
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
            updates.put("color", color);
            updates.put("shortDescription", shortDescription);
            updates.put("fullDescription", fullDescription);
            updates.put("isPublic", isPublic);
            mDatabase.child("boards")
                    .child(boardKey)
                    .updateChildren(updates);

        // If this is a board creation
        } else {
            if (latitude != null && longitude != null) {
                board = new Board(name, color, userUtils.getUserUid(), latitude, longitude, isPublic, new Date().getTime());
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
