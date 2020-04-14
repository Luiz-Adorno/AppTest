package miips.com.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import miips.com.LoginActivity.LoginActivity;
import miips.com.Messages.MessagesActivity;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.FirebaseMethods;
import miips.com.Utils.SectionPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;
    private Context context = HomeActivity.this;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseMethods firebaseMethods;
    GoogleSignInClient mGoogleSignInClient;

    public static boolean activeH = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(context);

        setupFirebaseAuth();

        setupBottomNavigationViewEx();

        setupViewPager();

    }


    // Responsible for adding the 2 tabs Product, Service
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProductFragment()); //index = 0
        adapter.addFragment(new ServiceFragment()); //index = 1
        ViewPager viewPager = findViewById(R.id.containerViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsFragment);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.product);
        tabLayout.getTabAt(1).setIcon(R.drawable.service2);
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
                        if (activeH) {
                            break;
                        } else {
                            break;
                        }

                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class); //ActivityNumber = 1
                        context.startActivity(intent2);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.ic_messages:
                        Intent intent3 = new Intent(context, MessagesActivity.class); //ActivityNumber = 2
                        context.startActivity(intent3);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.ic_profile:
                        Intent intent4 = new Intent(context, AccountActivity.class); //ActivityNumber = 3
                        context.startActivity(intent4);
                        overridePendingTransition(0, 0);
                        break;
                }

                return false;
            }
        });


        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void checkCurrentUser(FirebaseUser user) {

        if (user == null) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onComplete: userID ta assim: " + userID);
        DocumentReference docRef = db.collection(context.getString(R.string.dbname_user)).document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //user is successfully logged in
                    } else {
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();
                        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        activeH = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
    protected void onResume() {
        super.onResume();

    }
}
