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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String phone;
    private PhoneEditText phoneEditText;
    private String email, password, username, dateBirth, miipsName;
    private TextView cityWidgets;
    private TextView stateWidgets;
    private EditText mEmail, mPassword, mUsername;
    private Button btnRegister, btnLocation;
    private ProgressBar mProgessBar;
    ConnectionDetector cd;
    FirebaseMethods firebaseMethods;
    private RadioGroup radioGender;
    private RadioButton selectedRadioButton;
    private MaskedEditText dateEditText;
    RelativeLayout cepLayout, layoutLocation;
    private ImageView infoMiips;
    TestFilter filter;
    private TextView cityCep, stateCep, gotoZip;
    private EditText miipsID;

    //GooglePlayServices
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private boolean mPermissionsGranted = false;
    private String cityString, stateString;
    public String bestProvider;
    public Criteria criteria;


    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;


    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_temporario);
        mAuth = FirebaseAuth.getInstance();
        cd = new ConnectionDetector(this);
        mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        getPermissions();

        //call methods
        arrowReturn();
        initWidgets();
        filter = new TestFilter(mContext);
        setupFirebaseAuth();
        initGps();
        initBtn();
        initGender();
        initInfoDialog();
        initiZip();
    }

    private void initiZip() {
        gotoZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cepShow();
            }
        });
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
                    selectedRadioButton.getText();
                }
            }
        });
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
                                Toast.makeText(
                                        getApplicationContext(), R.string.cep_error,
                                        Toast.LENGTH_LONG).show();
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

                                //Retorno no Log
                                Log.d(TAG, cep.toString());
                            }
                            mProgessBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error ao pegar o cep",
                                    Toast.LENGTH_LONG).show();
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

    private void alertDialogPermissions() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(mContext);
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

    //verify if gps is enable
    private boolean isGpsEnable() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
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

    public void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location ");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            Location location = locationManager.getLastKnownLocation(bestProvider);
            Log.d(TAG, "getCurrentLocation: location ta assim: " + location);

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
                    if (cityString.equals("") || stateString.equals("")) {
                        cepShow();
                        alertDialogZip();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "onComplete: aqui o currentLocation ta null");
                cepShow();
                alertDialogZip();
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                phone = (phoneEditText.getText().toString());
                dateBirth = dateEditText.getText().toString();
                miipsName = miipsID.getText().toString();

                if (radioGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(mContext, R.string.gende_select, Toast.LENGTH_SHORT).show();

                } else {

                    if (checkInputs(email, password, username, cityString, dateBirth, miipsName)) {
                        mProgessBar.setVisibility(View.VISIBLE);


                        checkFieldIsExist("miips_id", miipsName, new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if (aBoolean) {
                                    //miipsName valid

                                    //Identify internet connection
                                    if (cd.isConnected()) {
                                        registerNewEmail(email, password);

                                    } else {
                                        buildDialog(RegisterActivity.this).show();
                                        mProgessBar.setVisibility(View.GONE);
                                    }

                                    mPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                } else {
                                    //miipsName exists, try another
                                    miipsID.setError("Miipsname existente");
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

    private boolean checkInputs(String email, String password, String username, String cityAux, String dateBirth, String miipsName) {
        if (username == null || username.equals("") || username.length() < 3) {
            mUsername.setError("Nome precisa ter 3 ou mais caracteres!");
            return false;
        } else if (username.length() > 30) {
            mUsername.setError("Nome precisa ter até 30 caracteres");
            return false;
        } else if (email == null || email.equals("")) {
            mEmail.setError("Email inválido");
            return false;
        } else if (password.equals("") || password.length() < 6) {
            mPassword.setError("Senha precisa ter 6 ou mais caracteres!");
            return false;
        } else if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(mContext, R.string.add_city_state, Toast.LENGTH_SHORT).show();
            return false;
        } else if (dateBirth.length() < 9) {
            dateEditText.setError("Data inválida");
            return false;
        } else if (miipsName.equals("")) {
            miipsID.setError("Miipsname inválido");
            return false;
        }
        return true;
    }

    /**
     * Initialize the activity widgets
     */
    private void initWidgets() {
        mEmail = findViewById(R.id.email_register);
        mUsername = findViewById(R.id.name_register);
        mPassword = findViewById(R.id.password_register);
        mProgessBar = findViewById(R.id.RegisterProgressBar);
        mProgessBar.setVisibility(View.GONE);
        btnRegister = findViewById(R.id.button_register);
        phoneEditText = findViewById(R.id.phone_number);
        cityWidgets = findViewById(R.id.city);
        stateWidgets = findViewById(R.id.state);
        btnLocation = findViewById(R.id.button_location);
        radioGender = findViewById(R.id.gender_group);
        dateEditText = findViewById(R.id.date);
        miipsID = findViewById(R.id.miips_id);
        infoMiips = findViewById(R.id.info_miipsname);
        cepLayout = findViewById(R.id.layout_cep);
        cepLayout.setVisibility(View.GONE);
        gotoZip = findViewById(R.id.goto_cep);
        layoutLocation = findViewById(R.id.layout_location);

    }

    private void arrowReturn() {
        ImageView mImage = findViewById(R.id.backArrowRegister);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
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

    //add user data in firestore
    private void setupFirebaseAuth() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged: flag69:" + user);
                if (user != null) {
                    //User is signed in
                    String userID = user.getUid();
                    Log.d(TAG, "onDataChange: user id ta assim: " + userID);
                    //add new user to the firestore
//                    firebaseMethods.addNewUserFirestore(userID, email, username, phone, cityString, stateString,
//                            selectedRadioButton.getText().toString(), dateBirth, miipsName);

                }

            }
        };
    }


    public void registerNewEmail(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            if (mAuth.getCurrentUser() != null) {
                                Toast.makeText(mContext, "Conta criada com sucesso.", Toast.LENGTH_SHORT).show();
                                mProgessBar.setVisibility(View.GONE);
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mEmail.setError("Email inválido ou existente!");
                            mProgessBar.setVisibility(View.GONE);

                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onRestart() {
        isGpsEnable();
        super.onRestart();
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
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}

