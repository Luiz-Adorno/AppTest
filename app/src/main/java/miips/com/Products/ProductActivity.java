package miips.com.Products;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import miips.com.Models.Local;
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
    private String name, price, image, description, cnpj_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context = ProductActivity.this;
        MyPreference myPreference = new MyPreference(context);
        mAuth = FirebaseAuth.getInstance();
        String idP = myPreference.getIdClick();
        db = FirebaseFirestore.getInstance();
        ImageView back_btn = findViewById(R.id.back_arrow);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.this.onBackPressed();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            getDataViaUser(idP);
        } else {
            String token = myPreference.getToken();
            getData(token, idP);
            //Log.d(TAG, "onCreate:myPrefenrece: "+idP +" e "+ token);
        }

    }

    public void initToolber(String cnpj_owner, String token){

        final TextView nameStore = findViewById(R.id.id);
        final CircleImageView circleImageView = findViewById(R.id.img_circle);
        //format string for the firestore model
        String cnpj = cnpj_owner.replaceFirst("/", "-");
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

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void initImage(String image){
        ImageView imageProduct = findViewById(R.id.profile_product);

        Picasso.get().load(image).fit().centerCrop().error(R.drawable.carregando).into(imageProduct);
    }

    public void initTexts(String name, String price, String description){
        TextView nameP = findViewById(R.id.name_product);
        TextView valor = findViewById(R.id.price);
        TextView descri = findViewById(R.id.descipt);

        nameP.setText(name);
        valor.setText(price);
        descri.setText(description);
    }

    public void getData(final String token, String idPro){
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
