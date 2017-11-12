package fr.fouss.boardeo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import fr.fouss.boardeo.sign_in.SignInChooserActivity;

public class HomeActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Shared preferences
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Boolean isLoggedIn = sharedPreferences.getBoolean("IsUserLoggedIn", false);

        if (isLoggedIn) {

        } else {
            startActivity(new Intent(this, SignInChooserActivity.class));
        }
    }
}
