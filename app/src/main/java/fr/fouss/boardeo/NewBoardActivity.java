package fr.fouss.boardeo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import fr.fouss.boardeo.listing.BoardData;

public class NewBoardActivity extends AppCompatActivity {

    Intent request;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        // setup toolbar
        setSupportActionBar(findViewById(R.id.NewBoardToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup validation button
        findViewById(R.id.validateBoardEditionButton).setOnClickListener(v -> onValidateButtonClick(v));

        // get request intent
        request = getIntent();

        // setup fields based on request intent
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

    ///// EVENTS /////

    /**
     * When validation floating button is clicked
     * @param v
     */
    public void onValidateButtonClick(View v) {
        // retrieve fields
        EditText title = findViewById(R.id.titleField);
        EditText author = findViewById(R.id.authorField);
        EditText shortDesc = findViewById(R.id.shortDescField);
        EditText fullDesc = findViewById(R.id.fullDescField);

        boolean filled = true;
        if (title.length() == 0) {
            title.setError("Missing");
            filled = false;
        }
        if (author.length() == 0) {
            author.setError("Missing");
            filled = false;
        }
        if (shortDesc.length() == 0) {
            shortDesc.setError("Missing");
            filled = false;
        }
        if (fullDesc.length() == 0) {
            fullDesc.setError("Missing");
            filled = false;
        }
        if (filled) {
            onReturnResult();
        }
    }

    /**
     * When returning the new board data
     * Data should have been verified before calling this
     */
    public void onReturnResult() {
        // retrieve fields
        EditText title = findViewById(R.id.titleField);
        EditText author = findViewById(R.id.authorField);
        EditText shortDesc = findViewById(R.id.shortDescField);
        EditText fullDesc = findViewById(R.id.fullDescField);
        CheckBox subCheckbox = findViewById(R.id.newBoardSubCheckBox);

        Intent result = new Intent();

        // copy request intent
        result.putExtras(request);

        // replace some fields in the result intent
        result.putExtra(BoardData.BOARD_NAME_FIELD, title.getText().toString());
        result.putExtra(BoardData.BOARD_AUTHOR_FIELD, author.getText().toString());
        result.putExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD, shortDesc.getText().toString());
        result.putExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD, fullDesc.getText().toString());
        result.putExtra(BoardData.BOARD_SUBSCRIPTION_FIELD, subCheckbox.isChecked());

        // return to requesting activity
        setResult(MiscUtil.NEW_BOARD_RESULT, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // return without any result
        setResult(MiscUtil.CANCEL_NEW_BOARD_RESULT);
        finish();
        return true;
    }
}
