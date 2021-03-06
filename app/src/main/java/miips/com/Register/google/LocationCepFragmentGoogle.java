package miips.com.Register.google;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vicmikhailau.maskededittext.MaskedEditText;

import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
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


public class LocationCepFragmentGoogle extends Fragment {
    private static final String TAG = "LocationCepFragment";
    private TextView cityCep, stateCep, gotoCep;
    private Button btnCep;
    private MaskedEditText cep;
    private ProgressBar mProgessBar;
    private String cityString, stateString;
    private Button next, cancel;
    ConnectionDetector cd;
    private RelativeLayout lay1, lay2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_location_cep, container, false);
        mProgessBar = view.findViewById(R.id.progressBar_cyclic);
        mProgessBar.setVisibility(View.GONE);
        cep = view.findViewById(R.id.cep);
        btnCep = view.findViewById(R.id.button_location);
        cityCep = view.findViewById(R.id.city);
        stateCep = view.findViewById(R.id.state);
        next = view.findViewById(R.id.button_register);
        cancel = view.findViewById(R.id.cancel);
        gotoCep = view.findViewById(R.id.goto_cep);
        cd = new ConnectionDetector(getActivity());
        lay1 = view.findViewById(R.id.layout1);
        lay2 = view.findViewById(R.id.layout2);
        lay1.setVisibility(View.GONE);
        lay2.setVisibility(View.GONE);

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
                ft.replace(R.id.frame_layout, new LocationFragmentGoogle());
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
                if (checkInputs(cityString, stateString)) {

                    if (cd.isConnected()) {
                        RegisterActivityGoogle reg = (RegisterActivityGoogle) getActivity();
                        reg.getFromLocation(cityString, stateString);

                        // Begin the transaction
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.frame_layout, new NameFragmentGoogle());
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

    private boolean checkInputs(String cityAux, String stateString) {
        if (cityAux == null || cityAux.equals("")) {
            Toast.makeText(getActivity(), R.string.add_city_state, Toast.LENGTH_SHORT).show();
            return false;
        }else if (stateString == null || stateString.equals("")){
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
                                stateString =  cep.getUf();

                                lay1.setVisibility(View.VISIBLE);
                                lay2.setVisibility(View.VISIBLE);

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