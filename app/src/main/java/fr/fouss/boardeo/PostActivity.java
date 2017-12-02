package fr.fouss.boardeo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.data.Post;
import fr.fouss.boardeo.utils.UserUtils;

public class PostActivity extends AppCompatActivity {

    private String postKey;
    private UserUtils userUtils;
    private DatabaseReference mDatabase;

    private Post post;
    public boolean boardRetrievalLauched = false;
    private boolean boardRetrieved = false;
    private Board board;
    private boolean menuInflated = false;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // data base reference
        userUtils = new UserUtils(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postKey = getIntent().getStringExtra(Post.KEY_FIELD);
        updateTextFields();
    }

    private void retrieveBoard() {
        DatabaseReference dataReference = mDatabase.child("boards").child(post.getBoardKey());

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                board = dataSnapshot.getValue(Board.class);
                assert board != null;
                boardRetrieved = true;
                updateMenuVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this,
                        "Board info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTextFields() {
        DatabaseReference dataReference = mDatabase.child("posts").child(postKey);

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                assert post != null;

                // Setup of texts
                TextView titleLabel = findViewById(R.id.post_title_label);
                TextView contentLabel = findViewById(R.id.post_content_label);
                TextView dateLabel = findViewById(R.id.post_date_label);
                TextView authorLabel = findViewById(R.id.post_author_label);
                titleLabel.setText(post.getTitle());
                contentLabel.setText(post.getContent());
                dateLabel.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(post.getTimestamp())));
                authorLabel.setText(post.getAuthorUid());

                Button editPostButton = findViewById(R.id.edit_post_button);
                if (post.getAuthorUid().equals(userUtils.getUserUid())) {
                    editPostButton.setOnClickListener(v -> onEditPostButtonClicked(v));
                } else {
                    editPostButton.setVisibility(View.GONE);
                }

                if (!boardRetrievalLauched) {
                    boardRetrievalLauched = true;
                    retrieveBoard();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this,
                        "Post info couldn't be retrieved",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onEditPostButtonClicked(View v) {
        Intent intent = new Intent(this, NewPostActivity.class);
        intent.putExtra(Post.KEY_FIELD, postKey);
        intent.putExtra(Board.KEY_FIELD, post.getBoardKey());
        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);
        // Setup of menu
        menuInflated = true;
        updateMenuVisibility();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post_delete_menu_item :
                onDeleteMenuItemClick();
                return true;
            case R.id.post_edit_menu_item :
                onEditMenuItemClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMenuVisibility() {
        if (menuInflated && boardRetrieved) {
            if (post.getAuthorUid().equals(userUtils.getUserUid())) {
                // author
                toolbar.getMenu().findItem(R.id.post_edit_menu_item).setVisible(true);
                toolbar.getMenu().findItem(R.id.post_delete_menu_item).setVisible(true);
            } else if (board.getOwnerUid().equals(userUtils.getUserUid())) {
                // board owner
                toolbar.getMenu().findItem(R.id.post_delete_menu_item).setVisible(true);
            }
        }
    }

    private void onDeleteMenuItemClick() {

    }

    private void onEditMenuItemClick() {

    }
}
