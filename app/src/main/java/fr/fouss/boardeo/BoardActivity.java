package fr.fouss.boardeo;

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

    private Menu toolbarMenu;
    private BoardItemAdapter boardAdapter;

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
        boardAdapter = adapter;
        adapter.setModeListener(selectionMode -> onSelectionModeToggle(selectionMode));
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
        toolbarMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.unsubscribeButton :
                boardAdapter.setSubscriptionOnSelection(false);
                return true;
            case R.id.subscribeButton :
                boardAdapter.setSubscriptionOnSelection(true);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSelectionModeToggle(boolean selectionMode) {
        Log.d("Board", "selection mode toggle : " + selectionMode);
        // show/hide necessary options depending on the mode
        if (selectionMode) {
            toolbarMenu.findItem(R.id.unsubscribeButton).setVisible(true);
            toolbarMenu.findItem(R.id.subscribeButton).setVisible(true);
            toolbarMenu.findItem(R.id.filterSetting).setVisible(false);
        } else {
            toolbarMenu.findItem(R.id.unsubscribeButton).setVisible(false);
            toolbarMenu.findItem(R.id.subscribeButton).setVisible(false);
            toolbarMenu.findItem(R.id.filterSetting).setVisible(true);
        }
    }
}
