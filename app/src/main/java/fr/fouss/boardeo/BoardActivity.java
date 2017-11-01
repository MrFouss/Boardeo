package fr.fouss.boardeo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Activity that displays a list of boards
 */
public class BoardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_board);

        // setup recycler view
        RecyclerView boardRecyclerList = findViewById(R.id.boardRecyclerList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        boardRecyclerList.setLayoutManager(mLayoutManager);
        BoardItemAdapter adapter = new BoardItemAdapter();
        boardRecyclerList.setAdapter(adapter);
    }
}
