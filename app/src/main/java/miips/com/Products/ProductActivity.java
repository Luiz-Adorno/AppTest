package miips.com.Products;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import miips.com.Local.LocalActivity;
import miips.com.Models.FeedbackProduct;
import miips.com.Models.Local;
import miips.com.Models.Products.Products;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Utils.MyPreference;
import miips.com.cart.CartActivity;

public class ProductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;
    private static final String TAG = "ProductAct";
    private static Products products;
    private static User settings;
    private MyPreference myPreference;
    private TextView sv;
    private ImageView fav;
    private String name, price, image, description, cnpj_owner;
    private Button btn_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context = ProductActivity.this;
        myPreference = new MyPreference(context);
        mAuth = FirebaseAuth.getInstance();
        String idP = myPreference.getIdClick();
        db = FirebaseFirestore.getInstance();
        ImageView back_btn = findViewById(R.id.back_arrow);
        fav = findViewById(R.id.save_icon);
        sv = findViewById(R.id.save);
        btn_cart = findViewById(R.id.btn_add_cart);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.this.onBackPressed();
            }
        });

        if (mAuth.getCurrentUser() != null) {
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
        DocumentReference localRef = db.collection(getString(R.string.cp)).document(token).collection("Local").document(cnpj);
        localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Local local;
                        local = document.toObject(Local.class);
                        nameStore.setText(local.getNome_estabelecimento());
                        String image = local.getProfilePhoto();

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

    public void saveFav(final String idP) {
        //i put this onclick here because it will be triggered only after loading the page
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


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

    public void getData(final String token, String idPro) {
        DocumentReference productRef = db.collection(getString(R.string.cp)).document(token).collection("Product").document(idPro);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                products = new Products();
                products = document.toObject(Products.class);
                name = products.getNome_produto();
                description = products.getDescri();
                price = products.getValor();
                image = products.getUrl_product();
                cnpj_owner = products.getCnpj_owner();

                initToolber(cnpj_owner, token);
                initImage(image);
                initTexts(name, price, description);
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
