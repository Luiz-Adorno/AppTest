package miips.com.Register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;


import miips.com.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public String Email, Password, Name, MiipsID, Birth, Gender, City, State;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.frame_layout, new NameFragment());
        ft.commit();

    }

    public void getFromEmail(String email, String password){
        Log.d(TAG, "getCurrentLocation: email ta assim: " + email);
        Email = email;
        Password = password;
    }

    public void getFromName(String name, String miipsID){
        Log.d(TAG, "getCurrentLocation: name ta assim: " + name);
        Name = name;
        MiipsID = miipsID;
    }

    public void getFromBirth(String birth, String gender){
        Log.d(TAG, "getCurrentLocation: BIRTH E GENDER ta assim: " + birth +" "+ gender );
        Birth = birth;
        Gender = gender;
    }

    public void getFromLocation(String city, String state){
        Log.d(TAG, "getCurrentLocation: BIRTH E GENDER ta assim: " + city +" "+ state );
        City = city;
        State = state;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: EMAIL HAS BEEN FINISHED");
        super.onDestroy();
    }
}

