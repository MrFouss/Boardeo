package fr.fouss.boardeo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import fr.fouss.boardeo.listing.BoardData;

public class NewBoardActivity extends AppCompatActivity {

    Intent request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        Toolbar toolbar = findViewById(R.id.NewBoardToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton addButton = findViewById(R.id.validateBoardEditionButton);
        addButton.setOnClickListener(v -> onValidateButtonClick(v));

        request = getIntent();
        EditText title = findViewById(R.id.titleField);
        title.setText(request.getStringExtra(BoardData.BOARD_NAME_FIELD));
        EditText author = findViewById(R.id.authorField);
        author.setText(request.getStringExtra(BoardData.BOARD_AUTHOR_FIELD));
        EditText shortDesc = findViewById(R.id.shortDescField);
        shortDesc.setText(request.getStringExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD));
        EditText fullDesc = findViewById(R.id.fullDescField);
        fullDesc.setText(request.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD));
        CheckBox subCheckbox = findViewById(R.id.newBoardSubCheckBox);
        subCheckbox.setChecked(request.getBooleanExtra(BoardData.BOARD_SUBSCRIPTION_FIELD, true));
    }

    public void onValidateButtonClick(View v) {
        Intent resultData = new Intent();
        resultData.putExtras(request);

        EditText title = findViewById(R.id.titleField);
        EditText author = findViewById(R.id.authorField);
        EditText shortDesc = findViewById(R.id.shortDescField);
        EditText fullDesc = findViewById(R.id.fullDescField);
        CheckBox subCheckbox = findViewById(R.id.newBoardSubCheckBox);

        resultData.putExtra(BoardData.BOARD_NAME_FIELD, title.getText().toString());
        resultData.putExtra(BoardData.BOARD_AUTHOR_FIELD, author.getText().toString());
        resultData.putExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD, shortDesc.getText().toString());
        resultData.putExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD, fullDesc.getText().toString());
        resultData.putExtra(BoardData.BOARD_SUBSCRIPTION_FIELD, subCheckbox.isChecked());

        setResult(MiscUtil.NEW_BOARD_RESULT, resultData);

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(MiscUtil.CANCEL_NEW_BOARD_RESULT);
        finish();
        return true;
    }
}
