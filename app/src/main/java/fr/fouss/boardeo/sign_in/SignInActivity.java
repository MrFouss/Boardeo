package fr.fouss.boardeo.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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

public class SignInActivity extends SignInBaseActivity
        implements View.OnClickListener {

    /**
     * Firebase authenticator
     */
    private FirebaseAuth mAuth;

    /**
     * User utility class
     */
    private UserUtils userUtils;

    /**
     * Google Sign in client
     */
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
     */
    private static final int RETURN_CODE_GOOGLE_SIGN_IN = 9001;

    /**
     * User email address field
     */
    private EditText mEmailField;

    /**
     * User password field
     */
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Get the instance of the Firebase authenticator
        mAuth = FirebaseAuth.getInstance();

        // User utility class instantiation
        userUtils = new UserUtils(this);

        // Text fields
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Add listeners on buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_forgotten_password_link).setOnClickListener(this);
        findViewById(R.id.email_create_account_link).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void emailSignIn(String email, String password) {

        if (!signInValidateForm())
            return;

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    userUtils.setSignedIn(task.isSuccessful() && userUtils.isEmailVerified());

                    if (task.isSuccessful()) {
                        if (userUtils.isEmailVerified()) {
                            Intent homeIntent = new Intent(this, HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                        } else {
                            userUtils.emailSendVerification();
                            userUtils.signOut();
                            Toast.makeText(this,
                                    "Activate your account first\nVerification email sent at:\n" + userUtils.getUserEmail(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Email authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    hideProgressDialog();
                });
    }

    private void emailForgottenPassword(String email) {

        if (!forgottenPasswordValidateForm())
            return;

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent at:\n" + email,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Password reset email delivery failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean forgottenPasswordValidateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        return valid;
    }

    private boolean signInValidateForm() {
        boolean valid = forgottenPasswordValidateForm();

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        } else if (password.length() < 6) {
            mPasswordField.setError("Must contain at least 6 characters");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RETURN_CODE_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RETURN_CODE_GOOGLE_SIGN_IN) {
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
                                Toast.makeText(this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                            }

                            hideProgressDialog();
                        });
            } catch (ApiException e) {
                userUtils.setSignedIn(false);
                Toast.makeText(this, "Google authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                emailSignIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.email_forgotten_password_link:
                emailForgottenPassword(mEmailField.getText().toString());
                break;
            case R.id.email_create_account_link:
                startActivity(new Intent(this, CreateAccountActivity.class));
                break;
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}
