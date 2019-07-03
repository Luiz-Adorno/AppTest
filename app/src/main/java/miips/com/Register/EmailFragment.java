package miips.com.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import miips.com.LoginActivity.LoginActivity;
import miips.com.R;

public class EmailFragment extends Fragment {
    private static final String TAG = "EmailFragment";
    EditText emailE, passwordE, confirmPasswordE;
    Button next, cancel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_email, container, false);
        emailE = view.findViewById(R.id.email_register);
        passwordE = view.findViewById(R.id.password_register);
        confirmPasswordE = view.findViewById(R.id.confirm_password);
        cancel = view.findViewById(R.id.cancel);
        next = view.findViewById(R.id.button_register);
        init();
        initCancel();


        return view;
    }

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Click ");
                String email = emailE.getText().toString();
                String password = passwordE.getText().toString();
                String confirmPassword = confirmPasswordE.getText().toString();
                RegisterActivity reg = (RegisterActivity) getActivity();

                if (checkInputs(email, password, confirmPassword)) {
                    reg.getFromEmail(email, password);
                    // Begin the transaction
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    // Replace the contents of the container with the new fragment
                    ft.replace(R.id.frame_layout, new NameFragment());
                    ft.commit();
                }

            }
        });
    }

    private void initCancel(){
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
        } else if (!verifyPassword(password, confirmPassword)){
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
}