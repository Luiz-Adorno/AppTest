package miips.com.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.Messages.MessagesActivity;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.MyPreference;

public class AccountActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUMBER = 3;
    private Context context;
    private ImageView profilePhoto;
    private TextView editProfile, password, singOut, politics, terms, helpCenter, feedback, miipsID, cityWidgets;
    public static boolean activeP = false;
    private ProgressBar mProgressBar;
    private static User settings;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private static final String TAG = AccountActivity.class.getName();

    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        context = AccountActivity.this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        initWidgets();
        setupBottomNavigationViewEx();

        //FIREBASE GOOGLE GET USER
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            getUserData();
            userIntent();
        } else {
            intentNoUser();
        }
    }

    private void initWidgets() {
        mProgressBar = findViewById(R.id.progressBar_cyclic);
        profilePhoto = findViewById(R.id.ic_profile);
        miipsID = findViewById(R.id.miips_id);
        editProfile = findViewById(R.id.editProfile);
        singOut = findViewById(R.id.btn_out);
        password = findViewById(R.id.password);
        politics = findViewById(R.id.politic);
        terms = findViewById(R.id.terms);
        helpCenter = findViewById(R.id.helpCenter);
        feedback = findViewById(R.id.feedback);
        cityWidgets = findViewById(R.id.city);
    }

    private void intentNoUser() {
        RelativeLayout relativeLayout = findViewById(R.id.topPanel);
        relativeLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        Picasso.get().load(R.drawable.nouser).into(profilePhoto);
        miipsID.setText("Fazer login ou registrar-se");
        miipsID.setTextColor(ContextCompat.getColor(context, R.color.link_blue));
        cityWidgets.setTextColor(Color.BLACK);
        MyPreference myPreference = new MyPreference(context);
        String doc_id = myPreference.getToken();
        cityWidgets.setText(doc_id);

        miipsID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreference myPreference = new MyPreference(context);
                myPreference.setFLAG(true);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
    }

    private void userIntent() {
        //--------------------------------------     ---------------------------------------------//
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        //--------------------------------------     ---------------------------------------------//

        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

                AlertDialog.Builder alertSingOut = new AlertDialog.Builder(context);
                alertSingOut.setMessage("Sair do Miips?");
                alertSingOut.setCancelable(true);
                alertSingOut.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        MyPreference myPreference = new MyPreference(context);
                        myPreference.setFLAG(true);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
                alertSingOut.setNegativeButton("Não", null);
                alertSingOut.create().show();
            }
        });

        //--------------------------------------     ---------------------------------------------//
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        //--------------------------------------     ---------------------------------------------//


        politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PoliticActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        //--------------------------------------     ---------------------------------------------//

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TermsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        //--------------------------------------     ---------------------------------------------//

        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HelpCenterActivty.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        //--------------------------------------     ---------------------------------------------//

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        //--------------------------------------     ---------------------------------------------//
    }

    // Bottom Navigation view setup
    private void setupBottomNavigationViewEx() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ActivityNumber = 0
                        context.startActivity(intent1);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class); //ActivityNumber = 1
                        context.startActivity(intent2);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.ic_messages:
                        Intent intent3 = new Intent(context, MessagesActivity.class); //ActivityNumber = 2
                        context.startActivity(intent3);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.ic_profile:
                        if (activeP) {
                            break;
                        } else {
                            Intent intent4 = new Intent(context, AccountActivity.class); //ActivityNumber = 3
                            context.startActivity(intent4);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        }

                }

                return false;
            }
        });

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setProfileWidgets(User user) {
        if (miipsID.equals("")) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "setProfileWidgets: username ta assim:" + user.getUsername());
            if (user.getprofile_url() == null) {
                Picasso.get().load(R.drawable.user_profile).error(R.drawable.user_profile).into(profilePhoto);
            } else {
                Picasso.get().load(user.getprofile_url()).error(R.drawable.user_profile).into(profilePhoto);
            }
            cityWidgets.setText(user.getCity());
            miipsID.setText(user.getmiips_id());
            mProgressBar.setVisibility(View.GONE);
        }
    }


    private void getUserData() {
        //retrieve user information from database Firestore
        db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        settings = new User();

        Log.d(TAG, "onDataChange: user id ta assim: " + userID);
        DocumentReference docRef = db.collection(context.getString(R.string.dbname_user)).document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        settings = document.toObject(User.class);
                        setProfileWidgets(settings);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeP = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeP = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
