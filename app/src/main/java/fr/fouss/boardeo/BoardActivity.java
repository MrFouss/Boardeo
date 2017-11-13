package fr.fouss.boardeo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.fouss.boardeo.listing.BoardRecyclerViewAdapter;
import fr.fouss.boardeo.listing.BoardData;

/**
 * Activity that displays a list of boards
 */
public class BoardActivity extends AppCompatActivity {

    private Menu toolbarMenu;
    private BoardRecyclerViewAdapter boardAdapter;

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
        BoardRecyclerViewAdapter adapter = new BoardRecyclerViewAdapter();
        boardAdapter = adapter;
        adapter.setModeListener(selectionMode -> onSelectionModeToggle(selectionMode));
        boardRecyclerList.setAdapter(adapter);
        adapter.setItemClickListener(position -> onBoardClick(position));

        // set add board button listener
        FloatingActionButton addButton = findViewById(R.id.addBoardButton);
        addButton.setOnClickListener(v -> onAddBoardButtonClick(v));
    }

    public void onBoardClick(int position) {
        Intent intent = new Intent(this, BoardDetailsActivity.class);
        BoardData d = boardAdapter.getItem(position);
        d.fillIntentExtras(intent);
        startActivityForResult(intent, MiscUtil.BOARD_DETAIL_REQUEST);
    }

    public void onAddBoardButtonClick(View v) {
        // launch new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
//        BoardData.fillDefaultIntentExtras(intent);
        startActivityForResult(intent, MiscUtil.BOARD_CREATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MiscUtil.BOARD_CREATION_REQUEST && resultCode == MiscUtil.NEW_BOARD_RESULT) {
            boardAdapter.addItem(new fr.fouss.boardeo.listing.BoardData(data));
            boardAdapter.notifyDataSetChanged();
        } else if (requestCode == MiscUtil.BOARD_DETAIL_REQUEST && resultCode == MiscUtil.BOARD_DETAIL_RESULT) {
            BoardData d = boardAdapter.getItemById(data.getIntExtra(BoardData.BOARD_ID_FIELD, -1));
            d.setFromIntent(data);
            boardAdapter.notifyDataSetChanged();
        }
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
                boardAdapter.setSelectionSubscription(false);
                boardAdapter.notifyDataSetChanged();
                return true;
            case R.id.subscribeButton :
                boardAdapter.setSelectionSubscription(true);
                boardAdapter.notifyDataSetChanged();
                return true;
            case R.id.deleteBoardButton :
                removeSelection();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void removeSelection() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Do you really whan to delete boards ?")
                .setTitle("Warning !");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boardAdapter.deleteSelection();
                boardAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("Board", "back");
        boardAdapter.setSelectionMode(false);
        boardAdapter.notifyDataSetChanged();
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
