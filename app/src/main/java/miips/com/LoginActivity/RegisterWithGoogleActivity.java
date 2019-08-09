package miips.com.LoginActivity;

import android.Manifest;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import miips.com.Home.HomeActivity;
import miips.com.Models.TestFilter;
import miips.com.R;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.FirebaseMethods;
import miips.com.Utils.PhoneEditText;
import miips.com.Utils.ZipCode.APIRetrofitService;
import miips.com.Utils.ZipCode.CEP;
import miips.com.Utils.ZipCode.CEPDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterWithGoogleActivity extends AppCompatActivity {

    private String username;
    private String phone;
    private String dateBirth;

    private static final String TAG = "RegisterWithGoogleActiv";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private ImageView infoMiips;
    public String miipsName;
    private PhoneEditText mPhone;
    private TextView cityWidgets, stateWidgets;
    private EditText mUsername;
    private Button btnRegister, btnLocation;
    RelativeLayout cepLayout, layoutLocation;
    private ProgressBar mProgessBar;
    ConnectionDetector cd;
    GoogleSignInClient mGoogleSignInClient;
    ImageView mImage;
    FirebaseMethods firebaseMethods;
    private MaskedEditText dateEditText;
    private RadioGroup radioGender;
    private TextView cityCep, stateCep, gotoZip;
    private RadioButton selectedRadioButton;
    private EditText namiips;


    //GooglePlayServices
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private boolean mPermissionsGranted = false;
    public static String cityString, stateString;
    public String bestProvider;
    public Criteria criteria;

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;


    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_google);
        mAuth = FirebaseAuth.getInstance();
        cd = new ConnectionDetector(this);
        mContext = RegisterWithGoogleActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        getPermissions();

        //call methods
