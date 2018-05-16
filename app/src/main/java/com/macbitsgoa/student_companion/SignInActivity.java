package com.macbitsgoa.student_companion;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.macbitsgoa.student_companion.Testcode.REQUEST_GOOGLE_PLAY_SERVICES;


public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, com.squareup.okhttp.Callback {
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 0;
    @BindView(R.id.sign_in_button)
    com.google.android.gms.common.SignInButton SignInButton;
    private String serverClientId = "666225132801-iklcdj36jau98rf2v44a0v1rnguioatd.apps.googleusercontent.com";
    private String clientSecret = "NGfzk-7sJilNmFla6TZy8WrL";
    //private String serverClientId1 = "  666225132801-5d33mmf5uic0d5qecjaogl457psphq03.apps.googleusercontent.com";
    String idTokenString = "";
    String authCode = "";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        checkGooglePlayServices();

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
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        // ...
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


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
        authCode = account.getServerAuthCode();
        Log.e("Response", account.getServerAuthCode());
        firebaseAuthWithGoogle(account);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        Log.e("Response:email:", account.getEmail());
        Log.e("Response:id:", account.getId());
        Log.e("Response:name:", account.getDisplayName());
        finish();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
    }

    void checkGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);

        if (!(connectionStatusCode == ConnectionResult.SUCCESS)) {
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(
                        SignInActivity.this,
                        connectionStatusCode,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();

            }
        }
    }


    void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.e("Response:", "Google User Id :" + account.getId());

        // --------------------------------- //
        // BELOW LINE GIVES YOU JSON WEB TOKEN, (USED TO GET ACCESS TOKEN) : 
        Log.e("Response:", "Google JWT : " + account.getIdToken());
        // --------------------------------- //

        // Save this JWT in global String : 
        idTokenString = account.getIdToken();
        getGoogleToken();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e("Response:", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {

                            // --------------------------------- //
                            // BELOW LINE GIVES YOU FIREBASE TOKEN ID : 
                            //Log.e`("TAG", "Firebase User Access Token : " + task.getResult());
                            // --------------------------------- //
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else {
                            Log.e("Response:", "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void getGoogleToken() {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", serverClientId)   // something like : ...apps.googleusercontent.com
                .add("client_secret", clientSecret)
                .add("redirect_uri", "https://balmy-component-204213.firebaseapp.com/__/auth/handler")
                .add("code", authCode) // device code.
                .add("id_token", idTokenString) // This is what we received in Step 5, the jwt token.
                .build();

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(this);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e("Response:", e.getMessage());
    }

    @Override
    public void onResponse(com.squareup.okhttp.Response response) throws IOException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.body().string());
            String message = jsonObject.toString(5);
            Log.e("Response:AuthKey:", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
