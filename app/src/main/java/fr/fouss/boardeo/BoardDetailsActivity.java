package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.fouss.boardeo.data.Board;

public class BoardDetailsActivity extends AppCompatActivity {

    String boardKey;

    /**
     * The current board data to be returned
     */
//    Intent boardIntent;

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boardKey = getIntent().getStringExtra(Board.KEY_FIELD);

//        boardIntent = new Intent();

        // Copy intent
//        boardIntent.putExtras(getIntent());
        updateTextFields();

        // Setup of the edit button and its listener
        Button editButton = findViewById(R.id.editBoardButton);
        editButton.setOnClickListener(this::onEditBoardButtonClick);
    }

    /**
     * When edit button is clicked
     * @param v
     */
    public void onEditBoardButtonClick(View v) {
        // Start new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
        intent.putExtra(Board.KEY_FIELD, boardKey);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == MiscUtil.BOARD_CREATION_REQUEST
//                && resultCode == MiscUtil.NEW_BOARD_RESULT) {
//            // New board activity normal return
//
//            // Copy result intent
//            boardIntent.putExtras(data);
//            updateTextFields();
//        }
    }

    private void updateTextFields() {
//        String boardKey = boardIntent.getStringExtra("BOARD_KEY");
        DatabaseReference dataReference = mDatabase.child("boards").child(boardKey);

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Board board = dataSnapshot.getValue(Board.class);
                assert board != null;

                // Setup of texts
                TextView name = findViewById(R.id.boardName);
                name.setText(board.getName());
                TextView shortDescription = findViewById(R.id.boardShortDescription);
                shortDescription.setText(board.getShortDescription());
                TextView fullDescription = findViewById(R.id.boardFullDescription);
                fullDescription.setText(board.getFullDescription());
                TextView ownerUid = findViewById(R.id.boardOwnerUid);
                ownerUid.setText(board.getOwnerUid());
                TextView coordinates = findViewById(R.id.boardCoordinates);
                String coordinatesText = board.getLatitude() + " ; " + board.getLongitude();
                coordinates.setText(coordinatesText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BoardDetailsActivity.this,
                        "Board info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
//        Intent intent = new Intent();
//        // copy the current board data to update the requesting activity
//        intent.putExtras(boardIntent);
//        setResult(MiscUtil.BOARD_DETAIL_RESULT, intent);
        finish();
        return true;
    }

}
