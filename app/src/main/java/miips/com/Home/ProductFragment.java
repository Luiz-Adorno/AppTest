package miips.com.Home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProductFragment extends Fragment {

    private FirebaseFirestore db;
    private ProductFragment context;
    private FirebaseAuth mAuth;
    private static User settings;
    private String userID;
    private static Products products;
    private ArrayList<Products> listOne = new ArrayList<>();

    private RecyclerView verticalRecyclerView, verticalRecyclerView2, verticalRecyclerView3, verticalRecyclerView4;
    private RecyclerView adRecyclerView, adRecyclerView2, adRecyclerView3, adRecyclerView4;
    private VerticalRecyclerViewAdapter adapter, adapter2, adapter3, adapter4;
    private HorizontalAdRecyclerViewAdapter adAdapter, adAdapter2, adAdapter3, adAdapter4;
    private ArrayList<VerticalModel> arrayListVertical, arrayListVertical2, arrayListVertical3, arrayListVertical4;

    private ArrayList<AdModel> listAd = new ArrayList<>();
    private ArrayList<AdModel> listAd2 = new ArrayList<>();
    private ArrayList<AdModel> listAd3 = new ArrayList<>();
    private ArrayList<AdModel> listAd4 = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtcs, container, false);
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view1);
        verticalRecyclerView2 = view.findViewById(R.id.vertical_recycler_view2);
        verticalRecyclerView3 = view.findViewById(R.id.vertical_recycler_view3);
        verticalRecyclerView4 = view.findViewById(R.id.vertical_recycler_view4);
        adRecyclerView = view.findViewById(R.id.ad_rc);
        adRecyclerView2 = view.findViewById(R.id.ad_rc2);
        adRecyclerView3 = view.findViewById(R.id.ad_rc3);
        adRecyclerView4 = view.findViewById(R.id.ad_rc4);

        //make scrollview continue scroll like recyclerview
        adRecyclerView.setNestedScrollingEnabled(false);
        adRecyclerView2.setNestedScrollingEnabled(false);
        adRecyclerView3.setNestedScrollingEnabled(false);
        adRecyclerView4.setNestedScrollingEnabled(false);
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

        getUserData();

        return view;
    }

    private void getUserLocation(User user) {
        String state = user.getState();
        String city = user.getCity();
        String docID = city + "-" + state;
        Log.d(TAG, docID);
        getProducts(docID);
    }

    private void getProducts(String docId) {
        products = new Products();
        CollectionReference productRef = db.collection(getString(R.string.cp)).document(docId).collection("Product");
        productRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "ta => " + document.getData());
                                products = document.toObject(Products.class);
                                products.setDocId(document.getId());
                                listOne.add(products);
                            }
                            // Print the name from the list....
                            for (Products model : listOne) {
                                Log.d(TAG, "lista ta: " + model.getNome_produto());
                            }

                            for (int i = 1; i <= 12; i++) {
                                if (i == 1) {
                                    setupRecyclerVertical1();
                                    setDataAd();
                                    setSectionOne("Calçados", getContext().getResources().getDrawable(R.drawable.black_draw), Color.WHITE);
                                } else if (i == 2) {
                                    setSectionOne("Vestuário", getContext().getResources().getDrawable(R.drawable.black_draw), Color.WHITE);
                                } else if (i == 3) {
                                    setSectionOne("Joalheria", getContext().getResources().getDrawable(R.drawable.black_draw), Color.WHITE);
                                } else if (i == 4) {
                                    setSectionOne("Acessórios", getContext().getResources().getDrawable(R.drawable.black_draw), Color.WHITE);
                                } else if (i == 5) {
                                    setupRecyclerVertical2();
                                    setDataAd2();
                                    setSectionTwo("Perfumaria", getContext().getResources().getDrawable(R.drawable.rainbow_draw), Color.WHITE);
                                } else if (i == 6) {
                                    setSectionTwo("Cosméticos", getContext().getResources().getDrawable(R.drawable.rainbow_draw), Color.WHITE);
                                } else if (i == 7) {
                                    setSectionTwo("Farmaceutico", getContext().getResources().getDrawable(R.drawable.rainbow_draw), Color.WHITE);
                                } else if (i == 8) {
                                    setupRecyclerViewAd3();
                                    setDataAd3();
                                    setupRecyclerVertical3();
                                    setSectionThree("Livros", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                                } else if (i == 9) {
                                    setSectionThree("Papelaria", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);
                                } else if (i == 10) {
                                    setupRecyclerViewAd4();
                                    setDataAd4();
                                    setupRecyclerVertical4();
                                    setSectionFour("Eletrônicos", getContext().getResources().getDrawable(R.drawable.white_draw), Color.BLACK);

                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getUserData() {
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
        arrayListVertical = new ArrayList<>();

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical);
        adapter.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }

    private void setupRecyclerVertical2() {
        arrayListVertical2 = new ArrayList<>();

        verticalRecyclerView2.setHasFixedSize(true);
        verticalRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter2 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical2);
        adapter2.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView2.setAdapter(adapter2);

    }

    private void setupRecyclerVertical3() {
        arrayListVertical3 = new ArrayList<>();

        verticalRecyclerView3.setHasFixedSize(true);
        verticalRecyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter3 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical3);
        adapter3.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView3.setAdapter(adapter3);

    }

    private void setupRecyclerVertical4() {
        arrayListVertical4 = new ArrayList<>();

        verticalRecyclerView4.setHasFixedSize(true);
        verticalRecyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter4 = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical4);
        adapter4.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView4.setAdapter(adapter4);

    }

    ////////////////////////////////////-- SetSection --\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setSectionOne(String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listOne) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical.add(verticalModel);
    }

    private void setSectionTwo(String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listOne) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical2.add(verticalModel);
    }

    private void setSectionThree(String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listOne) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical3.add(verticalModel);
    }

    private void setSectionFour(String section, Drawable colorBlack, int colorString) {
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle(section);

        //set title color
        verticalModel.setColorTitle(colorBlack);
        verticalModel.setColorString(colorString);

        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

        for (Products model : listOne) {

            HorizontalModel horizontalModel = new HorizontalModel();
            //set each product from db
            horizontalModel.setProductId(model.getDocId());
            horizontalModel.setImage(model.getUrl_product());
            //them add
            arrayListHorizontal.add(horizontalModel);
        }

        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical4.add(verticalModel);
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

    private void setupRecyclerViewAd4(){
        listAd4 = new ArrayList<>();

        adRecyclerView4.setHasFixedSize(true);
        adRecyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter4 = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd4);
        //make vertical adapter for recyclerview
        adRecyclerView4.setAdapter(adAdapter4);
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
}
