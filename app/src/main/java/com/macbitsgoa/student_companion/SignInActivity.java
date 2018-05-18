package com.macbitsgoa.student_companion;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, com.squareup.okhttp.Callback {
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    GoogleSignInClient signInClient;

    com.google.android.gms.common.SignInButton SignInButton;
    String authCode = "";
    String accessToken;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private int RC_SIGN_IN = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            Log.e("Response:", "Google signIn failed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                checkStoragePermission();
            }
        }
    }

    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(SignInActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(SignInActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignInActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Permission to read storage is required .");
            alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SignInActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(SignInActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERM_REQ_EXT_STORAGE);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Response", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            authCode = account.getServerAuthCode();
            firebaseAuthWithGoogle(account);
            Log.e("Response", account.getServerAuthCode());
            Log.e("Response:email:", account.getEmail());
            Log.e("Response:id:", account.getId());
            Log.e("Response:name:", account.getDisplayName());
        }
    }

    void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        String authCode = account.getServerAuthCode();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = null;

        if (authCode != null) {
            requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", getString(R.string.server_client_id))
                    .add("client_secret", getString(R.string.client_secret))
                    .add("redirect_uri", "https://balmy-component-204213.firebaseapp.com/__/auth/handler")
                    .add("code", authCode) // device code.
                    .build();
        }

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        makePlayServiceAvailable();
        checkStoragePermission();


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e("Response:", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.e("Response:", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        signInClient = GoogleSignIn.getClient(this, gso);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent signInIntent = signInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void initViews() {
        setContentView(R.layout.activity_sign_in);
        SignInButton = findViewById(R.id.sign_in_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e("Response:", e.getMessage());
    }

    @Override
    public void onResponse(com.squareup.okhttp.Response response) throws IOException {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response.body().string());
            accessToken = (String) jsonObject.get("access_token");
            String message = jsonObject.toString(5);
            Log.e("Response:AuthKey:", message);
            if (accessToken != null) {
                Log.e("Response:", accessToken);
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
