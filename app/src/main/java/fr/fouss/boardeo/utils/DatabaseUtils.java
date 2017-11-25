package fr.fouss.boardeo.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
