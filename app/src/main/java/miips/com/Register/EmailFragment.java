package miips.com.Register;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.List;

import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.FirebaseMethods;

public class EmailFragment extends Fragment {
    private static final String TAG = "EmailFragment";
    EditText emailE, passwordE, confirmPasswordE;
    Button next, cancel;
    public String Name, MiipsID, Birth, Gender, City, State;
    ConnectionDetector cd;
    private ProgressBar mProgessBar;
    FirebaseMethods firebaseMethods;
    String email, password;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_email, container, false);
        emailE = view.findViewById(R.id.email_register);
        passwordE = view.findViewById(R.id.password_register);
        confirmPasswordE = view.findViewById(R.id.confirm_password);
        cancel = view.findViewById(R.id.cancel);
        next = view.findViewById(R.id.button_register);
        cd = new ConnectionDetector(getActivity());
        mProgessBar = view.findViewById(R.id.registerProgressBar);
        mProgessBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(getActivity());
        setupFirebaseAuth();

        //get the vars in the activity container from the others fragments
        RegisterActivity reg = (RegisterActivity) getActivity();
        Name = reg.Name;
        MiipsID = reg.MiipsID;
        Birth = reg.Birth;
        Gender = reg.Gender;
        City = reg.City;
        State = reg.State;

        init();
        initCancel();


        return view;
    }

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Click ");
                email = emailE.getText().toString();
                password = passwordE.getText().toString();
                String confirmPassword = confirmPasswordE.getText().toString();


                if (checkInputs(email, password, confirmPassword)) {
                    mProgessBar.setVisibility(View.VISIBLE);

                    checkFieldIsExist("miips_id", MiipsID, new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                //miipsName valid

                                //Identify internet connection
                                if (cd.isConnected()) {
                                    registerNewEmail(email, password);


                                } else {
                                    buildDialog(getActivity()).show();
                                    mProgessBar.setVisibility(View.GONE);
                                }
                                passwordE.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            } else {
                                dialogMiipsId(getActivity()).show();
                                //miipsName exists, try another
                                mProgessBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });
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

    private boolean verifyPassword(String password, String confirm) {
        if (password.equals(confirm)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkInputs(String email, String password, String confirmPassword) {
        if (email == null || email.equals("") || !isValidEmail(email)) {
            emailE.setError("Email inválido");
            return false;
        } else if (password.equals("") || password.length() < 6) {
            passwordE.setError("Senha precisa ter 6 ou mais caracteres!");
            return false;
        } else if (!verifyPassword(password, confirmPassword)) {
            confirmPasswordE.setError("Senhas distintas");
            return false;
        }
        return true;
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //check in database if miipsname exist
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

    public AlertDialog.Builder dialogMiipsId(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Ops ocorreu um problema");
        builder.setMessage("Alguém acabou de cadastrar o Miips ID que você escolheu, por favor escolher outro");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go to username fragment

                // Begin the transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.frame_layout, new NameFragment());
                ft.commit();
            }
        });

        return builder;
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
                                Toast.makeText(getActivity(), "Conta criada com sucesso.", Toast.LENGTH_SHORT).show();
                                mProgessBar.setVisibility(View.GONE);

                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            emailE.setError("Email inválido ou existente!");
                            mProgessBar.setVisibility(View.GONE);

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
                    firebaseMethods.addNewUserFirestore(userID, email, Name, City, State,
                            Gender, Birth, MiipsID);

                }

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}