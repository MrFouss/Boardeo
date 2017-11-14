package fr.fouss.boardeo.sign_in;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import fr.fouss.boardeo.HomeActivity;
import fr.fouss.boardeo.R;
import fr.fouss.boardeo.utils.UserUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GoogleSignInActivity extends SignInBaseActivity
        implements View.OnClickListener {

    /**
     * Firebase authenticator instance
     */
    private FirebaseAuth mAuth;

    /**
     * User utility class
     */
    private UserUtils userUtils;

    /**
     * Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
     */
    private static final int RC_SIGN_IN = 9001;

    /**
     * Google Sign in client
     */
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        mAuth = FirebaseAuth.getInstance();

        // User utility class instantiation
        userUtils = new UserUtils(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                showProgressDialog();

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task1 -> {
                            userUtils.setSignedIn(task1.isSuccessful());

                            if(task1.isSuccessful()) {
                                Intent homeIntent = new Intent(this, HomeActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                                startActivity(homeIntent);
                            } else {
                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }

                            hideProgressDialog();
                        });
            } catch (ApiException e) {
                userUtils.setSignedIn(false);
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}

