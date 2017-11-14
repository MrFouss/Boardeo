package fr.fouss.boardeo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import fr.fouss.boardeo.R;

public class UserUtils {

    private Activity activity;

    /**
     * Firebase authenticator instance
     */
    private FirebaseAuth mAuth;

    /**
     * Shared preferences file
     */
    private SharedPreferences sharedPreferences;

    private GoogleSignInClient mGoogleSignInClient;

    public UserUtils(Activity activity) {

        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();

        // Shared preferences
        sharedPreferences = activity.getSharedPreferences(
                "fr.fouss.boardeo.GLOBAL_INFO",
                Context.MODE_PRIVATE);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void emailSendVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification();
    }

    private @NonNull FirebaseUser getUser() {
        assert mAuth.getCurrentUser() != null;
        return mAuth.getCurrentUser();
    }

    public Boolean isSignedIn() {
        return sharedPreferences.getBoolean("IsSignedIn", false);
    }

    public void setSignedIn(boolean isSignedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IsSignedIn", isSignedIn);
        editor.apply();
    }

    public String getUserName() {
        return getUser().getDisplayName();
    }

    public String getUserEmail() {
        return getUser().getEmail();
    }

    public Boolean isEmailVerified() {
        return getUser().isEmailVerified();
    }

    public void signOut() {
        switch (getUser().getProviderId()) {
            case GoogleAuthProvider.PROVIDER_ID:
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                break;

            case EmailAuthProvider.PROVIDER_ID:
                mAuth.signOut();
                break;
        }

        setSignedIn(false);
    }
}
