package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.fouss.boardeo.listing.BoardData;

public class BoardDetailsActivity extends AppCompatActivity {

    /**
     * The current board data to be returned
     */
    Intent currBoardData;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_details);

        // setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currBoardData = new Intent();
        // copy intent
        currBoardData.putExtras(getIntent());

        // setting texts
        TextView title = findViewById(R.id.detailBoardTitle);
        title.setText(currBoardData.getStringExtra(BoardData.BOARD_NAME_FIELD));
        TextView author = findViewById(R.id.detailAuthorName);
        author.setText(currBoardData.getStringExtra(BoardData.BOARD_AUTHOR_FIELD));
        TextView fullDescription = findViewById(R.id.detailFullDescription);
        fullDescription.setText(currBoardData.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD));

        // setup edit button listener
        Button editButton = findViewById(R.id.editBoardButton);
        editButton.setOnClickListener(v -> onEditBoardButtonClick(v));
    }

    /**
     * When edit button is clicked
     * @param v
     */
    public void onEditBoardButtonClick(View v) {
        // start new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
        intent.putExtras(currBoardData);
        startActivityForResult(intent, MiscUtil.BOARD_CREATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MiscUtil.BOARD_CREATION_REQUEST
                && resultCode == MiscUtil.NEW_BOARD_RESULT) {
            // new board activity normal return

            // copy result intent
            currBoardData.putExtras(data);

            // update views
            TextView title = findViewById(R.id.detailBoardTitle);
            title.setText(currBoardData.getStringExtra(BoardData.BOARD_NAME_FIELD));
            TextView author = findViewById(R.id.detailAuthorName);
            author.setText(currBoardData.getStringExtra(BoardData.BOARD_AUTHOR_FIELD));
            TextView fullDescription = findViewById(R.id.detailFullDescription);
            fullDescription.setText(currBoardData.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD));
        }
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        // copy the current board data to update the requesting activity
        intent.putExtras(currBoardData);
        setResult(MiscUtil.BOARD_DETAIL_RESULT, intent);
        finish();
        return true;
    }

}
