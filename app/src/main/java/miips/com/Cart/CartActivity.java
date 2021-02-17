package miips.com.Cart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import miips.com.Adapters.CartAdapter.RecyclerViewCartAdapter;
import miips.com.Carts.CartsListActivity;
import miips.com.Local.LocalActivity;
import miips.com.Models.Cart;
import miips.com.Models.OrderAdress;
import miips.com.R;
import miips.com.Utils.ExampleDialog;
import miips.com.Utils.MyPreference;

public class CartActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private static final String TAG = "CartActivity";
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private Context context = CartActivity.this;
    private TextView txtAdd;
    private MyPreference myPreference;
    private String userID, localId, payment, order;
    private int totalPrice = 0;
    private FirebaseAuth mAuth;
    private LinearLayout payLayout;
    private TextView local_name, total_price;
    private LinearLayout deliv;
    private TextView tlt, adress, change_adress;
    private ProgressBar mProgressBar;
    private RadioGroup radioPayment, radioOrder;
    private ImageView back, delete;
    private Button btn;
    private String storeAdress, storeAdressStore;
    private RelativeLayout change;
    private RadioButton selectedRadioButton, selectOder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //retrieve user information from database Firestore
        payLayout = findViewById(R.id.paymentLayout);
        db = FirebaseFirestore.getInstance();
        mProgressBar = findViewById(R.id.progressBar_cyclic);
        recyclerView = findViewById(R.id.recycler_cart);
        myPreference = new MyPreference(context);
        txtAdd = findViewById(R.id.txt_add);
        btn = findViewById(R.id.btn_buy);
        delete = findViewById(R.id.delete);
        change_adress = findViewById(R.id.change_adress);
        total_price = findViewById(R.id.total);
        back = findViewById(R.id.back_arrow);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        radioOrder = findViewById(R.id.local);
        radioPayment = findViewById(R.id.gender_group);
        localId = myPreference.getIdLocal();
        local_name = findViewById(R.id.local_name);
        deliv = findViewById(R.id.delivery_layout);
        tlt = findViewById(R.id.title_delivery);
        adress = findViewById(R.id.adress_plus_number);
        change = findViewById(R.id.change_icon);
        backPressed();
        getLocal();
        deleteCart();
        getOrderMethod();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioOrder.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(context, "Por favor escolha como vai receber o produto", Toast.LENGTH_SHORT).show();
                } else {
                    if (order.equals("Retirar na loja")) {
                        Toast.makeText(context, "Tudo Okayy", Toast.LENGTH_SHORT).show();
                    } else if (order.equals("Entrega em local escolhido")) {
                        if (radioPayment.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(context, "Por favor selecione o método de pagamento", Toast.LENGTH_SHORT).show();
                            //do nothing
                        } else {
                            if (adress.getText().toString().isEmpty()) {
                                Toast.makeText(context, "Por favor adcione o endereço de entrega", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Tudo Okayy", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

            }
        });
    }

    private void getOrderMethod() {
        radioOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioOrder.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(context, "Por favor escolha como vai receber o produto", Toast.LENGTH_SHORT).show();
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioOrder.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectOder = findViewById(selectedId);
                    selectOder.getText();
                    order = selectOder.getText().toString();
                    Log.d(TAG, "onCheckedChanged: bolsa: " + order);

                    if (order.equals("Retirar na loja")) {
                        payLayout.setVisibility(View.GONE);
                        deliv.setVisibility(View.VISIBLE);
                        tlt.setText("Buscar no endereço");
                        change.setVisibility(View.GONE);
                        adress.setText(storeAdressStore);

                    } else if (order.equals("Entrega em local escolhido")) {
                        adress.setText("");
                        payLayout.setVisibility(View.VISIBLE);
                        change.setVisibility(View.VISIBLE);
                        deliv.setVisibility(View.VISIBLE);
                        tlt.setText("Entregar no endereço");
                        getLocalForOrder();
                        getPayment();
                    }
                }
            }
        });
    }

    private void getLocalForOrder() {
        final DocumentReference documentReference = db.collection("users")
                .document(userID).collection("localToOrder").document("local");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    change_adress.setText("Alterar endereço de entrega");
                                    OrderAdress orderAdress = document.toObject(OrderAdress.class);
                                    storeAdress = orderAdress.getRua() + ", " + orderAdress.getNumero() + ", " + orderAdress.getComplemento();
                                    adress.setText(storeAdress);
                                    change_adress.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ExampleDialog exampleDialog = new ExampleDialog();
                                            exampleDialog.show(getSupportFragmentManager(), "example dialog");
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    change_adress.setText("Adcionar endereço de entrega");
                    change_adress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ExampleDialog exampleDialog = new ExampleDialog();
                            exampleDialog.show(getSupportFragmentManager(), "example dialog");
                        }
                    });
                }
            }
        });

    }

    @Override
    public void applyTexts(String rua, String numero, String complemento) {
        storeAdress = rua + ", " + numero + ", " + complemento;
        adress.setText(storeAdress);
        DocumentReference documentReference = db.collection("users")
                .document(userID).collection("localToOrder").document("local");

        OrderAdress orderAdress = new OrderAdress();
        orderAdress.setComplemento(complemento);
        orderAdress.setNumero(numero);
        orderAdress.setRua(rua);

        documentReference.set(orderAdress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context,"Endereço salvo!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Falha ao adcionar endereço, tente novamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPayment() {
        //get gender selected
        radioPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioPayment.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(context, "Por favor selecione o método de pagamento", Toast.LENGTH_SHORT).show();
                    //do nothing
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioPayment.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.getText();
                    payment = selectedRadioButton.getText().toString();
                    Log.d(TAG, "onCheckedChanged: bolsa: " + payment);

                }
            }
        });
    }


    private void addMore(final String id_local) {
        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, LocalActivity.class);
                String cnpj = id_local.replaceFirst("/", "-");
                myPreference.setIDLOCAL(cnpj);
                context.startActivity(intent1);
                overridePendingTransition(0, 0);
            }
        });
    }

    public void deleteCart() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertSingOut = new AlertDialog.Builder(context);
                alertSingOut.setMessage("Deseja deletar carrinho?");
                alertSingOut.setCancelable(true);
                alertSingOut.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //first delete the subcollection and then the document
                        mProgressBar.setVisibility(View.VISIBLE);
                        final DocumentReference docR = db.collection("users")
                                .document(userID).collection("cart").document(localId);
                        CollectionReference documentReference = db.collection("users")
                                .document(userID).collection("cart").document(localId).collection("products");

                        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                                docR.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent1 = new Intent(context, CartsListActivity.class);
                                        startActivity(intent1);
                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Não foi possivel deletar carrinho, tente mais tarde.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });


                    }
                });
                alertSingOut.setNegativeButton("Não", null);
                alertSingOut.create().show();
            }
        });
    }

    public void setupRecycler(ArrayList<Cart> list) throws ParseException {
        Log.d(TAG, "setupRecycler: 88 interve");
        recyclerView.setHasFixedSize(true);
        RecyclerViewCartAdapter adapter = new RecyclerViewCartAdapter(context, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.notifyDataSetChanged();

        setTotalPrice(totalPrice);
    }

    public void setTotalPrice(int tlt) throws ParseException {
        Log.d(TAG, "setTotalPrice: diatauda: " + tlt);
        if (tlt == 0) {
            total_price.setText("0,00");
        } else {
            String prepared = prepare(String.valueOf(tlt));

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');

            DecimalFormat format = new DecimalFormat("###,###.00");
            format.setDecimalFormatSymbols(symbols);
            format.setParseBigDecimal(true);

            BigDecimal bigDecimal = (BigDecimal) format.parse(prepared);
            String n = format.format(bigDecimal);

            total_price.setText(n);
        }
    }


    public static String prepare(String input) {
        if (input.length() == 2) {
            return "," + input;
        }
        if (input.length() == 1) {
            return ",0" + input;
        }
        String integerPart = input.substring(0, input.length() - 2);
        String fraction = input.substring(input.length() - 2);
        return integerPart + "," + fraction;
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<Cart> lista) throws ParseException;
    }

    private void getLocal() {
        DocumentReference documentReference = db.collection("users")
                .document(userID).collection("cart").document(localId);
        Log.d(TAG, "getLocal: as she wear: " + localId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                mProgressBar.setVisibility(View.GONE);
                storeAdressStore = document.getString("rua") + ", " + document.getString("numero") + ", " + document.getString("complemento");

                local_name.setText(document.getString("name_local"));
                addMore((document.getString("cnpj")));
            }
        });

    }

    private void getProducts(final CartActivity.FirestoreCallback firestoreCallback) {
        final ArrayList<Cart> lista = new ArrayList<>();
        CollectionReference collectionReference = db.collection("users")
                .document(userID).collection("cart").document(localId).collection("products");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Cart cart;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        cart = document.toObject(Cart.class);
                        lista.add(cart);
                        String y = cart.getPrice().replace(",", "");
                        int value1 = Integer.parseInt(y);
                        totalPrice = totalPrice + value1;
                    }
                    try {
                        firestoreCallback.onCallback(lista);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void backPressed() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(context, CartsListActivity.class);
                context.startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        totalPrice = 0;
        getProducts(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Cart> lista) throws ParseException {
                setupRecycler(lista);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, CartsListActivity.class);
        context.startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
