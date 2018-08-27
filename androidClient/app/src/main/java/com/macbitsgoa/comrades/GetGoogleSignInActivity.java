package com.macbitsgoa.comrades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.macbitsgoa.comrades.CHCKt.BITS_EMAIL_SUFFIX;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * This Activity can be used to make signIn available and ask for storage permissions
 * at specific places in app.
 * if you want this activity to return an accessToken,
 * start activity using ***startActivityForResult(intent,request code)***
 * By default this Activity will not return anything.
 * use onActivityResult() in the calling activity to get the result.
 * use Intent.getExtra() using key as KEY_ACCOUNT to get the user account
 * and KEY_TOKEN to get access token
 *
 * @author Aayush Singla
 */

public class GetGoogleSignInActivity extends Activity {
    public static final String KEY_TOKEN = "token";
    private static final String TAG = TAG_PREFIX + GetGoogleSignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0;
    private static final int ERROR_CODE_PERMISSION_DENIED = 12501;
    private static final int RC_PERM_REQ_EXT_STORAGE = 7;
    private static final String PREFS_FILE_NAME = "first_time_asking";
    private boolean returnResult;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignI\nClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        final Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    /**
     * This method checks onStart of the activity if the user has already signed in for
     * any other feature in the app.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        returnResult = getCallingActivity() != null;
        if (currentUser != null && account.getServerAuthCode() != null) {
            returnResult(account);
        }
    }

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(final Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account.getEmail().endsWith(BITS_EMAIL_SUFFIX)) {
                firebaseAuthWithGoogle(account);
            } else {
                mGoogleSignInClient.signOut();
                Toast.makeText(this, "Please use your BITS mail", Toast.LENGTH_LONG).show();
                finish();
            }

            // Signed in successfully, show authenticated UI.
        } catch (final ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            if (e.getStatusCode() == ERROR_CODE_PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Your request can't be processed.Please try again later.",
                        Toast.LENGTH_LONG).show();
            }
            returnResult(null);
        }
    }

    private void returnResult(final GoogleSignInAccount account) {
        final Intent intent = new Intent();
        if (returnResult && account.getServerAuthCode() != null) {
            final String accessToken = getTemporaryToken(account.getServerAuthCode());
            intent.putExtra(KEY_TOKEN, accessToken);
        }
        setResult(RESULT_OK, intent);
        askStoragePermission();
    }

    private String getTemporaryToken(final String authCode) {
        final StrictMode.ThreadPolicy policy = new StrictMode
                .ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);


        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = null;

        if (authCode != null) {

            requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", getString(R.string.server_client_id))
                    .add("client_secret", getString(R.string.client_secret))
                    .add("redirect_uri",
                            "https://balmy-component-204213.firebaseapp.com/__/auth/handler")
                    .add("code", authCode)
                    .build();
        } else {
            Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        }

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final JSONObject jsonObject = new JSONObject(response.body().string());
            return (String) jsonObject.get("access_token");

        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        returnResult(account);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(GetGoogleSignInActivity.this, "SignIn failed",
                                Toast.LENGTH_LONG).show();
                        // ...
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                Toast.makeText(GetGoogleSignInActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length > 0 && grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied!",
                        Toast.LENGTH_SHORT).show();
            } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                Toast.makeText(GetGoogleSignInActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void askStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(GetGoogleSignInActivity.this,
                READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED && ContextCompat.checkSelfPermission(GetGoogleSignInActivity.this,
                READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(GetGoogleSignInActivity.this,
                    READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(GetGoogleSignInActivity.this,
                    WRITE_EXTERNAL_STORAGE)) {

                String[] permission = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(GetGoogleSignInActivity.this, permission, 7);

            } else {

                if (isFirstTimeAskingPermission(this, STORAGE)) {

                    firstTimeAskingPermission(this, STORAGE, false);
                    String[] permission = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(GetGoogleSignInActivity.this, permission, 7);

                } else {

                    Toast.makeText(GetGoogleSignInActivity.this, "Go to Settings and Grant the Storage permission " +
                            "or clear Data to use this feature.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            finish();
        }
    }
}

