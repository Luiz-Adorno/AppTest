package miips.com.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import miips.com.LoginActivity.LoginActivity;
import miips.com.Models.User;
import miips.com.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getName();
    private ImageView mProfilePhoto, back;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView username, email, editProfile, dateBirth, gender, miipsID;
    private ProgressBar mProgressBar;
    private TextView cityWidgets, stateWidgets;
    private static User settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = ProfileActivity.this;
        mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();
        initWidgets();
        btnBack();
    }


    private void initWidgets() {
        miipsID = findViewById(R.id.miips_id);
        mProfilePhoto = findViewById(R.id.ic_profile);
        back = findViewById(R.id.backArrowRegister);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        mProgressBar = findViewById(R.id.progressBar_cyclic);
        editProfile = findViewById(R.id.edit_profile);
        cityWidgets = findViewById(R.id.city);
        stateWidgets = findViewById(R.id.state);
        dateBirth = findViewById(R.id.dateBirth);
        gender = findViewById(R.id.gender);

    }

    private void btnEditProfile(){
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
            }
        });
    }

    private void setProfileWidgets(User user){

        if(username.equals("")){
            mProgressBar.setVisibility(View.VISIBLE);
        }else {
            //granted the connection
            btnEditProfile();
            if(user.getprofile_url() == null) {
                Picasso.get().load(R.drawable.user_profile).error(R.drawable.user_profile).into(mProfilePhoto);
            }else{
                Picasso.get().load(user.getprofile_url()).error(R.drawable.user_profile).into(mProfilePhoto);
            }
            miipsID.setText(user.getmiips_id());
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            cityWidgets.setText(user.getCity());
            stateWidgets.setText(user.getState());
            dateBirth.setText(user.getBirth());
            gender.setText(user.getGender());
            mProgressBar.setVisibility(View.GONE);

        }
    }

    private void btnBack() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
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
                    finish();
                }
            }
        };
    }

    private void getUserData(){
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
        //wait fot 1s to get data updated from db
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                getUserData();
            }
        }, 1000);   //2 seconds

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
    }
}