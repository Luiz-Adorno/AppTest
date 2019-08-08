package miips.com.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import miips.com.LoginActivity.LoginActivity;
import miips.com.LoginActivity.RegisterWithGoogleActivity;
import miips.com.R;

// store the last visible activity in SharedPreferences and have a Dispatcher activity
// that starts the last activity according to the preferences.

public class Dispatcher extends Activity {

  private Context context = Dispatcher.this;
  private static final String TAG = "SplashScreen";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Class<?> activityClass;

    try {
      SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
      activityClass = Class.forName(
              prefs.getString("lastActivity", Dispatcher.class.getName()));//is just the default Activity that should start if no activity is saved in the preferences
    } catch (ClassNotFoundException ex) {
      activityClass = Dispatcher.class;
    }

    startActivity(new Intent(this, activityClass));

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //check if user is logged
    checkCurrentUser(mAuth.getCurrentUser());
  }

  private void checkCurrentUser(FirebaseUser user) {
    if (user == null) {
      Log.d(TAG, "checkCurrentUser: user is null");
      Intent intent = new Intent(context, LoginActivity.class);
      startActivity(intent);
      overridePendingTransition(0, 0);
      finish();
    } else {
      FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
              //user is successfully logged in
              Intent intent = new Intent(context, HomeActivity.class);
              Log.d(TAG, "checkCurrentUser: user not null");
              startActivity(intent);
              overridePendingTransition(0, 0);
              finish();
            } else {
              Log.d(TAG, "checkCurrentUser: user is null");
              Intent intent = new Intent(context, RegisterWithGoogleActivity.class);
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

}