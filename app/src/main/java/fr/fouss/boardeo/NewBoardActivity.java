package fr.fouss.boardeo;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class NewBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        Toolbar toolbar = findViewById(R.id.NewBoardToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addButton = findViewById(R.id.validateBoardEditionButton);
        addButton.setOnClickListener(v -> onAddButtonClicked(v));
    }

    public void onAddButtonClicked(View v) {
        Intent resultData = new Intent();

        EditText title = findViewById(R.id.titleField);
        EditText author = findViewById(R.id.authorField);
        EditText shortDesc = findViewById(R.id.shortDescField);
        EditText fullDesc = findViewById(R.id.fullDescField);

        resultData.putExtra(BoardData.BOARD_NAME_FIELD, title.getText().toString());
        resultData.putExtra(BoardData.BOARD_AUTHOR_FIELD, author.getText().toString());
        resultData.putExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD, shortDesc.getText().toString());
        resultData.putExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD, fullDesc.getText().toString());

        setResult(Activity.RESULT_OK, resultData);

        finish();
    }
}
