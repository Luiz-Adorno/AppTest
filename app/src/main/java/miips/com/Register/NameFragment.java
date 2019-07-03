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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class NameFragment extends Fragment {
    EditText mName, miipsId;
    String miipsName, username;
    Button next, cancel;
    private Context mContext;
    private ImageView infoMiips;
    EditFiler filter;

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
        infoMiips = view.findViewById(R.id.info_miipsname);
        filter = new EditFiler(mContext);
        filter.setFilter(miipsId, mName);

        initInfoDialog();
        initCancel();
        init();
        return view;
    }

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miipsName = miipsId.getText().toString();
                username = mName.getText().toString();
                final RegisterActivity reg = (RegisterActivity) getActivity();

                checkFieldIsExist("miips_id", miipsName, new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            //miipsName is valid
                            miipsId.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            if (checkInputs(username, miipsName)) {
                                //Send the name and miipsName to activity
                                reg.getFromName(username, miipsName);

                                // Begin the transaction
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                // Replace the contents of the container with the new fragment
                                ft.replace(R.id.frame_layout, new BirthFragment());
                                ft.commit();
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