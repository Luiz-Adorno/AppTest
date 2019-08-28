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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Utils.ConnectionDetector;

public class BirthFragment extends Fragment {
    private MaskedEditText dateEditText;
    private RadioGroup radioGender;
    private RadioButton selectedRadioButton;
    private Context mContext;
    Button next, cancel;
    String dateBirth, gender;
    ConnectionDetector cd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register_birth, container, false);
        dateEditText = view.findViewById(R.id.date);
        radioGender = view.findViewById(R.id.gender_group);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        mContext = getActivity();
        cd = new ConnectionDetector(getActivity());

        //get gender selected
        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioGender.getCheckedRadioButtonId() == -1) {
                    //do nothing
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioGender.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectedRadioButton = view.findViewById(selectedId);
                    selectedRadioButton.getText();
                    gender = selectedRadioButton.getText().toString();
                }
            }
        });

        init();
        initCancel();
        return view;
    }

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateBirth = dateEditText.getText().toString();
                if (radioGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(mContext, R.string.gende_select, Toast.LENGTH_SHORT).show();

                } else {
                    if (checkInputs(dateBirth)) {

                        if (cd.isConnected()) {
                            RegisterActivity reg = (RegisterActivity) getActivity();
                            reg.getFromBirth(dateBirth, gender);


                            // Begin the transaction
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.frame_layout, new LocationCepFragment());
                            ft.commit();
                        } else {
                            buildDialog(getActivity()).show();
                        }

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

    private boolean checkInputs(String dateBirth) {
        if (dateBirth.length() < 10) {
            dateEditText.setError("Data inválida");
            return false;
        }
        return true;
    }
}