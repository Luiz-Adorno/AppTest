package miips.com.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import miips.com.LoginActivity.LoginActivity;
import miips.com.Messages.MessagesActivity;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Register.google.RegisterActivityGoogle;
import miips.com.Register.normal.LocationCepFragment;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.FirebaseMethods;
import miips.com.Utils.MyPreference;
import miips.com.Utils.SectionPagerAdapter;
import miips.com.Utils.StatesManipulation;

import static androidx.constraintlayout.widget.Constraints.TAG;

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

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DOCID = "";

    //GooglePlayServices
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private boolean mPermissionsGranted = false;
    private String cityString, stateString;
    public String bestProvider;
    public Criteria criteria;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

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

        if (cd.isConnected()) {
            checkIfUserHasDataInDb();
        }else {
            buildDialog(context).show();
        }

    }

    private void checkIfUserHasDataInDb(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //if user == null, get the location
            initGPS();
        } else {
            //user is registered
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
                            //all god

                            setupBottomNavigationViewEx();

                            setupViewPager();

                        } else {
                            //go to registerActivityGoogle finish the registration
                            Log.d(TAG, "checkCurrentUser: user is null");
                            Intent intent = new Intent(context, RegisterActivityGoogle.class);
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
    }

    private void initGPS(){
        if (isGpsEnable()) {
            getPermissions();
            if (mPermissionsGranted) {
                Log.d(TAG, "onClick: pegou os nomes no 1 click");
                getCurrentLocation();
                Log.d(TAG, "initGPS: axx: "+ cityString + " e " + stateString);
                String docID = cityString + "-" + stateString;

                MyPreference myPreference = new MyPreference(this);
                myPreference.setTOKEN(docID);

                setupBottomNavigationViewEx();

                setupViewPager();
            } else {
                alertDialogPermissions();
            }
        }
    }

    public void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location ");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            List<String> providers = locationManager.getProviders(true);
            Location location = null;
            for (String provider : providers) {
                location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (location == null || location.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    mProgessBar.setVisibility(View.GONE);
                }
            }

            if (location != null) {
                Log.d(TAG, "onComplete: found location");

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    Log.d(TAG, "getCurrentLocation: latitude e long: "+ latitude + longitude);
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Log.d(TAG, "getCurrentLocation: adresses ="+ addresses);
                    cityString = addresses.get(0).getSubAdminArea();
                    stateString = StatesManipulation.stManipulation(addresses.get(0).getAdminArea());
                    mProgessBar.setVisibility(View.GONE);
                    Log.d(TAG, "onComplete: city and state ta assim: " + cityString + stateString);
                    if (cityString.equals("") || stateString.equals("")) {
                        alertDialogZip();
                        mProgessBar.setVisibility(View.GONE);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    mProgessBar.setVisibility(View.GONE);
                }

            } else {
                Log.d(TAG, "onComplete: aqui o currentLocation ta null");
                alertDialogZip();
                mProgessBar.setVisibility(View.GONE);
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
            mProgessBar.setVisibility(View.GONE);
        }
    }

    private void alertDialogZip() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
        alertDialogP.setMessage("Ops, não foi possivel pegar sua localização, por favor faça login ou seu cadastro");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgessBar.setVisibility(View.GONE);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        alertDialogP.create().show();
    }


    // Responsible for adding the 2 tabs Product, Service
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VestFragment()); //index = 0
        adapter.addFragment(new ShoesFragment()); //index = 1
        adapter.addFragment(new AcessoFragment()); //index = 2
        adapter.addFragment(new JewFragment()); //index = 1
        ViewPager viewPager = findViewById(R.id.containerViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsFragment);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.clothes);
        tabLayout.getTabAt(1).setIcon(R.drawable.shoes);
        tabLayout.getTabAt(2).setIcon(R.drawable.product);
        tabLayout.getTabAt(3).setIcon(R.drawable.diamond);
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

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Sem conexão com internet");
        builder.setMessage("É necessária uma conexão de internet para prosseguir!");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
            }
        });

        return builder;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: called");

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                        Log.d(TAG, "onRequestPermissionsResult: permission granted");
                        mPermissionsGranted = true;
                    }
                }
            }
        }
    }

    private void getPermissions() {
        Log.d(TAG, "getPermissions: getting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mPermissionsGranted = true;
                Log.d(TAG, "getPermissions: mPermissionsGranted is true");
            } else {
                ActivityCompat.requestPermissions((Activity) context, permissions, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void alertDialogPermissions() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
        alertDialogP.setMessage("É necessário permitir que o Miips acesse o local do dispositivo!");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermissions();
            }
        });
        alertDialogP.create().show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.alert_message_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //verify if gps is enable
    private boolean isGpsEnable() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
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
