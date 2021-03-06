package miips.com.Products;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import miips.com.Cart.CartActivity;
import miips.com.Local.LocalActivity;
import miips.com.Models.Cart;
import miips.com.Models.FeedbackProduct;
import miips.com.Models.Local;
import miips.com.Models.LocalCart;
import miips.com.Models.Products.Products;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Utils.MyPreference;

public class ProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;
    private static final String TAG = "ProductAct";
    private static Products products;
    private static User settings;
    private MyPreference myPreference;
    private TextView sv, qnt;
    private ImageView fav;
    private String name, price, image, description, cnpj_owner, product_category, userUid;
    private Button btn_cart;
    private LinearLayout sizeLayout;
    private ProgressBar mProgessBar;
    private Local local;
    private LocalCart localCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context = ProductActivity.this;
        myPreference = new MyPreference(context);
        mAuth = FirebaseAuth.getInstance();
        String idP = myPreference.getIdClick();
        db = FirebaseFirestore.getInstance();
        qnt = findViewById(R.id.qnt);
        ImageView back_btn = findViewById(R.id.back_arrow);
        fav = findViewById(R.id.save_icon);
        sv = findViewById(R.id.save);
        sizeLayout = findViewById(R.id.sizelayout);
        sizeLayout.setVisibility(View.GONE);
        btn_cart = findViewById(R.id.btn_add_cart);
        mProgessBar = findViewById(R.id.progressBar_cyclic);
        mProgessBar.setVisibility(View.GONE);

        myPreference.setSIZE("");
        Log.d(TAG, "onStart: claro: " + myPreference.getSize());

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.this.onBackPressed();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            userUid = firebaseUser.getUid();
            getDataViaUser(idP);
            saveFav(idP);
        } else {
            String token = myPreference.getToken();
            getData(token, idP);
            //Log.d(TAG, "onCreate:myPrefenrece: "+idP +" e "+ token);

            fav.setImageResource(R.drawable.s2);
            sv.setText("Salvar");
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Faça Login para salvar produtos na lista de favoritos", Toast.LENGTH_SHORT).show();
                }
            });

            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Faça Login para encomendar e comprar produtos", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void initToolber(final String cnpj_owner, String token) {

        final TextView nameStore = findViewById(R.id.id);
        final CircleImageView circleImageView = findViewById(R.id.img_circle);
        //format string for the firestore model
        final String cnpj = cnpj_owner.replaceFirst("/", "-");
        final DocumentReference localRef = db.collection(getString(R.string.cp)).document(token).collection("Local").document(cnpj);
        localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        localCart = new LocalCart();
                        local = document.toObject(Local.class);
                        nameStore.setText(local.getNome_estabelecimento());
                        String image = local.getProfilePhoto();
                        localCart.setBairro(local.getBairro());
                        localCart.setCep(local.getCep());
                        localCart.setComplemento(local.getComplemento());
                        localCart.setName_local(local.getNome_estabelecimento());
                        localCart.setNumero(local.getNumero());
                        localCart.setRua(local.getRua());
                        localCart.setIdLocal(local.getCompany_id());
                        localCart.setCnpj(local.getCnpj());
                        localCart.setImg(image);
                        localCart.setTelefone(local.getTelefone());
                        localCart.setDate(FieldValue.serverTimestamp());

                        Picasso.get().load(image).error(R.drawable.carregando).into(circleImageView);

                        circleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myPreference.setIDLOCAL(cnpj);

                                Intent intent = new Intent(context, LocalActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void addSize(String product_category, List<String> size) {

        if (product_category.equals("Vestuário") || product_category.equals("Calçado")) {
            sizeLayout.setVisibility(View.VISIBLE);
            LinearLayout container = findViewById(R.id.container);

            SingleSelectToggleGroup singleSelectToggleGroup = new SingleSelectToggleGroup(this);
            container.addView(singleSelectToggleGroup);

            for (final String text : size) {
                LabelToggle toggle = new LabelToggle(this);
                toggle.setText(text);
                toggle.setPadding(0, 10, 20, 0);
                toggle.setMarkerColor(Color.BLACK);
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myPreference.setSIZE(text);
                    }
                });
                singleSelectToggleGroup.addView(toggle);
            }
        }
    }


    public void saveFav(final String idP) {

        final String userID = mAuth.getCurrentUser().getUid();

        final DocumentReference favCol = db.collection("users").document(userID).collection("feedbackProduct").document(idP);

        //check if document exist
        favCol.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    //get state
                    favCol.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    FeedbackProduct feedbackProduct;
                                    feedbackProduct = document.toObject(FeedbackProduct.class);

                                    //check state
                                    if (feedbackProduct.getFaveState()) {
                                        fav.setImageResource(R.drawable.s2r);
                                        sv.setText("Salvo");
                                        //removes from favorites
                                        fav.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                favCol.update("faveState", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            fav.setImageResource(R.drawable.s2);
                                                            sv.setText("Salvar");
                                                            Log.d(TAG, "onComplete: fav successfully updated");
                                                        } else {
                                                            Log.d(TAG, "onComplete: update failed");
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        fav.setImageResource(R.drawable.s2);
                                        sv.setText("Salvar");

                                        //add to favorites if the user clicks
                                        fav.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                favCol.update("faveState", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            fav.setImageResource(R.drawable.s2r);
                                                            sv.setText("Salvo");
                                                            Log.d(TAG, "onComplete: fav successfully updated");
                                                        } else {
                                                            Log.d(TAG, "onComplete: update failed");
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    sv.setText("Salvar");
                    fav.setImageResource(R.drawable.s2);

                    //like the product
                    fav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FeedbackProduct feedbackProduct = new FeedbackProduct();
                            feedbackProduct.setFaveState(true);
                            favCol.set(feedbackProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        sv.setText("Salvo");
                                        fav.setImageResource(R.drawable.s2r);
                                        Log.d(TAG, "onComplete: Created new node");
                                    } else {
                                        Log.d(TAG, "onComplete: error, the node was not created");
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    public void initImage(String image) {
        ImageView imageProduct = findViewById(R.id.profile_product);

        Glide.with(context)
                .load(image)
                .into(imageProduct);


        //Picasso.get().load(image).fit().centerCrop().error(R.drawable.carregando).into(imageProduct);
    }

    public void initTexts(String name, String price, String description) {
        TextView nameP = findViewById(R.id.name_product);
        TextView valor = findViewById(R.id.price);
        TextView descri = findViewById(R.id.descipt);

        nameP.setText(name);
        valor.setText(price);
        descri.setText(description);
    }

    public void getData(final String token, final String idPro) {
        DocumentReference productRef = db.collection(getString(R.string.cp)).document(token).collection("Product").document(idPro);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final DocumentSnapshot document = task.getResult();
                products = new Products();
                products = document.toObject(Products.class);
                name = products.getNome_produto();
                description = products.getDescri();
                price = products.getValor();
                image = products.getUrl_product();
                cnpj_owner = products.getCnpj_owner();
                product_category = products.getProduct_category();
                qnt.setText(products.getQuantidade());
                final int qnt = Integer.parseInt(products.getQuantidade());
                final String cnpj = cnpj_owner.replaceFirst("/", "-");

                addSize(product_category, products.getSize());

                initToolber(cnpj_owner, token);
                initImage(image);
                initTexts(name, price, description);


                final Cart cart = new Cart();
                cart.setImg(image);
                cart.setName(name);
                cart.setPrice(price);
                cart.setCategory(product_category);
                cart.setId(document.getId());

                //i put this onclick here because it will be triggered only after loading the page
                btn_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (product_category.equals("Vestuário") || product_category.equals("Calçado")) {
                            if (myPreference.getSize().length() > 0) {
                                if (qnt > 0) {
                                    mProgessBar.setVisibility(View.VISIBLE);
                                    cart.setSize(myPreference.getSize());
                                    //creates a cart document on firestore
                                    final DocumentReference documentReference = db.collection("users").document(userUid).collection("cart").document(cnpj);
                                    documentReference.set(localCart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            documentReference.collection("products").document(idPro).set(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(context, CartActivity.class);
                                                    myPreference.setIDLOCAL(cnpj);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                                    finish();
                                                }
                                            });
                                        }
                                    });

                                } else {
                                    AlertDialog.Builder alertSingOut = new AlertDialog.Builder(context);
                                    alertSingOut.setMessage("Oops, esse produto está fora de estoque, salve ele para receber notícias de quando estiver disponível");
                                    alertSingOut.setCancelable(true);
                                    alertSingOut.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    alertSingOut.create().show();
                                }
                            } else {
                                Toast.makeText(context, "Por favor selecione o tamanho do produto", Toast.LENGTH_SHORT).show();
                            }
                            //other categories
                        } else {
                            if (qnt > 0) {
                                mProgessBar.setVisibility(View.VISIBLE);
                                //creates a cart document on firestore
                                final DocumentReference documentReference = db.collection("users").document(userUid).collection("cart").document(cnpj);
                                documentReference.set(localCart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        documentReference.collection("products").document(idPro).set(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent(context, CartActivity.class);
                                                myPreference.setIDLOCAL(cnpj);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();
                                            }
                                        });
                                    }
                                });

                            } else {
                                AlertDialog.Builder alertSingOut = new AlertDialog.Builder(context);
                                alertSingOut.setMessage("Oops, esse produto está fora de estoque, salve ele para receber notícias de quando estiver disponível");
                                alertSingOut.setCancelable(true);
                                alertSingOut.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alertSingOut.create().show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void getDataViaUser(final String idPro) {
        settings = new User();
        String userID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onDataChange: user id ta assim: " + userID);
        DocumentReference docRef = db.collection(context.getString(R.string.dbname_user)).document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        settings = document.toObject(User.class);
                        String state = settings.getState();
                        String city = settings.getCity();
                        final String gender = settings.getGender();
                        final String token = city + "-" + state;

                        getData(token, idPro);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
