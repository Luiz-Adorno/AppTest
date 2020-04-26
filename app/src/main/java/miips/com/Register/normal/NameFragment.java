package miips.com.Register.normal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import miips.com.LoginActivity.LoginActivity;
import miips.com.Models.EditFiler;
import miips.com.R;
import miips.com.Register.google.RegisterActivityGoogle;
import miips.com.Utils.ConnectionDetector;

public class NameFragment extends Fragment {
    EditText mName, miipsId;
    String miipsName, username;
    Button next, cancel;
    private Context mContext;
    private ImageView infoMiips;
    ConnectionDetector cd;
    EditFiler filter;
    private ProgressBar mProgessBar;

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
                mProgessBar.setVisibility(View.VISIBLE);
                final RegisterActivity reg = (RegisterActivity) getActivity();

                checkFieldIsExist("miips_id", miipsName, new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            //miipsName is valid
                            miipsId.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            if (checkInputs(username, miipsName)) {

                                if (cd.isConnected()) {

                                    //Send the name and miipsName to activity
                                    reg.getFromName(username, miipsName);

                                    // Begin the transaction
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    // Replace the contents of the container with the new fragment
                                    ft.replace(R.id.frame_layout, new BirthFragment());
                                    ft.commit();
                                    mProgessBar.setVisibility(View.GONE);
                                } else {
                                    buildDialog(getActivity()).show();
                                    mProgessBar.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            mProgessBar.setVisibility(View.GONE);
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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();
                getActivity().finish();
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
            mProgessBar.setVisibility(View.GONE);
            return false;
        } else if (miips.equals("") || miips.length() < 2) {
            miipsId.setError("Apelido inválido!");
            mProgessBar.setVisibility(View.GONE);
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
                        //Field exist
                        onSuccessListener.onSuccess(false);
                    } else {
                        onSuccessListener.onSuccess(true);
                    }

                }
            }
        });
    }
}