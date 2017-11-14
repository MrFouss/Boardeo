package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.fouss.boardeo.listing.BoardRecyclerViewAdapter;
import fr.fouss.boardeo.listing.BoardData;

/**
 * Activity that displays a list of boards
 */
public class BoardListActivity extends AppCompatActivity {

    private MenuItem toolbarUnsubscribeButton;
    private MenuItem toolbarSubscribeButton;
    private MenuItem toolbarFilterButton;
    private MenuItem toolbarDeleteButton;
    private FloatingActionButton newBoardButton;

    private BoardRecyclerViewAdapter boardRecyclerViewAdapter;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        // toolbar
        setSupportActionBar(findViewById(R.id.boardToolbar));

        // setup recycler view
        RecyclerView boardRecyclerView = findViewById(R.id.boardRecyclerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        boardRecyclerView.setLayoutManager(layoutManager);
        boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        boardRecyclerViewAdapter.setModeListener(selectionMode -> onSelectionModeChange(selectionMode));
        boardRecyclerView.setAdapter(boardRecyclerViewAdapter);
        boardRecyclerViewAdapter.setItemClickListener(position -> onBoardClick(position));

        // set new board button listener
        newBoardButton = findViewById(R.id.addBoardButton);
        newBoardButton.setOnClickListener(v -> onNewBoardButtonClick(v));
    }

    ///// EVENTS /////

    /**
     * When a board item is clicked
     * @param position
     */
    public void onBoardClick(int position) {
        if (boardRecyclerViewAdapter.isSelectionMode()) {
            // toggle selection checkbox of the clicked item
            boardRecyclerViewAdapter.toggleSelect(position);
            boardRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            // launch board detail activity
            Intent intent = new Intent(this, BoardDetailsActivity.class);
            BoardData boardData = boardRecyclerViewAdapter.getItem(position);
            boardData.fillIntentExtras(intent);
            startActivityForResult(intent, MiscUtil.BOARD_DETAIL_REQUEST);
        }
    }

    /**
     * When the new board floating button is clicked
     * @param v
     */
    public void onNewBoardButtonClick(View v) {
        // launch new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
        startActivityForResult(intent, MiscUtil.BOARD_CREATION_REQUEST);
    }

    /**
     * When a previously launched activity returns
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MiscUtil.BOARD_CREATION_REQUEST && resultCode == MiscUtil.NEW_BOARD_RESULT) {
            // normal return of new board activity
            boardRecyclerViewAdapter.addItem(new fr.fouss.boardeo.listing.BoardData(data));
        } else if (requestCode == MiscUtil.BOARD_DETAIL_REQUEST && resultCode == MiscUtil.BOARD_DETAIL_RESULT) {
            // normal return of detail board activity
            BoardData boardData = boardRecyclerViewAdapter.getItemById(data.getIntExtra(BoardData.BOARD_ID_FIELD, -1));
            boardData.setFromIntent(data);
        }
        boardRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * When creating the toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_list_menu, menu);
        toolbarUnsubscribeButton = menu.findItem(R.id.unsubscribeButton);
        toolbarSubscribeButton = menu.findItem(R.id.subscribeButton);
        toolbarFilterButton = menu.findItem(R.id.filterSetting);
        toolbarDeleteButton = menu.findItem(R.id.deleteBoardButton);
        return true;
    }

    /**
     * When a menu item in the toolbar is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unsubscribeButton :
                boardRecyclerViewAdapter.setSelectionSubscription(false);
                boardRecyclerViewAdapter.clearSelection();
                boardRecyclerViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.subscribeButton :
                boardRecyclerViewAdapter.setSelectionSubscription(true);
                boardRecyclerViewAdapter.clearSelection();
                boardRecyclerViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.deleteBoardButton :
                onDeleteBoardButtonClick();
                return true;
            case R.id.filterSetting :
                // TODO
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the delete button is clicked in the toobar
     */
    public void onDeleteBoardButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Do you really want to delete boards?")
                .setTitle("Warning!")
                .setPositiveButton("Yes", (dialog, id) -> {
                    boardRecyclerViewAdapter.deleteSelection();
                    onSupportNavigateUp();
                })
                .setNegativeButton("No", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * When the naviguation up button in toolbar is clicked
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        boardRecyclerViewAdapter.setSelectionMode(false);
        boardRecyclerViewAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (boardRecyclerViewAdapter.isSelectionMode()) {
            onSupportNavigateUp();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When the mode of board list recycler view changes
     * @param selectionMode
     */
    public void onSelectionModeChange(boolean selectionMode) {
        // show/hide necessary options depending on the mode
        if (selectionMode) {
            toolbarUnsubscribeButton.setVisible(true);
            toolbarSubscribeButton.setVisible(true);
            toolbarFilterButton.setVisible(false);
            toolbarDeleteButton.setVisible(true);
            newBoardButton.setVisibility(View.GONE);

            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toolbarUnsubscribeButton.setVisible(false);
            toolbarSubscribeButton.setVisible(false);
            toolbarFilterButton.setVisible(true);
            toolbarDeleteButton.setVisible(false);
            newBoardButton.setVisibility(View.VISIBLE);

            // hide back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

}
