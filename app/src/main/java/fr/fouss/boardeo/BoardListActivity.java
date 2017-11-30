package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.listing.BoardAdapter;

/**
 * Activity that displays a list of boards
 */
public class BoardListActivity extends AppCompatActivity {

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
        BoardAdapter boardAdapter = new BoardAdapter(this);
        boardRecyclerView.setAdapter(boardAdapter);
        boardAdapter.initSubscriptionsListener();
        boardAdapter.setBoardClickListener(this::onBoardClicked);

        // set new board button listener
        FloatingActionButton newBoardButton = findViewById(R.id.addBoardButton);
        newBoardButton.setOnClickListener(this::onNewBoardButtonClick);
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
}
