package fr.fouss.boardeo.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import fr.fouss.boardeo.HomeActivity;
import fr.fouss.boardeo.R;
import fr.fouss.boardeo.utils.UserUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EmailPasswordSignInActivity extends SignInBaseActivity
        implements View.OnClickListener {

    /**
     * Firebase authenticator instance
     */
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;

    private UserUtils userUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_sign_in);

        mAuth = FirebaseAuth.getInstance();

        // User utility class instantiation
        userUtils = new UserUtils(this);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
    }

    private void signIn(String email, String password) {

        if (!validateForm()) {
            return;
        }

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
                                    "Activate your account first\nVerification email sent at: " + userUtils.getUserEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    hideProgressDialog();
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.email_create_account_button:
                startActivity(new Intent(this, EmailPasswordCreateAccountActivity.class));
                break;
            case R.id.email_sign_in_button:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
        }
    }
}
