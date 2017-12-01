package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.data.Post;
import fr.fouss.boardeo.listing.PostAdapter;
import fr.fouss.boardeo.utils.UserUtils;

public class BoardDetailsActivity extends AppCompatActivity {

    /**
     * Board key to retrieve its data
     */
    String boardKey;

    private UserUtils userUtils;

    private Board board;

    private boolean myBoard = true;
    private boolean subscriptionListenerLauched = false;

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_details);

        // data base reference
        userUtils = new UserUtils(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get board data
        boardKey = getIntent().getStringExtra(Board.KEY_FIELD);
        updateTextFields();

        RecyclerView postsView = findViewById(R.id.postRecyclerView);
        postsView.setLayoutManager(new LinearLayoutManager(this));
        PostAdapter postAdapter = new PostAdapter(this);
        postsView.setAdapter(postAdapter);
        postAdapter.initPostListListener(boardKey);
        postAdapter.setPostClickListener(this::onPostClicked);
    }

    public void onAddPostButtonClick(View v) {
        // Start new board activity
        Intent intent = new Intent(this, NewPostActivity.class);
        intent.putExtra(Board.KEY_FIELD, boardKey);
        startActivity(intent);
    }

    /**
     * When edit button is clicked
     * @param v view
     */
    public void onEditBoardButtonClick(View v) {
        // Start new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
        intent.putExtra(Board.KEY_FIELD, boardKey);
        startActivity(intent);
    }

    private void updateTextFields() {
        DatabaseReference dataReference = mDatabase.child("boards").child(boardKey);

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                board = dataSnapshot.getValue(Board.class);
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

                // hide/show add post button depending on authorizations
                FloatingActionButton addPostButton = findViewById(R.id.addPostButton);
                if (board.getOwnerUid().equals(userUtils.getUserUid()) || board.getIsPublic()) {
                    addPostButton.setOnClickListener(v -> onAddPostButtonClick(v));
                } else {
                    addPostButton.setVisibility(View.GONE);
                }

                // Setup of the edit button and its listener
                Button editButton = findViewById(R.id.editBoardButton);
                if (board.getOwnerUid().equals(userUtils.getUserUid())) {
                    editButton.setOnClickListener(v -> onEditBoardButtonClick(v));
                } else {
                    editButton.setVisibility(View.GONE);
                }

                // enable subscription checkbox if your own board
                CheckBox subCheckbox = findViewById(R.id.board_detail_subscription_checkbox);
                myBoard = board.getOwnerUid().equals(userUtils.getUserUid());
                if (!myBoard) {
                    subCheckbox.setEnabled(true);
                }

                // launch subscription listener only once, after retrieving board data
                if (!subscriptionListenerLauched) {
                    subscriptionListenerLauched = true;
                    updateSubscription();
                    subCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                        onSubscriptionCheckboxCheckChange(buttonView, isChecked));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BoardDetailsActivity.this,
                        "Board info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateSubscription() {

        DatabaseReference dataReference = mDatabase
                .child("users")
                .child(userUtils.getUserUid())
                .child("subscriptions");

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isKeyPresent = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(boardKey)) {
                        isKeyPresent = true;
                        break;
                    }
                }

                // enable checkbox if not your own
                CheckBox subCheckbox = findViewById(R.id.board_detail_subscription_checkbox);
                subCheckbox.setChecked(isKeyPresent);
                if (!myBoard) {
                    subCheckbox.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BoardDetailsActivity.this,
                        "Board subscription couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSubscriptionCheckboxCheckChange(CompoundButton button, boolean isChecked) {
        // disable to wait for database update
        button.setEnabled(false);
        if (button.isChecked()) {
            mDatabase.child("users")
                    .child(userUtils.getUserUid())
                    .child("subscriptions")
                    .child(boardKey)
                    .setValue("true");
        } else {
            mDatabase.child("users")
                    .child(userUtils.getUserUid())
                    .child("subscriptions")
                    .child(boardKey)
                    .removeValue();
        }
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

    public void onPostClicked(String postKey) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(Post.KEY_FIELD, postKey);
        startActivity(intent);
    }

}
