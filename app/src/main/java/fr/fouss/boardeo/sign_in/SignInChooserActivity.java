package fr.fouss.boardeo.sign_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import fr.fouss.boardeo.R;

public class SignInChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_chooser);

        final Button emailPasswordSignInButton = findViewById(R.id.button_signin_emailpassword);
        emailPasswordSignInButton.setOnClickListener(e -> {
            startActivity(new Intent(this, EmailPasswordSignInActivity.class));
        });

        final Button googleSignInButton = findViewById(R.id.button_signin_google);
        googleSignInButton.setOnClickListener(e -> {
            startActivity(new Intent(this, GoogleSignInActivity.class));
        });
    }
}
