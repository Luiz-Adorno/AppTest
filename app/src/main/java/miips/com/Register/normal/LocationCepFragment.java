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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vicmikhailau.maskededittext.MaskedEditText;

import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Register.google.NameFragmentGoogle;
import miips.com.Register.google.RegisterActivityGoogle;
import miips.com.Utils.ConnectionDetector;
import miips.com.Utils.StatesManipulation;
import miips.com.Utils.ZipCode.APIRetrofitService;
import miips.com.Utils.ZipCode.CEP;
import miips.com.Utils.ZipCode.CEPDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LocationCepFragment extends Fragment {
    private static final String TAG = "LocationCepFragment";
    private TextView cityCep, stateCep, gotoCep;
    private Button btnCep;
    private MaskedEditText cep;
    private ProgressBar mProgessBar;
    private String cityString, stateString;
    private Button next, cancel;
    ConnectionDetector cd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_location_cep, container, false);
        mProgessBar = view.findViewById(R.id.registerProgressBar);
        mProgessBar.setVisibility(View.GONE);
        cep = view.findViewById(R.id.cep);
        btnCep = view.findViewById(R.id.button_location);
        cityCep = view.findViewById(R.id.city);
        stateCep = view.findViewById(R.id.state);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        gotoCep = view.findViewById(R.id.goto_cep);
        cd = new ConnectionDetector(getActivity());

        cepInit();
        init();
        initCancel();
        setGotoCep();
        return view;
    }

    private void setGotoCep() {
        gotoCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.frame_layout, new LocationFragment());
                ft.commit();
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

    private void init() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs(cityString)) {

                    if (cd.isConnected()) {
                        RegisterActivity reg = (RegisterActivity) getActivity();
                        reg.getFromLocation(cityString, stateString);

                        // Begin the transaction
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.frame_layout, new EmailFragment());
                        ft.commit();
                    } else {
                        buildDialog(getActivity()).show();
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

    private boolean checkInputs(String cityAux) {
        if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(getActivity(), R.string.add_city_state, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void cepInit() {
        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

        btnCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cep.getText().toString().isEmpty()) {
                    mProgessBar.setVisibility(View.VISIBLE);

                    Call<CEP> callCEPByCEP = service.getEnderecoByCEP(cep.getText().toString());

                    callCEPByCEP.enqueue(new Callback<CEP>() {
                        @Override
                        public void onResponse(Call<CEP> call, Response<CEP> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(
                                        getActivity(), R.string.cep_error,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                CEP cep = response.body();

                                cityString = cep.getLocalidade();
                                stateString =  StatesManipulation.stManipulation(cep.getUf());

                                cityCep.setText(cityString);
                                stateCep.setText(stateString);

                                if (cityString == null || stateString == null) {
                                    Toast.makeText(
                                            getActivity(),
                                            "CEP inválido",
                                            Toast.LENGTH_LONG).show();
                                }

                                //Retorno no Log
                                Log.d(TAG, cep.toString());
                            }
                            mProgessBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            Toast.makeText(
                                    getActivity(),
                                    "Error ao pegar o cep",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });
    }
}