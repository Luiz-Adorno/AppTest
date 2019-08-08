package miips.com.Register;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Utils.ConnectionDetector;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LocationFragment extends Fragment {
    private TextView gotoCep;
    private Button next, cancel;
    private Button btnLocation;
    private ProgressBar mProgessBar;
    private TextView cityWidgets;
    private TextView stateWidgets;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_location, container, false);
        gotoCep = view.findViewById(R.id.goto_cep);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        btnLocation = view.findViewById(R.id.button_location);
        mProgessBar = view.findViewById(R.id.registerProgressBar);
        mProgessBar.setVisibility(View.GONE);
        cityWidgets = view.findViewById(R.id.city);
        stateWidgets = view.findViewById(R.id.state);
        cd = new ConnectionDetector(getActivity());

//        if(cd.isConnected()){
//
//        }else{
//            buildDialog(getActivity()).show();
//        }

        initGps();
        initCancel();
        setGotoCep();
        return view;
    }

    private void setGotoCep() {
        gotoCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.frame_layout, new LocationCepFragment());
                ft.commit();
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

    private void getPermissions() {
        Log.d(TAG, "getPermissions: getting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mPermissionsGranted = true;
                Log.d(TAG, "getPermissions: mPermissionsGranted is true");
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void alertDialogPermissions() {
        mProgessBar.setVisibility(View.GONE);
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(getActivity());
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
        AlertDialog.Builder alertDialogP = new AlertDialog.Builder(getActivity());
        alertDialogP.setMessage("Não foi possivel pegar sua localização, que tal tentar pelo seu CEP?");
        alertDialogP.setCancelable(true);
        alertDialogP.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogP.create().show();
    }

    public void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location ");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    cityString = addresses.get(0).getLocality();
                    stateString = addresses.get(0).getAdminArea();
                    Log.d(TAG, "onComplete: city and state ta assim: " + cityString + stateString);
                    if (cityString.equals("") || stateString.equals("")) {
                        alertDialogZip();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "onComplete: aqui o currentLocation ta null");
                alertDialogZip();
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
}