package miips.com.Register.google;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.Models.EditFiler;
import miips.com.R;
import miips.com.Register.normal.BirthFragment;
import miips.com.Register.normal.RegisterActivity;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.FirebaseMethods;

public class NameFragmentGoogle extends Fragment {
    EditText mName, miipsId;
    String miipsName, username;
    Button next, cancel;
    private Context mContext;
    private ImageView infoMiips;
    private ProgressBar mProgessBar;
    ConnectionDetector cd;
    EditFiler filter;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;
    GoogleSignInClient mGoogleSignInClient;


    private String Birth, Gender, City, State;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_name, container, false);
        //init the components
        mName = view.findViewById(R.id.name_register);
        miipsId = view.findViewById(R.id.miips_id);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        mContext = getActivity();
        mProgessBar = view.findViewById(R.id.progressBar_cyclic);
        mProgessBar.setVisibility(View.GONE);
        infoMiips = view.findViewById(R.id.info_miipsname);
        filter = new EditFiler(mContext);
        filter.setFilter(miipsId, mName);
        cd = new ConnectionDetector(getActivity());
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(getActivity());

        //get the vars in the activity container from the others fragments
        RegisterActivityGoogle reg = (RegisterActivityGoogle) getActivity();
        Birth = reg.Birth;
        Gender = reg.Gender;
        City = reg.City;
        State = reg.State;

        initInfoDialog();
        initCancel();
        init();

        return view;
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

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miipsName = miipsId.getText().toString();
                username = mName.getText().toString();
                final String email = mAuth.getCurrentUser().getEmail();
                final String userID = mAuth.getCurrentUser().getUid();

                checkFieldIsExist("miips_id", miipsName, new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            //miipsName is valid
                            miipsId.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            if (checkInputs(username, miipsName)) {
                                mProgessBar.setVisibility(View.VISIBLE);

                                if (cd.isConnected()) {
                                    firebaseMethods.addNewUserFirestore(userID, email, username, City, State,
                                            Gender, Birth, miipsName);

                                    Toast.makeText(getActivity(), "Conta criada com sucesso.", Toast.LENGTH_SHORT).show();
                                    mProgessBar.setVisibility(View.GONE);

                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                } else {
                                    buildDialog(getActivity()).show();
                                    mProgessBar.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            //miipsName exists, try another
                            miipsId.setError("Apelido existente, tente outro");
                        }
                    }
                });
            }
        });
    }

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
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
                        getActivity().onBackPressed();
                        getActivity().finish();
                    }
                });
                alertSingOut.setNegativeButton(R.string.no, null);
                alertSingOut.create().show();
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

    private void initInfoDialog() {
        infoMiips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfoMiips();
            }
        });
    }

    private boolean checkInputs(String name, String miips) {
        if (name.length() < 3 || name.equals("")) {
            mName.setError("Nome inválido");
            return false;
        } else if (miips.equals("") || miips.length() < 2) {
            miipsId.setError("Apelido inválido!");
            return false;
        }
        return true;
    }

    //check in database if miipsName already exist
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
}