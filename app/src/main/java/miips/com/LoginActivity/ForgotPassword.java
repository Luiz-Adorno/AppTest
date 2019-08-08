package miips.com.LoginActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import miips.com.R;

public class ForgotPassword extends AppCompatActivity {

    Button btnSend;
    EditText getEmail;
    Context mContext;
    private FirebaseAuth auth;
    ImageView arrow;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSend = findViewById(R.id.btn_send_email);
        auth = FirebaseAuth.getInstance();
        getEmail = findViewById(R.id.get_email);
        mContext = ForgotPassword.this;
        arrow = findViewById(R.id.backArrowForget);
        mProgressBar = findViewById(R.id.ForgetProgressBar);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSendPass = new Intent(mContext, LoginActivity.class);
                startActivity(intentSendPass);
                finish();
            }
        });
        mProgressBar.setVisibility(View.GONE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = getEmail.getText().toString();
                if (isStringNull(emailAddress)) {
                    getEmail.setError("Digite seu email");
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        buildDialog(mContext).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    } else {
                                        mProgressBar.setVisibility(View.GONE);
                                        getEmail.setError("Email inválido");
                                    }
                                }
                            });
                }
            }
        });


    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Email enviado");
        builder.setMessage("Abra seu email e clique no link que enviamos para você redefinir sua senha!");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentSendPass = new Intent(mContext, LoginActivity.class);
                startActivity(intentSendPass);
                finish();
            }
        });

        return builder;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }

    private boolean isStringNull(String string) {
        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
