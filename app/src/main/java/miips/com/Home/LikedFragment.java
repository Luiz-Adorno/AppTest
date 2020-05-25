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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LikedFragment extends Fragment {

    private FirebaseFirestore db;
    private LikedFragment context;
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
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        mProgressBar = view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        relOff = view.findViewById(R.id.off_layout);

        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view);
        adRecyclerView = view.findViewById(R.id.ad_rc);
        //make scrollview continue scroll like recyclerview
        adRecyclerView.setNestedScrollingEnabled(false);
        verticalRecyclerView.setNestedScrollingEnabled(false);

        context = LikedFragment.this;
        mAuth = FirebaseAuth.getInstance();

        //retrieve user information from database Firestore
        db = FirebaseFirestore.getInstance();
        setupRecyclerViewAd();
        setDataAd();

        //first check if the array is already filled
        if (arrayListVertical.isEmpty()) {
            userID = mAuth.getCurrentUser().getUid();
            getDataViaUser();
        } else {
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
                userID = mAuth.getCurrentUser().getUid();
                getDataViaUser();

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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query localLiked = db.collection("users").document(userID).collection("feedbackLocal").whereEqualTo("followState", true);

        localLiked.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        final Local local = new Local();
                        local.setCnpj(document.getId());

                        getData(new LikedFragment.FirestoreCallback() {
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
                                    Log.d(TAG, "onCallback: empty");
                                }
                            }
                        }, docID, gender, local.getCnpj());
                    }
                }
            }
        });
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<Products> list);
    }

    private void getData(final LikedFragment.FirestoreCallback firestoreCallback, String docId, final String gender, String cnpj) {
        final ArrayList<Products> listUniversal = new ArrayList<>();

        final CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");
        final String cnpj_var = cnpj.replaceFirst("-", "/");
        Log.d(TAG, "tst: cnpjj:" + cnpj_var);
        productRef.whereEqualTo("cnpj_owner", cnpj_var).whereEqualTo("gender", gender).whereEqualTo("state", true).orderBy("data_cadastro", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            products = new Products();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "akonnn => " + document.getData());
                                products = document.toObject(Products.class);
                                products.setDocId(document.getId());
                                listUniversal.add(products);
                            }
                            productRef.whereEqualTo("cnpj_owner", cnpj_var).whereEqualTo("gender", "Unissex").whereEqualTo("state", true).orderBy("data_cadastro", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                mProgressBar.setVisibility(View.GONE);

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + "akonnn => " + document.getData());
                                                    products = document.toObject(Products.class);
                                                    products.setDocId(document.getId());
                                                    listUniversal.add(products);
                                                }
                                                firestoreCallback.onCallback(listUniversal);

                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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