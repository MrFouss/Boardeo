package fr.fouss.boardeo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class NewBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_board);

        Toolbar toolbar = findViewById(R.id.NewBoardToolbar);
        setSupportActionBar(toolbar);
    }
}
