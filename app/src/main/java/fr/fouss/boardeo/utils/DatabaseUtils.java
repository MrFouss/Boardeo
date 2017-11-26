package fr.fouss.boardeo.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import fr.fouss.boardeo.data.Board;

public class DatabaseUtils {

    /**
     * Firebase authenticator instance
     */
    private DatabaseReference mDatabase;

    /**
     * Shared preferences file
     */
    private SharedPreferences sharedPreferences;

    public DatabaseUtils(Activity activity) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void createNewBoard(Board board) {
        mDatabase.child("boards").push().setValue(board);
    }
}
