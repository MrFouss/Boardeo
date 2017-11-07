package fr.fouss.boardeo;

import android.app.Activity;
import android.content.Intent;
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
        Intent intent = new Intent(this, NewBoardActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String title = data.getStringExtra(BoardData.BOARD_NAME_FIELD);
        String author = data.getStringExtra(BoardData.BOARD_AUTHOR_FIELD);
        String shortDesc = data.getStringExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD);
        String fullDesc = data.getStringExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD);

        boardAdapter.addBoardItem(new BoardData(title, author, shortDesc, true));
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
            case R.id.deleteBoardButton :
                boardAdapter.removeSelection();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("Board", "back");
        boardAdapter.setSelectionMode(false);
        return true;
    }

    public void onSelectionModeToggle(boolean selectionMode) {
        Log.d("Board", "selection mode toggle : " + selectionMode);
        // show/hide necessary options depending on the mode
        if (selectionMode) {
            toolbarMenu.findItem(R.id.unsubscribeButton).setVisible(true);
            toolbarMenu.findItem(R.id.subscribeButton).setVisible(true);
            toolbarMenu.findItem(R.id.filterSetting).setVisible(false);
            toolbarMenu.findItem(R.id.deleteBoardButton).setVisible(true);
            findViewById(R.id.addBoardButton).setVisibility(View.GONE);

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toolbarMenu.findItem(R.id.unsubscribeButton).setVisible(false);
            toolbarMenu.findItem(R.id.subscribeButton).setVisible(false);
            toolbarMenu.findItem(R.id.filterSetting).setVisible(true);
            toolbarMenu.findItem(R.id.deleteBoardButton).setVisible(false);
            findViewById(R.id.addBoardButton).setVisibility(View.VISIBLE);

            // hide back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

}
