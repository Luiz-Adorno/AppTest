package miips.com.Carts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import miips.com.Adapters.CartsListAdapter.RecyclerViewCartsListAdapter;
import miips.com.Models.LocalCart;
import miips.com.R;


public class CartListFragment extends Fragment {
    private static final String TAG = CartsListActivity.class.getName();
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private RelativeLayout offCart;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartlist, container, false);

        recyclerView = view.findViewById(R.id.recycler_messages);
        db = FirebaseFirestore.getInstance();
        offCart = view.findViewById(R.id.off_cart);
        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser(mAuth.getCurrentUser());

        return view;
    }
    private interface FirestoreCallback {
        void onCallback(ArrayList<LocalCart> lista);
    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user != null) {
            setData(new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<LocalCart> lista) {
                    if (lista.isEmpty()){
                        offCart.setVisibility(View.VISIBLE);
                    }else{
                        setupRecycler(lista);
                    }
                }
            });

        }
    }

    private void setupRecycler(ArrayList<LocalCart> listss) {
        recyclerView.setHasFixedSize(true);
        RecyclerViewCartsListAdapter adapter = new RecyclerViewCartsListAdapter(getContext(), listss);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    private void setData(final CartListFragment.FirestoreCallback firestoreCallback) {
        final ArrayList<LocalCart> list = new ArrayList<>();

        String userUid = mAuth.getCurrentUser().getUid();
        CollectionReference collectionReference = db.collection("users").document(userUid).collection("cart");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LocalCart localCart;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "onComplete: alive: "+ document.getData());
                        localCart = document.toObject(LocalCart.class);
                        localCart.setIdLocal(document.getId());
                        list.add(localCart);
                    }
                    firestoreCallback.onCallback(list);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
