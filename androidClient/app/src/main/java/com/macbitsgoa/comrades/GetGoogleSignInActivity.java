package com.macbitsgoa.comrades;

import android.app.Activity;
import android.content.Intent;
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
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
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
    private boolean returnResult;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignI\nClient with the options specified by gso.
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        returnResult = getCallingActivity() != null;
        if (account != null) {
            returnResult(account);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied!, Retrying.",
                        Toast.LENGTH_SHORT).show();
                askStoragePermission();
            } else if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                finish();
            }
        }
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
            // Signed in successfully, show authenticated UI.
            returnResult(account);
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
        if (returnResult && account != null) {
            final String accessToken = firebaseAuthWithGoogle(account);
            intent.putExtra(KEY_TOKEN, accessToken);
        }
        setResult(RESULT_OK, intent);
        askStoragePermission();
        finish();

    }

    private String firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        final StrictMode.ThreadPolicy policy = new StrictMode
                .ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        final String authCode = account.getServerAuthCode();

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

    private void askStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            return;
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            final AlertDialog.Builder alertBuilder = new
                    AlertDialog.Builder(GetGoogleSignInActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("read access of external storage");
            alertBuilder.setMessage("Permission to read storage is required.");
            alertBuilder.setPositiveButton("Proceed", (dialog, which) ->
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 7));
            final AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, RC_PERM_REQ_EXT_STORAGE);
        }
    }


}
