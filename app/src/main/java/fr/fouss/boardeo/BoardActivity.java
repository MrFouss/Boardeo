package fr.fouss.boardeo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Activity that displays a list of boards
 */
public class BoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_board);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.boardToolbar);
        setSupportActionBar(toolbar);

        // setup recycler view
        RecyclerView boardRecyclerList = findViewById(R.id.boardRecyclerList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        boardRecyclerList.setLayoutManager(mLayoutManager);
        BoardItemAdapter adapter = new BoardItemAdapter();
        boardRecyclerList.setAdapter(adapter);

        // set add board button listener
        FloatingActionButton addButton = findViewById(R.id.addBoardButton);
        addButton.setOnClickListener(v -> onAddBoardButtonClick(v));
    }

    public void onAddBoardButtonClick(View v) {
        // launch new board activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filterSetting) {
            Log.d("Board", "filter setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
