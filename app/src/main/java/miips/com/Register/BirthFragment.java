package miips.com.Register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import miips.com.R;

public class BirthFragment extends Fragment {
    private MaskedEditText dateEditText;
    private RadioGroup radioGender;
    private RadioButton selectedRadioButton;
    private Context mContext;
    Button next, cancel;
    String dateBirth, gender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register_birth, container, false);
        dateEditText = view.findViewById(R.id.date);
        radioGender = view.findViewById(R.id.gender_group);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        mContext = getActivity();

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
                    if(checkInputs(dateBirth)){
                        RegisterActivity reg = (RegisterActivity) getActivity();
                        reg.getFromBirth(dateBirth, gender);


                        // Begin the transaction
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.frame_layout, new LocationFragment());
                        ft.commit();
                    }
                }
            }
        });

    }

    private boolean checkInputs(String dateBirth) {
        if (dateBirth.length() < 9) {
            dateEditText.setError("Data inválida");
            return false;
        }
        return true;
    }
}