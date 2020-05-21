package miips.com.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import miips.com.Messages.MessagesActivity;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.FirebaseMethods;
import miips.com.Utils.MyPreference;
import miips.com.Utils.SectionPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;
    private Context context = HomeActivity.this;
    private ProgressBar mProgessBar;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseMethods firebaseMethods;
    GoogleSignInClient mGoogleSignInClient;
    private BottomNavigationViewEx bottomNavigationViewEx;

    ConnectionDetector cd;

    public static boolean activeH = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(context);
        cd = new ConnectionDetector(context);
        mProgessBar = findViewById(R.id.progressBar_cyclic);
        mProgessBar.setVisibility(View.GONE);

        bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        bottomNavigationViewEx.setVisibility(View.GONE);

        setupFirebaseAuth();

        if (mAuth.getCurrentUser() != null) {
            setupViewPagerWithUser();
        } else {
            setupViewPagerNoUser();
        }

        setupBottomNavigationViewEx();

    }

    // Responsible for adding the tabs with liked tab
    private void setupViewPagerWithUser() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LikedFragment()); //index = 0
        adapter.addFragment(new VestFragment()); //index = 1
        adapter.addFragment(new ShoesFragment()); //index = 2
        adapter.addFragment(new AccessoFragment()); //index = 3
        adapter.addFragment(new SunGlassFragment()); //index = 4
        adapter.addFragment(new GlassFragment()); //index = 5
        adapter.addFragment(new BijuFragment()); //index = 6
        adapter.addFragment(new SemiFragment()); //index = 7
        adapter.addFragment(new JewFragment()); //index = 8
        ViewPager viewPager = findViewById(R.id.containerViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsFragment);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.tabheart);
        tabLayout.getTabAt(1).setIcon(R.drawable.clothes);
        tabLayout.getTabAt(2).setIcon(R.drawable.shoes);
        tabLayout.getTabAt(3).setIcon(R.drawable.product);
        tabLayout.getTabAt(4).setIcon(R.drawable.sunglass);
        tabLayout.getTabAt(5).setIcon(R.drawable.glass);
        tabLayout.getTabAt(6).setIcon(R.drawable.biju);
        tabLayout.getTabAt(7).setIcon(R.drawable.semi);
        tabLayout.getTabAt(8).setIcon(R.drawable.diamond);
    }




    // Responsible for adding the tabs without liked tab
    private void setupViewPagerNoUser() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VestFragment()); //index = 0
        adapter.addFragment(new ShoesFragment()); //index = 1
        adapter.addFragment(new AccessoFragment()); //index = 2
        adapter.addFragment(new SunGlassFragment()); //index = 3
        adapter.addFragment(new GlassFragment()); //index = 4
        adapter.addFragment(new BijuFragment()); //index = 5
        adapter.addFragment(new SemiFragment()); //index = 6
        adapter.addFragment(new JewFragment()); //index = 7
        ViewPager viewPager = findViewById(R.id.containerViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsFragment);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.clothes);
        tabLayout.getTabAt(1).setIcon(R.drawable.shoes);
        tabLayout.getTabAt(2).setIcon(R.drawable.product);
        tabLayout.getTabAt(3).setIcon(R.drawable.sunglass);
        tabLayout.getTabAt(4).setIcon(R.drawable.glass);
        tabLayout.getTabAt(5).setIcon(R.drawable.biju);
        tabLayout.getTabAt(6).setIcon(R.drawable.semi);
        tabLayout.getTabAt(7).setIcon(R.drawable.diamond);
    }

    // Bottom Navigation view setup
    private void setupBottomNavigationViewEx() {
        bottomNavigationViewEx.setVisibility(View.VISIBLE);
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
        MyPreference myPreference = new MyPreference(context);
        Boolean flag = myPreference.getFlag();
        Log.d(TAG, "onStart: infinyty: "+ flag);
        if(flag){
            myPreference.setFLAG(false);
            finish();
            startActivity(getIntent());
        }
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
}
