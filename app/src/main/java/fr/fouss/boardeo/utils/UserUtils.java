package fr.fouss.boardeo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class UserUtils {

    /**
     * Firebase authenticator instance
     */
    private FirebaseAuth mAuth;

    /**
     * Shared preferences file
     */
    private SharedPreferences sharedPreferences;

    public UserUtils(Activity activity) {

        mAuth = FirebaseAuth.getInstance();

        // Shared preferences
        sharedPreferences = activity.getSharedPreferences(
                "fr.fouss.boardeo.GLOBAL_INFO",
                Context.MODE_PRIVATE);
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
        assert mAuth.getCurrentUser() != null;

        switch (mAuth.getCurrentUser().getProviderId()) {
            case GoogleAuthProvider.PROVIDER_ID:
                mAuth.signOut();

                break;

            case EmailAuthProvider.PROVIDER_ID:
                mAuth.signOut();
                break;
        }

        setSignedIn(false);
    }
}
