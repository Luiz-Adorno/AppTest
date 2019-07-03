package miips.com.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import miips.com.Models.User;
import miips.com.R;


public class FirebaseMethods {
    private static final String TAG = FirebaseMethods.class.getName();

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }


    public void addNewUserFirestore(String userID, String email, String username, String phone,
                                    String city, String state, String gender, String dateBirth,
                                    String miips_id) {

        CollectionReference dbUsers = db.collection(mContext.getString(R.string.dbname_user));
        User userFirestore = new User(email, phone, username, city, state, gender, dateBirth, miips_id, null);

        dbUsers.document(userID).set(userFirestore).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Created new node");
                } else {
                    Log.d(TAG, "onComplete: error, the node was not created");
                }
            }
        });
    }

    /**
     * Update 'users' node for the current user
     *
     * @param username phoneNumber
     *                 city
     *                 state
     *                 mBirth
     *                 mGender
     */
    public void updateUserSettings(String username, String phoneNumber, String city,
                                   String state, String mBirth, String mGender, String miipsID) {
        String userID = mAuth.getCurrentUser().getUid();

        Log.d(TAG, "updateUser: updating user account settings.");

        if (username != null) {

            Map<String, Object> updateUsername = new HashMap<>();
            updateUsername.put(mContext.getString(R.string.field_username), username);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updateUsername
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: username successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

        if (phoneNumber != null) {
            Map<String, Object> updatePhone = new HashMap<>();
            updatePhone.put(mContext.getString(R.string.field_phone_number), phoneNumber);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updatePhone
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: phone successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

        if (city != null && state != null) {
            Map<String, Object> updateLocation = new HashMap<>();
            updateLocation.put(mContext.getString(R.string.field_city), city);
            updateLocation.put(mContext.getString(R.string.field_state), state);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updateLocation
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

        if (mBirth != null) {
            Map<String, Object> updateBirth = new HashMap<>();
            updateBirth.put(mContext.getString(R.string.field_birth), mBirth);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updateBirth
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

        if (mGender != null) {
            Map<String, Object> updateGender = new HashMap<>();
            updateGender.put(mContext.getString(R.string.field_gender), mGender);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updateGender
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

        if (miipsID != null) {
            Map<String, Object> updateID = new HashMap<>();
            updateID.put(mContext.getString(R.string.field_miips_id), miipsID);
            db.collection(mContext.getString(R.string.dbname_user)).document(userID).update(
                    updateID
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully updated");
                    } else {
                        Log.d(TAG, "onComplete: update failed");
                    }
                }
            });
        }

    }

}
