package miips.com.Local;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import miips.com.Adapters.LocalAdapter.StaggeredRecyclerViewAdapter;
import miips.com.Adapters.SearchAdapters.PostViewHolder;
import miips.com.Models.CompanyModel;
import miips.com.Models.FeedbackLocal;
import miips.com.Models.FollowersClass;
import miips.com.Models.Local;
import miips.com.Models.Post;
import miips.com.Models.Products.Products;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Utils.MyPreference;

public class LocalActivity extends AppCompatActivity {

    private static final String TAG = "LocalAcitivity";
    private static final int NUM_COLUMNS = 3;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;
    private RecyclerView mRecyclerView;
    private static User user;
    public String doc_id;
    private CircleImageView logo;
    private TextView nroP, nroS, flw, name;
    private ImageView heart, img_bk;
    private FirestorePagingAdapter<Post, PostViewHolder> mAdapter;
    private ArrayList<Products> listUniversal = new ArrayList<>();
    private LinearLayout ln;
    private MyPreference myPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        img_bk = findViewById(R.id.img_bk);
        nroP = findViewById(R.id.nro_produ);
        nroS = findViewById(R.id.nro_follows);
        logo = findViewById(R.id.logo);
        flw = findViewById(R.id.follow);
        heart = findViewById(R.id.heart);
        name = findViewById(R.id.name_local);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        context = LocalActivity.this;
        mRecyclerView = findViewById(R.id.recyclerView);
        ImageView back_btn = findViewById(R.id.back_arrow);
        ln = findViewById(R.id.ln);
        myPreference = new MyPreference(context);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalActivity.this.onBackPressed();
            }
        });

        // Init mRecyclerView
        mRecyclerView.setHasFixedSize(true);

        callData();

        if (mAuth.getCurrentUser() != null) {
            String idL = myPreference.getIdLocal();
            saveFav(idL);
        } else {
            heart.setImageResource(R.drawable.s2);
            flw.setText("Seguir");
            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Faça Login para seguir lojas e curtir produtos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void callData() {
        if (mAuth.getCurrentUser() != null) {
            String userID = mAuth.getCurrentUser().getUid();
            user = new User();

            Log.d(TAG, "onDataChange: user id ta assim: " + userID);
            DocumentReference docRef = db.collection(context.getString(R.string.dbname_user)).document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = document.toObject(User.class);
                            String state = user.getState();
                            String city = user.getCity();
                            final String docID = city + "-" + state;

                            setupAdapter(new FirestoreCallback() {
                                @Override
                                public void onCallback(ArrayList<Products> list) {
                                    StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                                            new StaggeredRecyclerViewAdapter(LocalActivity.this, list);
                                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
                                    mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                                    mRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
                                }
                            }, docID);

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            MyPreference myPreference = new MyPreference(context);
            doc_id = myPreference.getToken();
            //  Log.d(TAG, "onCreateView: doc_id: "+ doc_id);
            setupAdapter(new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<Products> list) {
                    StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                            new StaggeredRecyclerViewAdapter(LocalActivity.this, list);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                    mRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
                }
            }, doc_id);

        }
    }

    private void setData(String docID, String idLocal) {
        final DocumentReference dr = db.collection("allCnpjRegistered").document(idLocal);
        DocumentReference documentReference = db.collection("commercialPlaces").document(docID).collection("Local").document(idLocal);

        //get number of followers
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FollowersClass fd;
                        fd = document.toObject(FollowersClass.class);

                        nroS.setText(String.valueOf(fd.getFollowers()));
                    }
                }
            }
        });

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Local local;
                        local = document.toObject(Local.class);
                        Log.d(TAG, "onComplete: trtrtr: " + local.getProfilePhoto());

                        Picasso.get().load(local.getProfilePhoto()).fit().centerCrop().into(img_bk);

                        nroP.setText(String.valueOf(local.getProduct_count()));

                        Log.d(TAG, "onComplete: skrii: " + local.getCompany_id());

                        //get the Company's logo
                        DocumentReference dc = db.collection("companies").document(local.getCompany_id());
                        dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        CompanyModel cp = document.toObject(CompanyModel.class);
                                        Glide.with(context)
                                                .load(cp.getUrl_profile())
                                                .into(logo);
                                        Log.d(TAG, "onComplete: dropdop: " + cp.getUrl_profile());
                                    }
                                }
                            }
                        });

                        name.setText(local.getNome_estabelecimento());

                    }
                }

            }
        });
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<Products> list);
    }

    private void setupAdapter(final LocalActivity.FirestoreCallback firestoreCallback, String docID) {

        String id_local = myPreference.getIdLocal();
        Log.d(TAG, "setupAdapter: idaa: " + id_local);

        setData(docID, id_local);

        String cnpj = id_local.replaceFirst("-", "/");
        Log.d(TAG, "setupAdapter: aunn: " + cnpj);

        Query mQuery = db.collection(getString(R.string.cp)).document(docID).collection("Product").whereEqualTo("cnpj_owner", cnpj);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Products products;
                        products = document.toObject(Products.class);
                        products.setDocId(document.getId());

                        listUniversal.add(products);
                    }
                    firestoreCallback.onCallback(listUniversal);
                }
            }
        });
    }

    public void saveFav(final String idL) {
        final String userID = mAuth.getCurrentUser().getUid();
        final DocumentReference dr = db.collection("allCnpjRegistered").document(idL);
        final DocumentReference favCol = db.collection("users").document(userID).collection("feedbackLocal").document(idL);

        //check if document exist
        favCol.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    //get state
                    Log.d(TAG, "onEvent: ta existe isso");
                    favCol.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    FeedbackLocal feedbackLocal;
                                    feedbackLocal = document.toObject(FeedbackLocal.class);

                                    //check state
                                    if (feedbackLocal.getFollowState()) {
                                        heart.setImageResource(R.drawable.s2r);
                                        flw.setText("Seguindo");
                                        //removes from favorites
                                        ln.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                favCol.update("followState", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dr.update("followers", FieldValue.increment(-1));
                                                            heart.setImageResource(R.drawable.s2);
                                                            flw.setText("Seguir");
                                                            Log.d(TAG, "onComplete: fav successfully updated");

                                                            //update the number of followers
                                                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        final DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            FollowersClass fd;
                                                                            fd = document.toObject(FollowersClass.class);

                                                                            nroS.setText(String.valueOf(fd.getFollowers()));
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            Log.d(TAG, "onComplete: update failed");
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        heart.setImageResource(R.drawable.s2);
                                        flw.setText("Seguir");

                                        //add to favorites if the user clicks
                                        ln.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                favCol.update("followState", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dr.update("followers", FieldValue.increment(1));
                                                            heart.setImageResource(R.drawable.s2r);
                                                            flw.setText("Seguindo");
                                                            Log.d(TAG, "onComplete: fav successfully updated");

                                                            //update the number of followers
                                                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        final DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            FollowersClass fd;
                                                                            fd = document.toObject(FollowersClass.class);

                                                                            nroS.setText(String.valueOf(fd.getFollowers()));
                                                                        }
                                                                    }
                                                                }
                                                            });
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
                    Log.d(TAG, "onEvent: ta n existe isso");
                    flw.setText("Seguir");
                    heart.setImageResource(R.drawable.s2);

                    //like the product
                    ln.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FeedbackLocal feedbackLocal = new FeedbackLocal();
                            feedbackLocal.setFollowState(true);
                            favCol.set(feedbackLocal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dr.update("followers", FieldValue.increment(1));
                                        flw.setText("Seguindo");
                                        heart.setImageResource(R.drawable.s2r);
                                        Log.d(TAG, "onComplete: Created new node");

                                        //update the number of followers
                                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    final DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        FollowersClass fd;
                                                        fd = document.toObject(FollowersClass.class);

                                                        nroS.setText(String.valueOf(fd.getFollowers()));
                                                    }
                                                }
                                            }
                                        });
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
}
