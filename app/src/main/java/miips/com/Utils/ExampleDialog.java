package miips.com.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import miips.com.R;


public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editNro;
    private EditText editComple, editRua;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Endereço de entrega")
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String numero = editNro.getText().toString();
                        String complemento = editComple.getText().toString();
                        String rua = editRua.getText().toString();
                        if (!numero.isEmpty() || !complemento.isEmpty() | !rua.isEmpty()){
                            listener.applyTexts(rua, numero, complemento);
                        }else{
                            Toast.makeText(getContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        editComple = view.findViewById(R.id.edit_comple);
        editNro = view.findViewById(R.id.edit_numero);
        editRua = view.findViewById(R.id.edit_rua);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String rua, String numero, String complemento);
    }
}