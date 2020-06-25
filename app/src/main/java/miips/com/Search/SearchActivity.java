package miips.com.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import miips.com.Adapters.SearchAdapters.PostViewHolder;
import miips.com.Home.HomeActivity;
import miips.com.Carts.CartsListActivity;
import miips.com.Models.Post;
import miips.com.Models.User;
import miips.com.Products.ProductActivity;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.MyPreference;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;
    private static final String TAG = "SearchActivity";
    private static User user;
    private static final int ACTIVITY_NUMBER = 1;
    public String doc_id;
    private static final int NUM_COLUMNS = 2;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    public static boolean activeS = false;
    private FirestorePagingAdapter<Post, PostViewHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText search_edit = findViewById(R.id.search_edit);
        mProgressBar = findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        context = SearchActivity.this;
        mRecyclerView = findViewById(R.id.recyclerView);

        // Init mRecyclerView
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        callData();

        setupBottomNavigationViewEx();
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

                            setupAdapter(docID);

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
            setupAdapter(doc_id);

        }
    }


    private void setupAdapter( String docID) {
        Query mQuery = db.collection(getString(R.string.cp)).document(docID).collection("Product").whereEqualTo("state",true);

        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(8)
                .build();

        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Post post;
                        post = snapshot.toObject(Post.class);
                        post.setId(snapshot.getId());
                        return post;
                    }
                })
                .build();

        // Instantiate Paging Adapter
        mAdapter = new FirestorePagingAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.layout_grid_item, parent, false);
                return new PostViewHolder(view, context);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int i, @NonNull final Post post) {
                // Bind to ViewHolder
                viewHolder.bind(post);
                //Set the action in each product clicked
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyPreference myPreference = new MyPreference(context);
                        myPreference.setIDCLICK(post.getId());

                        //go go product activity
                        v.getContext().startActivity(new Intent(v.getContext(), ProductActivity.class));
                    }
                });
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("MainActivity", e.getMessage());
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;

                    case LOADED:
                        mProgressBar.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        Toast.makeText(
                                getApplicationContext(),
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show();

                        mProgressBar.setVisibility(View.GONE);
                        break;

                    case FINISHED:
                        mProgressBar.setVisibility(View.GONE);
                        break;
                }
            }

        };

        // Finally Set the Adapter to mRecyclerView
        mRecyclerView.setAdapter(mAdapter);

    }

    // Bottom Navigation view setup
    private void setupBottomNavigationViewEx() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ActivityNumber = 0
                        context.startActivity(intent1);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.ic_search:
                        if (activeS == true) {
                            break;
                        } else {
                            break;
                        }

                    case R.id.ic_messages:
                        Intent intent3 = new Intent(context, CartsListActivity.class); //ActivityNumber = 2
                        context.startActivity(intent3);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.ic_profile:
                        Intent intent4 = new Intent(context, AccountActivity.class); //ActivityNumber = 3
                        context.startActivity(intent4);
                        overridePendingTransition(0, 0);
                        break;


                }

                return false;
            }
        });

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeS = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeS = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

}

