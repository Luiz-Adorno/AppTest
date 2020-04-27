package miips.com.Init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Register.normal.LocationCepFragment;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.MyPreference;
import miips.com.Utils.StatesManipulation;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LocationVisitorActivity extends AppCompatActivity {

    private TextView gotoCep;
    private Button next, cancel;
    private Button btnLocation;
    private ProgressBar mProgessBar;
    private TextView cityWidgets;
    private TextView stateWidgets;
    private RelativeLayout lay1, lay2;
    private Context context;

    ConnectionDetector cd;

    //GooglePlayServices
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private boolean mPermissionsGranted = false;
    private String cityString, stateString;
    public String bestProvider;
    public Criteria criteria;


    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        context = LocationVisitorActivity.this;
        next = findViewById(R.id.button_register);
        next.setVisibility(View.GONE);
        cancel = findViewById(R.id.cancel);
        btnLocation = findViewById(R.id.button_location);
        mProgessBar = findViewById(R.id.progressBar_cyclic);
        mProgessBar.setVisibility(View.GONE);
        cityWidgets = findViewById(R.id.city);
        stateWidgets = findViewById(R.id.state);
        cd = new ConnectionDetector(context);
        lay1 = findViewById(R.id.layout1);
        lay2 = findViewById(R.id.layout2);
        lay1.setVisibility(View.GONE);
        lay2.setVisibility(View.GONE);
        MyPreference myPreference = new MyPreference(context);
        String doc_id = myPreference.getToken();
        if(doc_id.isEmpty()){
            getPermissions();
            initGps();
            initCancel();
            init();
        }else {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgessBar.setVisibility(View.VISIBLE);
                if (checkInputs(cityString, stateString)) {

                    if (cd.isConnected()) {

                        Log.d(TAG, "initGPS: axx: "+ cityString + " e " + stateString);
                        String docID = cityString + "-" + stateString;

                        MyPreference myPreference = new MyPreference(context);
                        myPreference.setTOKEN(docID);

                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        mProgessBar.setVisibility(View.GONE);
                        buildDialog(context).show();
                    }

                }
            }
        });
    }

    private boolean checkInputs(String cityAux, String stateString) {
        if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(context, R.string.add_city_state, Toast.LENGTH_SHORT).show();
            return false;
        }else if (stateString == null || stateString.equals("")){
            Toast.makeText(context, R.string.add_city_state, Toast.LENGTH_SHORT).show();
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
        mProgessBar.setVisibility(View.GONE);
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

    private void alertDialogZip() {
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(context);
        alertDialogP.setMessage("Não foi possivel pegar sua localização, faça o login para prosseguir!");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialogP.create().show();
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

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    private void initGps() {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGpsEnable()) {
                    if (mPermissionsGranted) {
                        Log.d(TAG, "onClick: pegou os nomes no 1 click");
                        getCurrentLocation();

                        lay1.setVisibility(View.VISIBLE);
                        lay2.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);

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
}
