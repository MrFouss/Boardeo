package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.listing.BoardAdapter;

/**
 * Activity that displays a list of boards
 */
public class BoardListActivity extends AppCompatActivity {

    private FloatingActionButton newBoardButton;

    private BoardAdapter boardAdapter;

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
        boardAdapter = new BoardAdapter(this);
        boardRecyclerView.setAdapter(boardAdapter);
        boardAdapter.initSubscriptionsListener();
        boardAdapter.setBoardClickListener(key -> onBoardClicked(key));

        // set new board button listener
        newBoardButton = findViewById(R.id.addBoardButton);
        newBoardButton.setOnClickListener(v -> onNewBoardButtonClick(v));
    }

    ///// EVENTS /////

    public void onBoardClicked(String key) {
        Intent intent = new Intent(this, BoardDetailsActivity.class);
        intent.putExtra(Board.KEY_FIELD, key);
        startActivity(intent);
    }

    /**
     * When the new board floating button is clicked
     * @param v
     */
    public void onNewBoardButtonClick(View v) {
        // launch new board activity
        Intent intent = new Intent(this, NewBoardActivity.class);
        startActivity(intent);
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
        return true;
    }

    /**
     * When a menu item in the toolbar is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
//        switch (item.getItemId()) {
//            case R.id.unsubscribeButton :
//                boardAdapter.setSelectionSubscription(false);
//                boardAdapter.clearSelection();
//                boardAdapter.notifyDataSetChanged();
//                return true;
//            case R.id.subscribeButton :
//                boardAdapter.setSelectionSubscription(true);
//                boardAdapter.clearSelection();
//                boardAdapter.notifyDataSetChanged();
//                return true;
//            case R.id.deleteBoardButton :
//                onDeleteBoardButtonClick();
//                return true;
//            case R.id.filterSetting :
//                // TODO
//                return true;
//            default :
//                return super.onOptionsItemSelected(item);
//        }
    }
}
