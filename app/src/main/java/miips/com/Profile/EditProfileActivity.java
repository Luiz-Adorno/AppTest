package miips.com.Profile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import miips.com.LoginActivity.LoginActivity;
import miips.com.Models.EditFiler;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Utils.FirebaseMethods;
import miips.com.Utils.StatesManipulation;
import miips.com.Utils.StringManipulation;
import miips.com.Utils.ZipCode.APIRetrofitService;
import miips.com.Utils.ZipCode.CEP;
import miips.com.Utils.ZipCode.CEPDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EditProfileActivity extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener {

    @Override
    public void getImagePath(Uri imagePah) {
        //assign to global variable
        mSelectUri = imagePah;
        Log.d(TAG, "getImagePath: URI = " + mSelectUri);
        uriString = imagePah.toString();
        Log.d(TAG, "getImagePath: URIS = " + uriString);
        uriFinal = StringManipulation.condenseUri(uriString);

    }


    private Uri mSelectUri = null;
    private String uriString = null;
    private String uriFinal;

    private static final String TAG = EditProfileActivity.class.getName();
    private static final int PERMISSION_REQUEST = 123;

    private boolean flagForPhoto = false; //if flag == true the profile photo already changed
    private ImageView mProfilePhoto, backArrow, editGender, saveEdit;
    private Context context;
    private EditText username;
    private MaskedEditText dateEditText;
    private FirebaseMethods firebaseMethods;
    private ProgressBar mProgressBar;
    private User mUserSettings;
    private TextView cityWidgets, stateWidgets, changeProfilePhoto;
    private Button btnLocation;
    private TextView gender;
    private static User settings;
    private TextView cityCep, stateCep, gotoZip;
    RelativeLayout cepLayout, layoutLocation;
    private EditText miipsID;
    private String urlPhoto;
    private ImageView infoMiips;
    EditFiler filter;

    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    //GooglePlayServices
    private String cityString, stateString;
    public String bestProvider;
    public Criteria criteria;

    //GooglePlayServices
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private boolean mPermissionsGranted = false;

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        context = EditProfileActivity.this;
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(context);
        filter = new EditFiler(context);
        getPermissions();
        initWidgets();
        filter.setFilter(miipsID, username);
        setupFirebaseAuth();
        btnBack();
        editGenderInit();
        initGps();
        initiZip();
        initInfoDialog();
        editProfilePhoto();
    }

    private void initiZip() {
        gotoZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cepShow();
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
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
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
                    mProgressBar.setVisibility(View.VISIBLE);

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

                                if (cep.getUf() != null) {
                                    stateString = StatesManipulation.stManipulation(cep.getUf());
                                }

                                Log.d(TAG, "onResponse: st1 é: " + stateString);


                                cityCep.setText(cep.getLocalidade());
                                stateCep.setText(stateString);
                                cityWidgets.setText(cityString);
                                stateWidgets.setText(stateString);

                                if (cityString == null || stateString == null) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "CEP inválido",
                                            Toast.LENGTH_LONG).show();
                                }

                                //Retorno no Log
                                Log.d(TAG, cep.toString());
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
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
                if (isGpsEnable()) {
                    if (mPermissionsGranted) {
                        mProgressBar.setVisibility(View.VISIBLE);
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

    private void alertDialogZip() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
        alertDialogP.setMessage("Não foi possivel pegar sua localização, que tal tentar pelo seu CEP?");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cepShow();
            }
        });
        alertDialogP.create().show();
    }

    public void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location ");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            List<String> providers = locationManager.getProviders(true);
            Location location = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l;
                }
            }

            Log.d(TAG, "getCurrentLocation: location is: "+ location);

            if (location != null) {
                Log.d(TAG, "onComplete: found location");

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Log.d(TAG, "location ta: "+ location.toString());
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    Log.d(TAG, "getCurrentLocation: latitude e long: "+ latitude + longitude);
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Log.d(TAG, "getCurrentLocation: adresses ="+ addresses);
                    cityString = addresses.get(0).getSubAdminArea();
                    stateString = addresses.get(0).getAdminArea();
                    Log.d(TAG, "onComplete: city and state ta assim: " + cityString + stateString);

                    mProgressBar.setVisibility(View.GONE);

                    if (cityString == null || stateString == null) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error ao tentar pegar pelo GPS, por favor tente pelo CEP",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(this, R.string.refresh_confirm, Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                }

            }  else {
                Log.d(TAG, "onComplete: aqui o currentLocation ta null");
                Toast.makeText(context, R.string.error_location, Toast.LENGTH_SHORT).show();
                alertDialogZip();
                mProgressBar.setVisibility(View.GONE);
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
            mProgressBar.setVisibility(View.GONE);
        }
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

    private boolean checkName(String name) {
        if (name == null || name.equals("") || name.length() < 3) {
            username.setError("Nome precisar ter 3 ou mais caracteres!");
            mProgressBar.setVisibility(View.GONE);
            return false;
        } else if (name.length() > 30) {
            username.setError("Nome precisa ter até 30 caracteres");
            mProgressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }


    private boolean checkInputs(String cityAux, String dateBirth, String miipsName) {
        if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(context, R.string.add_city_state, Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            return false;
        } else if (dateBirth.length() < 10) {
            dateEditText.setError("Data inválida");
            mProgressBar.setVisibility(View.GONE);
            return false;
        } else if (miipsName.equals("")) {
            miipsID.setError("Miipsname inválido");
            mProgressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
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


    //Retrieves the data contained in the widgets and submits it to the database
    private void saveProfileSettings() {
        mProgressBar.setVisibility(View.VISIBLE);
        //imm for hide the keyboard
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final String mUsername = username.getText().toString();
        final String mCity = cityWidgets.getText().toString();
        final String mState = stateWidgets.getText().toString();
        final String mBirth = dateEditText.getText().toString();
        final String mGender = gender.getText().toString();
        final String mMiipsId = miipsID.getText().toString();

        Log.d(TAG, "saveProfileSettings: mUsername ta assim: " + mUsername);

        //save new profile photo from gallery
        if (mSelectUri != null) {
            if (!flagForPhoto) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final String userID = mAuth.getCurrentUser().getUid();
                //get the URL image
                Log.d(TAG, "saveProfileSettings: urifinall: " + uriFinal);
                mStorageRef.child("users/" + userID + "/images/profilePhoto" + "/" + uriFinal).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        urlPhoto = uri.toString();
                        Log.d(TAG, "onSuccess: tst URL: " + urlPhoto);

                        //add the url profile photo tho firestore
                        Map<String, Object> updateProfileUrl = new HashMap<>();
                        updateProfileUrl.put(context.getString(R.string.field_url_profile), urlPhoto);
                        db.collection(context.getString(R.string.dbname_user)).document(userID).update(
                                updateProfileUrl
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pushImageFirebaseStorage();
                                    Log.d(TAG, "onComplete: url photo successfully updated");
                                    Toast.makeText(context, "Foto alterado com sucesso", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    flagForPhoto = true;
                                } else {
                                    Log.d(TAG, "onComplete: update failed");
                                    Toast.makeText(context, "Erro ao tentar mudar foto de perfil", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d(TAG, "onFailure: URL deu error, uriPhoto: " + urlPhoto);
                        Log.d(TAG, "onFailure: kk userID ta: " + userID + " e uriFinal: " + uriFinal);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
        }


        //case 1: the user did not change their username
        if (checkName(mUsername) && checkInputs(mCity, mBirth, mMiipsId)) {
            if (!mUserSettings.getUsername().equals(mUsername)) {
                firebaseMethods.updateUserSettings(mUsername, null, null, null, null, null);
                Toast.makeText(context, "Nome alterado com sucesso", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            if (!mUserSettings.getCity().equals(mCity) || !mUserSettings.getState().equals(mState)) {
                firebaseMethods.updateUserSettings(null, mCity, mState, null, null, null);
                Toast.makeText(context, "Localização alterado com sucesso", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
            if (!mUserSettings.getBirth().equals(mBirth)) {
                firebaseMethods.updateUserSettings(null, null, null, mBirth, null, null);
                Toast.makeText(context, "Nascimento alterado com sucesso", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            if (!mUserSettings.getGender().equals(mGender)) {
                firebaseMethods.updateUserSettings(null, null, null, null, mGender, null);
                Toast.makeText(context, "Gênero alterado com sucesso", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
            if (!mUserSettings.getmiips_id().equals(mMiipsId)) {

                checkFieldIsExist("miips_id", mMiipsId, new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            //miipsName valid
                            firebaseMethods.updateUserSettings(null, null, null, null, null, mMiipsId);
                            Toast.makeText(context, "MiipsID alterado com sucesso", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            miipsID.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            mUserSettings.setmiips_id(mMiipsId);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } else {
                            //miipsName exists, try another
                            miipsID.setError("MiipsID existente");
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    private void editProfilePhoto() {
        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagForPhoto = false;
                if (!onlyVerifyPermission()) {
                    alertEditPhoto();
                } else {
                    Log.d(TAG, "onClick: Opening  dialog to choose new photo");
                    SelectPhotoDialog dialog = new SelectPhotoDialog();
                    dialog.show(getSupportFragmentManager(), getString(R.string.dialog_select_photo));
                }
            }
        });
    }

    private boolean onlyVerifyPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;
        }
    }

    private void verifyPermissions() {
        Log.d(TAG, "verifyPermissions: asking for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(EditProfileActivity.this, permissions, PERMISSION_REQUEST);

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onClick: test after accept permissions");
            SelectPhotoDialog dialog = new SelectPhotoDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.dialog_select_photo));

        }
    }

    private void alertEditPhoto() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
        alertDialogP.setMessage("É necessário permitir que o Miips acesse fotos, mídia, arquivos do seu dispositivo");
        alertDialogP.setCancelable(false);
        alertDialogP.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verifyPermissions();
            }
        });
        alertDialogP.create().show();
    }

    private void initWidgets() {
        mProfilePhoto = findViewById(R.id.ic_profile);
        username = findViewById(R.id.username);
        mProgressBar = findViewById(R.id.loadingLoginProgressBar);
        backArrow = findViewById(R.id.back_arrow);
        cityWidgets = findViewById(R.id.city);
        stateWidgets = findViewById(R.id.state);
        btnLocation = findViewById(R.id.button_location);
        dateEditText = findViewById(R.id.date);
        gender = findViewById(R.id.genderText);
        editGender = findViewById(R.id.edit_gender);
        cepLayout = findViewById(R.id.layout_cep);
        cepLayout.setVisibility(View.GONE);
        miipsID = findViewById(R.id.miips_id);
        infoMiips = findViewById(R.id.info_miipsname);
        gotoZip = findViewById(R.id.goto_cep);
        layoutLocation = findViewById(R.id.layout_location);
        saveEdit = findViewById(R.id.save_settings);
        changeProfilePhoto = findViewById(R.id.edit_photo_profile);
    }

    private void editGenderInit() {
        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder adb = new AlertDialog.Builder(context);
                final CharSequence items[] = new CharSequence[]{"Feminino", "Masculino"};
                adb.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int n) {
                        Log.e("value is", "gender: " + n);
                        gender.setText(items[n]);
                        d.dismiss();
                    }
                });
                adb.setTitle("Escolha seu gênero:");
                adb.show();

            }
        });
    }

    private void setProfileWidgets(User user) {

        mUserSettings = user;

        if (username.equals("")) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            if (uriString == null) {
                if(user.getprofile_url() == null) {
                    Picasso.get().load(R.drawable.user_profile).error(R.drawable.user_profile).into(mProfilePhoto);
                }else{
                    Picasso.get().load(user.getprofile_url()).error(R.drawable.user_profile).into(mProfilePhoto);
                }
            } else {
                Picasso.get().load(uriString).error(R.drawable.user_profile).into(mProfilePhoto);
            }
            username.setText(user.getUsername());
            mProgressBar.setVisibility(View.GONE);
            cityWidgets.setText(user.getCity());
            stateWidgets.setText(user.getState());
            dateEditText.setText(user.getBirth());
            gender.setText(user.getGender());
            miipsID.setText(user.getmiips_id());
        }
    }

    private void btnBack() {

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
            }
        });

        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });

    }

    private void setupFirebaseAuth() {
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in

                } else {
                    //User is signed out
                    Intent intentSingOut = new Intent(context, LoginActivity.class);
                    startActivity(intentSingOut);
                    intentSingOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
            }
        };

    }

    private void pushImageFirebaseStorage() {
        mProgressBar.setVisibility(View.VISIBLE);
        String userID = mAuth.getCurrentUser().getUid();
        StorageReference storageReference = mStorageRef.child("users/" + userID + "/images/profilePhoto" + "/" + uriFinal);
        storageReference.putFile(mSelectUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressBar.setVisibility(View.GONE);

                Log.d(TAG, "onSuccess: mSelectUri ok");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: mSelectUri");
                Toast.makeText(context, "Error ao tentar atualizar foto de perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {
        //retrieve user information from database Firestore

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        getUserData();
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