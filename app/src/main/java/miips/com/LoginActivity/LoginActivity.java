package miips.com.LoginActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import miips.com.Home.HomeActivity;
import miips.com.R;
import miips.com.Register.google.RegisterActivityGoogle;
import miips.com.Register.normal.RegisterActivity;
import miips.com.Utils.ConnectionDetector;

import static miips.com.Home.HomeActivity.activeH;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInButton googleButton;
    GoogleSignInClient mGoogleSignInClient;

    //GooglePlayServices
    private static final int ERROR_DIALOG_REQUEST = 9001;


    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private String userID;

    ConnectionDetector cd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.loadingLoginProgressBar);
        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        mContext = LoginActivity.this;
        googleButton = findViewById(R.id.google_button);
        mProgressBar.setVisibility(View.GONE);
        //Verify internet connection
        cd = new ConnectionDetector(this);
        setupFirebaseAuth();

        if (isServiceOK()) {
            init();
            singInGoogle();
        }
    }

    //googlePlayServicesVerify
    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map request
            Log.d(TAG, "isServiceOK: Google Play Service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it
            Log.d(TAG, "isServiceOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Versão do celular ultrapassada", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Não foi possivel logar com a google", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Erro ao tentar entrar com Google", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                userID = mAuth.getCurrentUser().getUid();
                                Log.d(TAG, "onComplete: userID ta assim: " + userID);
                                Log.d(TAG, "onComplete: flag 0");
                                DocumentReference docRef = db.collection(mContext.getString(R.string.dbname_user)).document(userID);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                Intent intent = new Intent(mContext, HomeActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                Log.d(TAG, "onDataChange: flag1");
                                                finish();
                                            } else {
                                                Log.d(TAG, "No such document");
                                                mProgressBar.setVisibility(View.GONE);
                                                Intent intent1 = new Intent(mContext, RegisterActivityGoogle.class);
                                                startActivity(intent1);
                                                Log.d(TAG, "onDataChange: flag 2");
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            buildDialog(LoginActivity.this).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                        // ...
                    }
                });
    }


    private void singInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }


    private boolean isStringNull(String string) {
        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    private void init() {
        //initialize the button for logging in
        Button btnLogin = findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();


                //Identify internet connection
                if (cd.isConnected()) {
                    if (isStringNull(email)) {
                        mEmail.setError("Digite seu email");
                    } else if (isStringNull(password)) {
                        mPassword.setError("Digite sua senha");
                    } else if (password.length() < 6 && password.length() > 0) {
                        mPassword.setError("Senha precisa ter 6 ou mais caracteres!");
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            /**
                                             * If the user is Logged in the navigate to HomeActivity and call "finish()"
                                             */
                                            if (user != null) {
                                                mProgressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(mContext, HomeActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                LoginActivity.this.finish();
                                                mPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                            }

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            mPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                            Toast.makeText(mContext, "E-mail ou senha incorretos",
                                                    Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                        }

                                        // ...
                                    }
                                });
                    }
                } else {
                    buildDialog(LoginActivity.this).show();
                }

            }
        });

        TextView linkSingUp = findViewById(R.id.link_singup);
        linkSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        TextView linkResetPassword = findViewById(R.id.esqueceusenha);
        linkResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSendPass = new Intent(mContext, ForgotPassword.class);
                startActivity(intentSendPass);
            }
        });

    }


    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in

                } else {
                    //User is signed out
                }
            }
        };
    }


    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Sem conexão com internet");
        builder.setMessage("É necessária uma conexão de internet para prosseguir!");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            if (activeH) {
                LoginActivity.this.finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        mProgressBar.setVisibility(View.GONE);
    }
}
