package miips.com.Home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import miips.com.Adapters.HomeAdapters.VerticalRecyclerViewAdapter;
import miips.com.Adapters.HorizontalAdRecyclerViewAdapter;
import miips.com.Models.HomeModels.AdModel;
import miips.com.Models.HomeModels.HorizontalModel;
import miips.com.Models.HomeModels.VerticalModel;
import miips.com.Models.Local;
import miips.com.Models.Products.Products;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Utils.MyPreference;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class VestFragment extends Fragment {

    private FirebaseFirestore db;
    private VestFragment context;
    private FirebaseAuth mAuth;
    private String userID;
    private static User settings;
    private static Products products;
    private RelativeLayout relOff;

    public String doc_id;

    private RecyclerView verticalRecyclerView, adRecyclerView;
    private VerticalRecyclerViewAdapter adapter;
    private HorizontalAdRecyclerViewAdapter adAdapter;
    private ArrayList<VerticalModel> arrayListVertical = new ArrayList<>();
    private ArrayList<AdModel> listAd = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vest, container, false);
        mProgressBar = view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        relOff = view.findViewById(R.id.off_layout);

        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view);
        adRecyclerView = view.findViewById(R.id.ad_rc);
        //make scrollview continue scroll like recyclerview
        adRecyclerView.setNestedScrollingEnabled(false);
        verticalRecyclerView.setNestedScrollingEnabled(false);

        context = VestFragment.this;
        mAuth = FirebaseAuth.getInstance();

        //retrieve user information from database Firestore
        db = FirebaseFirestore.getInstance();

        setupRecyclerViewAd();
        setDataAd();

        //first check if the array is already filled
        if(arrayListVertical.isEmpty()){
            if (mAuth.getCurrentUser() != null) {
                userID = mAuth.getCurrentUser().getUid();
                getDataViaUser();
            } else {
                MyPreference myPreference = new MyPreference(getActivity());
                doc_id = myPreference.getToken();
                //  Log.d(TAG, "onCreateView: doc_id: "+ doc_id);
                getUserLocation(doc_id, "annonymous");

            }
        }else{
            mProgressBar.setVisibility(View.GONE);
            setupRecyclerVertical();
        }

        //show off layout
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (arrayListVertical.isEmpty()) {
                    mProgressBar.setVisibility(View.GONE);
                    relOff.setVisibility(View.VISIBLE);
                }
            }
        }, 8000);

        //call the function when user refresh the layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayListVertical.clear();

                if (mAuth.getCurrentUser() != null) {
                    userID = mAuth.getCurrentUser().getUid();
                    getDataViaUser();
                } else {
                    MyPreference myPreference = new MyPreference(getActivity());
                    doc_id = myPreference.getToken();
                    //  Log.d(TAG, "onCreateView: doc_id: "+ doc_id);
                    getUserLocation(doc_id, "annonymous");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        return view;
    }


    private void getDataViaUser() {
        settings = new User();

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
                        final String docID = city + "-" + state;

                        getUserLocation(docID, gender);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getUserLocation(final String docID, final String gender) {
        Log.d(TAG, "flag: " + docID);

        if (gender.equals("annonymous")) {
            Log.d(TAG, "flag2: ");
            final Query productRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").whereEqualTo("state",true);

            productRef
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " aa=> " + document.getData());
                                    final Local local = document.toObject(Local.class);

                                    getVest(new VestFragment.FirestoreCallback() {
                                        @Override
                                        public void onCallback(final ArrayList<Products> list) {
                                            if (!list.isEmpty()) {
                                                //after get the products object(onCallback list), get the logo of the company and than add to the list
                                                String replaced = local.getCnpj().replaceFirst("/", "-");

                                                DocumentReference localRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").document(replaced);
                                                localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            products = new Products();
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Local local = document.toObject(Local.class);
                                                                products.setUrl_product(local.getProfilePhoto());
                                                                products.setDocId(document.getId());
                                                                list.add(products);
                                                                setSectionOne(list);
                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Log.d(TAG, "onCallback: vest vazio unissex");
                                            }
                                        }
                                    }, docID, "Unissex", local.getCnpj());

                                    getVest(new VestFragment.FirestoreCallback() {
                                        @Override
                                        public void onCallback(final ArrayList<Products> list) {
                                            if (!list.isEmpty()) {
                                                //after get the products object(onCallback list), get the logo of the company and than add to the list
                                                String replaced = local.getCnpj().replaceFirst("/", "-");

                                                DocumentReference localRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").document(replaced);
                                                localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            products = new Products();
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Local local = document.toObject(Local.class);
                                                                products.setUrl_product(local.getProfilePhoto());
                                                                products.setDocId(document.getId());
                                                                list.add(products);
                                                                //get the products and
                                                                setSectionOne(list);
                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d(TAG, "onCallback: vest vazio unissex");
                                            }
                                        }
                                    }, docID, "Feminino", local.getCnpj());

                                    getVest(new VestFragment.FirestoreCallback() {
                                        @Override
                                        public void onCallback(final ArrayList<Products> list) {
                                            if (!list.isEmpty()) {
                                                //after get the products object(onCallback list), get the logo of the company and than add to the list
                                                String replaced = local.getCnpj().replaceFirst("/", "-");

                                                DocumentReference localRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").document(replaced);
                                                localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            products = new Products();
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Local local = document.toObject(Local.class);
                                                                products.setUrl_product(local.getProfilePhoto());
                                                                products.setDocId(document.getId());
                                                                list.add(products);
                                                                //get the products and
                                                                setSectionOne(list);
                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d(TAG, "onCallback: vest vazio unissex");
                                            }
                                        }
                                    }, docID, "Masculino", local.getCnpj());

                                }
                            } else {
                                Log.d(TAG, "error getting documents: ", task.getException());
                            }
                        }
                    });

        } else {
            final Query productRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").whereEqualTo("state",true);
            Log.d(TAG, "flag3: ");
            productRef
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " aa=> " + document.getData());
                                    final Local local = document.toObject(Local.class);

                                    getVest(new VestFragment.FirestoreCallback() {
                                        @Override
                                        public void onCallback(final ArrayList<Products> list) {
                                            if (!list.isEmpty()) {
                                                //after get the products object(onCallback list), get the logo of the company and than add to the list
                                                String replaced = local.getCnpj().replaceFirst("/", "-");

                                                DocumentReference localRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").document(replaced);
                                                localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            products = new Products();
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Local local = document.toObject(Local.class);
                                                                products.setUrl_product(local.getProfilePhoto());
                                                                products.setDocId(document.getId());
                                                                list.add(products);
                                                                //get the products and
                                                                setSectionOne(list);
                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d(TAG, "onCallback: vest vazio unissex");
                                            }
                                        }
                                    }, docID, gender, local.getCnpj());

                                    getVest(new VestFragment.FirestoreCallback() {
                                        @Override
                                        public void onCallback(final ArrayList<Products> list) {
                                            if (!list.isEmpty()) {
                                                //after get the products object(onCallback list), get the logo of the company and than add to the list
                                                String replaced = local.getCnpj().replaceFirst("/", "-");

                                                DocumentReference localRef = db.collection(getString(R.string.cp)).document(docID).collection("Local").document(replaced);
                                                localRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            products = new Products();
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Local local = document.toObject(Local.class);
                                                                products.setUrl_product(local.getProfilePhoto());
                                                                products.setDocId(document.getId());
                                                                list.add(products);
                                                                setSectionOne(list);
                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Log.d(TAG, "onCallback: vest vazio unissex");
                                            }
                                        }
                                    }, docID, "Unissex", local.getCnpj());
                                }
                            } else {
                                Log.d(TAG, "error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<Products> list);
    }

    private void getVest(final VestFragment.FirestoreCallback firestoreCallback, String docId, String gender, String cnpj) {
        final ArrayList<Products> listUniversal = new ArrayList<>();

        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");
        Log.d(TAG, "getVest: cnpjj:" + cnpj);
        productRef.whereEqualTo("cnpj_owner", cnpj).whereEqualTo("gender", gender).whereEqualTo("product_category", "Vestuário").whereEqualTo("state",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            products = new Products();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "vestfame => " + document.getData());
                                products = document.toObject(Products.class);
                                products.setDocId(document.getId());
                                listUniversal.add(products);
                            }
                            firestoreCallback.onCallback(listUniversal);

                        } else {
                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    ////////////////////////////////////-- SetSection --\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setSectionOne(ArrayList<Products> listUniversal) {
        mProgressBar.setVisibility(View.GONE);
        VerticalModel verticalModel = new VerticalModel();

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();
        Log.d(TAG, "universal ta assim: " + listUniversal);
        for (Products model : listUniversal) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical.add(verticalModel);
        setupRecyclerVertical();
    }

    //////////////////////////////--SetupSection--\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setupRecyclerVertical() {
        relOff.setVisibility(View.GONE);

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical);
        adapter.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }

    private void setupRecyclerViewAd() {
        listAd = new ArrayList<>();

        adRecyclerView.setHasFixedSize(true);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd);
        //make vertical adapter for recyclerview
        adRecyclerView.setAdapter(adAdapter);
    }

    private void setDataAd() {
        AdModel adModel = new AdModel();

        for (int i = 0; i <= 2; i++) {
            adModel.setAd_one(R.drawable.ad_service_one);
            adModel.setAd_two(R.drawable.ad_service_two);

            listAd.add(adModel);
        }
        adAdapter.notifyDataSetChanged();
    }
}