package com.macbitsgoa.comrades.ref;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.macbitsgoa.comrades.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;


public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, Callback, View.OnClickListener {
    public static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int RC_PERM_REQ_EXT_STORAGE = 7;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MAC->" + SignInActivity.class.getSimpleName();
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                final Task<GoogleSignInAccount> task = getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else {
                Log.e(TAG, "Sign in result not ok, result code is " + resultCode);
            }
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
            }
        }
    }

    private void askStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            return;
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignInActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("read access of external storage");
            alertBuilder.setMessage("Permission to read storage is required.");
            alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(final DialogInterface dialog, final int which) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 7);
                }
            });
            final AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, RC_PERM_REQ_EXT_STORAGE);
        }
    }

    private void handleSignInResult(final Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUi(account);
        } catch (final ApiException e) {
            Log.w(TAG, "sign in result failed " + e.getMessage(), e.fillInStackTrace());
            updateUi(null);
        }
    }

    private void updateUi(final GoogleSignInAccount account) {
        if (account != null) {
            firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        final String authCode = account.getServerAuthCode();

        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = null;

        if (authCode != null) {
            requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", getString(R.string.server_client_id))
                    .add("client_secret", getString(R.string.client_secret))
                    .add("redirect_uri", getString(R.string.redirect_url))
                    .add("code", authCode)
                    .build();
        }

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        makePlayServiceAvailable();
        askStoragePermission();


        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        signInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(this);
    }

    private void initViews() {
        setContentView(R.layout.activity_sign_in);
        signInButton = findViewById(R.id.sign_in_button);
    }

    private void makePlayServiceAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (connectionStatusCode != ConnectionResult.SUCCESS
                && apiAvailability.isUserResolvableError(connectionStatusCode)) {
            final Dialog dialog = apiAvailability.getErrorDialog(
                    SignInActivity.this,
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(final Request request, final IOException e) {
        Log.e(TAG, e.getMessage(), e.fillInStackTrace());
    }

    @Override
    public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response.body().string());
            final String accessToken = (String) jsonObject.get("access_token");
            final String message = jsonObject.toString(5);
            Log.e("Response:AuthKey:", message);
            if (accessToken != null) {
                final Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(ACCESS_TOKEN_KEY, accessToken);
                startActivity(intent);
                finish();
            }
        } catch (final JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    @Override
    public void onClick(final View view) {
        final int id = view.getId();
        if (id == R.id.sign_in_button) {
            final Intent signInIntent = signInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
}
