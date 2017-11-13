package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.fouss.boardeo.listing.BoardData;

public class BoardDetailsActivity extends AppCompatActivity {

    Intent currBoardValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting texts
        currBoardValues = new Intent();
        currBoardValues.putExtras(getIntent());
        TextView title = findViewById(R.id.detailBoardTitle);
        title.setText(currBoardValues.getStringExtra(BoardData.BOARD_NAME_FIELD));
        TextView author = findViewById(R.id.detailAuthorName);
        author.setText(currBoardValues.getStringExtra(BoardData.BOARD_AUTHOR_FIELD));
        TextView fullDescription = findViewById(R.id.detailFullDescription);
        fullDescription.setText(currBoardValues.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD));

        Button editButton = findViewById(R.id.editBoardButton);
        editButton.setOnClickListener(v -> onEditBoardButtonClick(v));
    }

    public void onEditBoardButtonClick(View v) {
        Intent intent = new Intent(this, NewBoardActivity.class);
        intent.putExtras(currBoardValues);
        startActivityForResult(intent, MiscUtil.BOARD_CREATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MiscUtil.BOARD_CREATION_REQUEST
                && resultCode == MiscUtil.NEW_BOARD_RESULT) {
            currBoardValues.putExtras(data);
            TextView title = findViewById(R.id.detailBoardTitle);
            title.setText(currBoardValues.getStringExtra(BoardData.BOARD_NAME_FIELD));
            TextView author = findViewById(R.id.detailAuthorName);
            author.setText(currBoardValues.getStringExtra(BoardData.BOARD_AUTHOR_FIELD));
            TextView fullDescription = findViewById(R.id.detailFullDescription);
            fullDescription.setText(currBoardValues.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        intent.putExtras(currBoardValues);
        setResult(MiscUtil.BOARD_DETAIL_RESULT, intent);
        finish();
        return true;
    }

}