//        initWidgets();
        TestFilter filter = new TestFilter(mContext);
        filter.setFilter(namiips, mUsername);
        arrowReturn();
        setupFirebaseAuth();
        initBtn();
        initGps();
        initGender();
        initInfoDialog();
        initiZip();
    }

    private void initiZip(){
        gotoZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cepShow();
            }
        });
    }

    private void alertDialogZip() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(mContext);
        alertDialogP.setMessage("Não foi possivel pegar sua localização, que tal tentar pelo seu CEP?");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogP.create().show();
    }

    private void initGender() {
        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioGender.getCheckedRadioButtonId() == -1) {
                    //do nothing
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioGender.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectedRadioButton = findViewById(selectedId);
                }
            }
        });
    }

    private void initInfoDialog() {
        infoMiips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfoMiips();
            }
        });
    }

    private void dialogInfoMiips() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(mContext);
        alertDialogP.setMessage(R.string.miips_id);
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogP.create().show();
    }

    //if the getCurrentLocation get error, the option of get location with cep is shown
    private void cepShow() {
        final MaskedEditText cep = findViewById(R.id.cep);
        Button btnCep = findViewById(R.id.btn_cep);
        cityCep = findViewById(R.id.cityp);
        stateCep = findViewById(R.id.statep);
        layoutLocation.setVisibility(View.GONE);
        cepLayout.setVisibility(View.VISIBLE);

        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

        btnCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cep.getText().toString().isEmpty()) {
                    mProgessBar.setVisibility(View.VISIBLE);

                    Call<CEP> callCEPByCEP = service.getEnderecoByCEP(cep.getText().toString());

                    callCEPByCEP.enqueue(new Callback<CEP>() {
                        @Override
                        public void onResponse(Call<CEP> call, Response<CEP> response) {
                            if (!response.isSuccessful()) {
                                cep.setError(getString(R.string.cep_error));

                            } else {
                                CEP cep = response.body();

                                cityString = cep.getLocalidade();
                                stateString = cep.getUf();

                                cityCep.setText(cep.getLocalidade());
                                stateCep.setText(cep.getUf());

                                if(cityString == null || stateString == null){
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "CEP inválido",
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                            mProgessBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            cep.setError(getString(R.string.cep_error));
                        }
                    });

                }

            }
        });
    }

    private void initGps() {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mProgessBar.setVisibility(View.VISIBLE);
                if (isGpsEnable()) {
                    if (mPermissionsGranted) {
                        mProgessBar.setVisibility(View.GONE);
                        Log.d(TAG, "onClick: pegou os nomes no 1 click");
                        getCurrentLocation();
                        cityWidgets.setText(cityString);
                        stateWidgets.setText(stateString);
                        Log.d(TAG, "onClick: o mPermission ta true aqui");
                    } else {
                        alertDialogPermissions();
                    }
                }
            }
        });
    }

    public void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location ");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            Location location = locationManager.getLastKnownLocation(bestProvider);
            Log.d(TAG, "getCurrentLocation: location ta assim :" + location);

            if (location != null) {
                Log.d(TAG, "onComplete: found location");

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(mContext, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    cityString = addresses.get(0).getLocality();
                    stateString = addresses.get(0).getAdminArea();
                    Log.d(TAG, "onComplete: city and state ta assim: " + cityString + stateString);

                    if(cityString.equals("") || stateString.equals("")){
                        cepShow();
                        alertDialogZip();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "onComplete: aqui o currentLocation ta null");
                alertDialogZip();
                cepShow();
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }

    private void alertDialogPermissions() {
        AlertDialog.Builder alertSingOut = new AlertDialog.Builder(mContext);
        alertSingOut.setMessage("É necessário permitir que o Miips acesse o local do dispositivo!");
        alertSingOut.setCancelable(true);
        alertSingOut.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermissions();
            }
        });
        alertSingOut.create().show();
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

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mPermissionsGranted = true;
                Log.d(TAG, "getPermissions: mPermissionsGranted is true");
            } else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }


    //verify if gps is enable
    public boolean isGpsEnable() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


    private void initBtn() {
        final String email = mAuth.getCurrentUser().getEmail();
        final String userID = mAuth.getCurrentUser().getUid();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = mUsername.getText().toString();
                phone = (mPhone.getText().toString());
                dateBirth = dateEditText.getText().toString();
                miipsName = namiips.getText().toString();

                if (radioGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(mContext, R.string.gende_select, Toast.LENGTH_SHORT).show();
                } else {

                    if (checkInputs(username, cityString, dateBirth, miipsName)) {
                        mProgessBar.setVisibility(View.VISIBLE);

                        checkFieldIsExist("miips_id", miipsName, new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {

                                if(aBoolean){
                                    //miipsName valid

                                    //Identify internet connection
                                    if (cd.isConnected()) {
                                        //add new user to the firestore
//                                        firebaseMethods.addNewUserFirestore(userID, email, username, phone, cityString, stateString,
//                                                selectedRadioButton.getText().toString(), dateBirth, miipsName);
                                        Intent intent = new Intent(mContext, HomeActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    } else {
                                        buildDialog(RegisterWithGoogleActivity.this).show();
                                        mProgessBar.setVisibility(View.GONE);
                                    }
                                }else{
                                    //miipsName exists, try another
                                    namiips.setError("Miipsname existente");
                                    mProgessBar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                }
            }
        });
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

    private boolean checkInputs(String username, String cityAux, String dateBirth, String miipsName) {
        if (username == null || username.equals("") || username.length() < 3) {
            mUsername.setError("Nome precisar ter 3 ou mais caracteres!");
            return false;
        } else if (username.length() > 30) {
            mUsername.setError("Nome precisa ter até 30 caracteres");
            return false;
        } else if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(mContext, R.string.add_city_state, Toast.LENGTH_SHORT).show();
            return false;
        } else if (dateBirth.length() < 9) {
            Toast.makeText(mContext, "Data inválida", Toast.LENGTH_SHORT).show();
            return false;
        } else if (miipsName.equals("")) {
            namiips.setError("Crie seu Miipsname");
            return false;
        }
        return true;
    }

    /**
     * Initialize the activity widgets
     */
    private void initWidgets() {
        cityWidgets = findViewById(R.id.city);
        mUsername = findViewById(R.id.name_register);
        mPhone = findViewById(R.id.phone_number);
        mProgessBar.setVisibility(View.GONE);
        btnRegister = findViewById(R.id.button_register);
        btnLocation = findViewById(R.id.button_location);
        mImage = findViewById(R.id.backArrowRegister);
        stateWidgets = findViewById(R.id.state);
        dateEditText = findViewById(R.id.date);
        radioGender = findViewById(R.id.gender_group);
        infoMiips = findViewById(R.id.info_miipsname);
        namiips = findViewById(R.id.miips_id);
        cepLayout = findViewById(R.id.layout_cep);
        cepLayout.setVisibility(View.GONE);
        gotoZip = findViewById(R.id.goto_cep);
        layoutLocation = findViewById(R.id.layout_location);
    }

    public void checkFieldIsExist(String key, String value, final OnSuccessListener<Boolean> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo(key, value).addSnapshotListener(new EventListener<QuerySnapshot>() {
            private boolean isRunOneTime = false;
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!isRunOneTime) {
                    isRunOneTime = true;
                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                    if (e != null) {
                        e.printStackTrace();
                        String message = e.getMessage();
                        onSuccessListener.onSuccess(false);
                        return;
                    }

                    if (snapshotList.size() > 0) {
                        //Field is Exist
                        onSuccessListener.onSuccess(false);
                    } else {
                        onSuccessListener.onSuccess(true);
                    }

                }
            }
        });
    }

    private void arrowReturn() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

                AlertDialog.Builder alertSingOut = new AlertDialog.Builder(mContext);
                alertSingOut.setMessage(R.string.exit_gmail);
                alertSingOut.setCancelable(true);
                alertSingOut.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                });
                alertSingOut.setNegativeButton(R.string.no, null);
                alertSingOut.create().show();
            }
        });
    }

    private void setupFirebaseAuth() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart: mAtuhListener ta assim:" + mAuthListener);
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
    public void onBackPressed() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

        AlertDialog.Builder alertSingOut = new AlertDialog.Builder(mContext);
        alertSingOut.setMessage(R.string.exit_gmail);
        alertSingOut.setCancelable(true);
        alertSingOut.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
        alertSingOut.setNegativeButton(R.string.no, null);
        alertSingOut.create().show();
    }
}

