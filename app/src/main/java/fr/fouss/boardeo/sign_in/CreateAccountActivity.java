package fr.fouss.boardeo.sign_in;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import fr.fouss.boardeo.HomeActivity;
import fr.fouss.boardeo.R;
import fr.fouss.boardeo.utils.UserUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CreateAccountActivity extends SignInBaseActivity
        implements View.OnClickListener {

    /**
     * Firebase authenticator instance
     */
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mPasswordAgainField;

    private UserUtils userUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        // User utility class instantiation
        userUtils = new UserUtils(this);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mUsernameField = findViewById(R.id.field_username);
        mPasswordField = findViewById(R.id.field_password);
        mPasswordAgainField = findViewById(R.id.field_password_again);

        // Buttons
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
    }

    private void createAccount(String email, String username, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    userUtils.setSignedIn(task.isSuccessful());

                    if (task.isSuccessful()) {
                        userUtils.emailSendVerification();

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        assert mAuth.getCurrentUser() != null;

                        mAuth.getCurrentUser().updateProfile(profileUpdate)
                                .addOnCompleteListener(task1 -> {
                                    if(task.isSuccessful()) {
                                        // 1. Instantiate an AlertDialog.Builder with its constructor
                                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                                        // 2. Chain together various setter methods to set the dialog characteristics
                                        builder.setMessage(getString(R.string.dialog_content_email_account_created))
                                                .setTitle(getString(R.string.dialog_title_email_account_created));

                                        builder.setCancelable(false);

                                        // Add the buttons
                                        builder.setNeutralButton("Ok", (dialog, id1) -> {
                                            userUtils.signOut();
                                            Intent intent = new Intent(this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        });

                                        // 3. Get the AlertDialog from create()
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    } else {
                                        Toast.makeText(this,
                                                "Account update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
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

        String username = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError("Required");
            valid = false;
        } else {
            mUsernameField.setError(null);
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

        String passwordAgain = mPasswordAgainField.getText().toString();
        if (TextUtils.isEmpty(passwordAgain)) {
            mPasswordAgainField.setError("Required");
            valid = false;
        } else if (!passwordAgain.equals(password)) {
            mPasswordAgainField.setError("Passwords must be the same");
            valid = false;
        } else {
            mPasswordAgainField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_create_account_button:
                createAccount(mEmailField.getText().toString(),
                        mUsernameField.getText().toString(),
                        mPasswordField.getText().toString());
                break;
        }
    }
}
