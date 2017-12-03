package fr.fouss.boardeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.listing.BoardAdapter;
import fr.fouss.boardeo.sign_in.SignInActivity;
import fr.fouss.boardeo.utils.UserUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Activity that displays a list of boards
 */
public class BoardListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView usernameLabel;

    private UserUtils userUtils;
    private DatabaseReference mDatabase;

    ///// LIFECYCLE /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        userUtils = new UserUtils(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.boardToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        usernameLabel = navHeaderView.findViewById(R.id.username);

        // setup recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView boardRecyclerView = findViewById(R.id.boardRecyclerList);
        boardRecyclerView.setLayoutManager(layoutManager);
        BoardAdapter boardAdapter = new BoardAdapter(this);
        boardRecyclerView.setAdapter(boardAdapter);
        boardAdapter.initSubscriptionsListener();
        boardAdapter.setBoardClickListener(this::onBoardClicked);

        // set new board button listener
        FloatingActionButton newBoardButton = findViewById(R.id.addBoardButton);
        newBoardButton.setOnClickListener(this::onNewBoardButtonClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userUtils.isSignedIn()) {
            usernameLabel.setText(userUtils.getUserName());
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.child("users").child(userUtils.getUserUid()).child("lastConnection").setValue(new Date().getTime());
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_my_boards:
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_sign_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.dialog_content_sign_out))
                        .setTitle(getString(R.string.dialog_title_sign_out));
                builder.setPositiveButton("Yes", (dialog, id1) -> {
                    userUtils.signOut();
                    startActivity(new Intent(this, SignInActivity.class));
                });
                builder.setNegativeButton("No", (dialog, id1) -> {});
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
