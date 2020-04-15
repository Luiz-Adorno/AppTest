package miips.com.Home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import miips.com.Adapters.HorizontalAdRecyclerViewAdapter;
import miips.com.Adapters.HomeAdapters.VerticalRecyclerViewAdapter;
import miips.com.Models.HomeModels.AdModel;
import miips.com.Models.HomeModels.HorizontalModel;
import miips.com.Models.HomeModels.VerticalModel;
import miips.com.Models.Products.Products;
import miips.com.Models.User;
import miips.com.R;
import miips.com.Register.google.RegisterActivityGoogle;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProductFragment extends Fragment {

    private FirebaseFirestore db;
    private ProductFragment context;
    private FirebaseAuth mAuth;
    private static User settings;
    private String userID;
    private static Products products;
    private ProgressBar mProgressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView verticalRecyclerView, verticalRecyclerView2, verticalRecyclerView3, verticalRecyclerView4;
    private RecyclerView adRecyclerView, adRecyclerView2, adRecyclerView3, adRecyclerView4, adRecyclerView5;
    private VerticalRecyclerViewAdapter adapter, adapter2, adapter3, adapter4;
    private HorizontalAdRecyclerViewAdapter adAdapter, adAdapter2, adAdapter3, adAdapter4, adAdapter5;
    private ArrayList<VerticalModel> arrayListVertical = new ArrayList<>(), arrayListVertical2 = new ArrayList<>(), arrayListVertical3 = new ArrayList<>(), arrayListVertical4 = new ArrayList<>();

    private ArrayList<AdModel> listAd = new ArrayList<>();
    private ArrayList<AdModel> listAd2 = new ArrayList<>();
    private ArrayList<AdModel> listAd3 = new ArrayList<>();
    private ArrayList<AdModel> listAd4 = new ArrayList<>();
    private ArrayList<AdModel> listAd5 = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtcs, container, false);
        mProgressBar = view.findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view1);
        verticalRecyclerView2 = view.findViewById(R.id.vertical_recycler_view2);
        verticalRecyclerView3 = view.findViewById(R.id.vertical_recycler_view3);
        verticalRecyclerView4 = view.findViewById(R.id.vertical_recycler_view4);
        adRecyclerView = view.findViewById(R.id.ad_rc);
        adRecyclerView2 = view.findViewById(R.id.ad_rc2);
        adRecyclerView3 = view.findViewById(R.id.ad_rc3);
        adRecyclerView4 = view.findViewById(R.id.ad_rc4);
        adRecyclerView5 = view.findViewById(R.id.ad_rc5);

        //make scrollview continue scroll like recyclerview
        adRecyclerView.setNestedScrollingEnabled(false);
        adRecyclerView2.setNestedScrollingEnabled(false);
        adRecyclerView3.setNestedScrollingEnabled(false);
        adRecyclerView4.setNestedScrollingEnabled(false);
        adRecyclerView5.setNestedScrollingEnabled(false);
        verticalRecyclerView.setNestedScrollingEnabled(false);
        verticalRecyclerView2.setNestedScrollingEnabled(false);
        verticalRecyclerView3.setNestedScrollingEnabled(false);
        verticalRecyclerView4.setNestedScrollingEnabled(false);

        context = ProductFragment.this;
        mAuth = FirebaseAuth.getInstance();

        //retrieve user information from database Firestore
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        setupRecyclerViewAd();
        setupRecyclerViewAd2();
        setupRecyclerViewAd3();
        setupRecyclerViewAd4();
        setupRecyclerViewAd5();

        getUserData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        arrayListVertical.clear();
                        arrayListVertical2.clear();
                        arrayListVertical3.clear();
                        //  arrayListVertical4.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        getUserData();
                    }
                }, 2000);
            }
        });

        return view;
    }

    private void getUserLocation(User user) {
        String state = user.getState();
        String city = user.getCity();
        String docID = city + "-" + state;
        Log.d(TAG, docID);

        getUnissexShoes(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd();
                    setSectionOne(list, "Calçado", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: teniz vazio unissex");
                }
            }
        }, docID);

        getFemaleShoes(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd();
                    setSectionOne(list, "Calçado", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: teniz vazio female");
                }
            }
        }, docID);

        getMaleShoes(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd();
                    setSectionOne(list, "Calçado", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: teniz vazio male");
                }
            }
        }, docID);

        getVestFemale(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd2();
                    setSectionTwo(list, "Vestuário", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: vest vazio female");
                }
            }
        }, docID);

        getVestMas(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd2();
                    Log.d(TAG, "onCallback: coronga: "+ list.size());
                    setSectionTwo(list, "Vestuário", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: vest vazio masc");
                }
            }
        }, docID);

        getVestUnissex(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd2();
                    setSectionTwo(list, "Vestuário", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: vest vazio unissex");
                }

            }
        }, docID);

        getUnissexAccessories(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio unissex");
                }

            }
        }, docID);

        getFemaleAccessories(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio fem");
                }

            }
        }, docID);

        getMaleAccessories(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSunglassUni(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSunglassfemale(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSunglassMale(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd3();
                    setSectionThree(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getJewelryU(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getJewelryF(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getJewelryM(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSemiJewelryU(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSemiJewelryF(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getSemiJewelryM(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getBijouU(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getBijouF(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);

        getBijouM(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Products> list) {
                if (!list.isEmpty()) {
                    setDataAd4();
                    setDataAd5();
                    setSectionFour(list, "Acessórios", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                }else{
                    Log.d(TAG, "onCallback: acess vazio male");
                }

            }
        }, docID);
    }

    private void getUnissexShoes(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Calçado")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "unisexshoes => " + document.getData());
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

    private void getFemaleShoes(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Calçado")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "femaleshoes => " + document.getData());
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

    private void getMaleShoes(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Calçado")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "maleshoes => " + document.getData());
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

    private void getVestFemale(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Vestuário")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
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

    private void getVestMas(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Vestuário")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    products = document.toObject(Products.class);
                                    products.setDocId(document.getId());
                                    listUniversal.add(products);
                                }
                            Log.d(TAG, "onComplete: getVestMas: "+ listUniversal.size());
                                firestoreCallback.onCallback(listUniversal);

                        } else {
                            Toast.makeText(getContext(), "Conexão fraca, tentar novamente mais tarde", Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private void getVestUnissex(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Vestuário")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "vesuniss => " + document.getData());
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

    private void getUnissexAccessories(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Acessórios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "accessorioesunis => " + document.getData());
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

    private void getFemaleAccessories(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Acessórios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "accefemale => " + document.getData());
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

    private void getMaleAccessories(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Acessórios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "maleacesse => " + document.getData());
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

    private void getSunglassUni(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Óculos de sol")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getSunglassfemale(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Óculos de sol")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getSunglassMale(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Óculos de sol")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getJewelryU(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Joias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getJewelryF(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Joias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getJewelryM(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Joias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getSemiJewelryU(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Semijoia")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getSemiJewelryF(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Semijoia")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getSemiJewelryM(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Semijoia")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getBijouU(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Unissex").whereEqualTo("product_category", "Bijuteria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getBijouF(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Feminino").whereEqualTo("product_category", "Bijuteria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private void getBijouM(final FirestoreCallback firestoreCallback, String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");

        productRef.whereEqualTo("gender", "Masculino").whereEqualTo("product_category", "Bijuteria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> listUniversal = new ArrayList<>();
                            mProgressBar.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
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

    private interface FirestoreCallback {
        void onCallback(ArrayList<Products> list);
    }

    private void getUserData() {
        mProgressBar.setVisibility(View.VISIBLE);
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
                        getUserLocation(settings);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    //////////////////////////////--SetupSection--\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setupRecyclerVertical1() {
        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical);
        adapter.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }

    private void setupRecyclerVertical2() {
        verticalRecyclerView2.setHasFixedSize(true);
        verticalRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter2 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical2);
        adapter2.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView2.setAdapter(adapter2);

    }

    private void setupRecyclerVertical3() {
        verticalRecyclerView3.setHasFixedSize(true);
        verticalRecyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter3 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical3);
        adapter3.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView3.setAdapter(adapter3);

    }

    private void setupRecyclerVertical4() {
        verticalRecyclerView4.setHasFixedSize(true);
        verticalRecyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter4 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical4);
        adapter4.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView4.setAdapter(adapter4);

    }

    ////////////////////////////////////-- SetSection --\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setSectionOne(ArrayList<Products> listUniversal, String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

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
        setupRecyclerVertical1();
    }

    private void setSectionTwo(ArrayList<Products> listUniversal, String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listUniversal) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical2.add(verticalModel);
        setupRecyclerVertical2();
    }

    private void setSectionThree(ArrayList<Products> listUniversal, String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listUniversal) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical3.add(verticalModel);
        setupRecyclerVertical3();
    }

    private void setSectionFour(ArrayList<Products> listUniversal, String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listUniversal) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical4.add(verticalModel);
        setupRecyclerVertical4();
    }

    //////////////////////////////--SetupAd--\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private void setupRecyclerViewAd() {
        listAd = new ArrayList<>();

        adRecyclerView.setHasFixedSize(true);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd);
        //make vertical adapter for recyclerview
        adRecyclerView.setAdapter(adAdapter);
    }

    private void setupRecyclerViewAd2() {
        listAd2 = new ArrayList<>();

        adRecyclerView2.setHasFixedSize(true);
        adRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter2 = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd2);
        //make vertical adapter for recyclerview
        adRecyclerView2.setAdapter(adAdapter2);
    }

    private void setupRecyclerViewAd3() {
        listAd3 = new ArrayList<>();

        adRecyclerView3.setHasFixedSize(true);
        adRecyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter3 = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd3);
        //make vertical adapter for recyclerview
        adRecyclerView3.setAdapter(adAdapter3);
    }

    private void setupRecyclerViewAd4() {
        listAd4 = new ArrayList<>();

        adRecyclerView4.setHasFixedSize(true);
        adRecyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter4 = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd4);
        //make vertical adapter for recyclerview
        adRecyclerView4.setAdapter(adAdapter4);
    }

    private void setupRecyclerViewAd5() {
        listAd5 = new ArrayList<>();

        adRecyclerView5.setHasFixedSize(true);
        adRecyclerView5.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter5 = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd5);
        //make vertical adapter for recyclerview
        adRecyclerView5.setAdapter(adAdapter5);
    }

    /////////////////////////////--SetAdd--\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setDataAd() {
        AdModel adModel = new AdModel();

        for (int i = 0; i <= 5; i++) {
            adModel.setAd_one(R.drawable.ad_one);
            adModel.setAd_two(R.drawable.ad_two);

            listAd.add(adModel);
        }
        adAdapter.notifyDataSetChanged();
    }

    private void setDataAd2() {
        AdModel adModel2 = new AdModel();

        for (int i = 0; i <= 1; i++) {
            adModel2.setAd_one(R.drawable.ad_one);
            adModel2.setAd_two(R.drawable.ad_two);

            listAd2.add(adModel2);
        }
        adAdapter2.notifyDataSetChanged();
    }

    private void setDataAd3() {
        AdModel adModel3 = new AdModel();

        for (int i = 0; i <= 1; i++) {
            adModel3.setAd_one(R.drawable.ad_one);
            adModel3.setAd_two(R.drawable.ad_two);

            listAd3.add(adModel3);
        }
        adAdapter3.notifyDataSetChanged();
    }

    private void setDataAd4() {
        AdModel adModel4 = new AdModel();

        for (int i = 0; i <= 1; i++) {
            adModel4.setAd_one(R.drawable.ad_one);
            adModel4.setAd_two(R.drawable.ad_two);

            listAd4.add(adModel4);
        }
        adAdapter4.notifyDataSetChanged();
    }

    private void setDataAd5() {
        AdModel adModel4 = new AdModel();

        for (int i = 0; i <= 1; i++) {
            adModel4.setAd_one(R.drawable.ad_one);
            adModel4.setAd_two(R.drawable.ad_two);

            listAd5.add(adModel4);
        }
        adAdapter5.notifyDataSetChanged();
    }


    private void verifyUser() {
        DocumentReference docRef = db.collection(context.getString(R.string.dbname_user)).document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Log.d(TAG, "no documentss");
                        Intent intent = new Intent(getContext(), RegisterActivityGoogle.class);
                        startActivity(intent);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
