package fr.fouss.boardeo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // data base reference
        userUtils = new UserUtils(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postKey = getIntent().getStringExtra(Post.KEY_FIELD);
        if (postKey != null) {
            updateTextFields();
        }
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
}
