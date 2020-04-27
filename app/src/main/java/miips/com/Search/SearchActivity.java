package miips.com.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import miips.com.Adapters.SearchAdapters.VerticalRecyclerViewSearchAdapter;
import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.Messages.MessagesActivity;
import miips.com.Models.HomeModels.HorizontalModel;
import miips.com.Models.HomeModels.VerticalModel;
import miips.com.Models.Products.Products;
import miips.com.Models.SearchModels.HorizontalSearchModel;
import miips.com.Models.SearchModels.VerticalSearchModel;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Utils.BottomNavigationViewHelper;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private Context context = SearchActivity.this;
    private static final int ACTIVITY_NUMBER = 1;
    private EditText search_edit;

    private VerticalRecyclerViewSearchAdapter adapter;
    private ArrayList<VerticalSearchModel> arrayListVertical;
    private ArrayList<HorizontalSearchModel> list = new ArrayList<>();
    private RecyclerView verticalRecyclerView;
    public static boolean activeS = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_edit = findViewById(R.id.search_edit);
        search_edit.setOnEditorActionListener(editorListener);

        verticalRecyclerView = findViewById(R.id.recycler_search);

        setupRecycler();
        setData();

        setupBottomNavigationViewEx();
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                Toast.makeText(context, "Texto: "+ search_edit.getText(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };


    private void setupRecycler() {
        arrayListVertical = new ArrayList<>();

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewSearchAdapter(context, arrayListVertical);
        adapter.setHasStableIds(true);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }


    private void setData() {
        for (int i = 1; i <= 5; i++) {

            VerticalSearchModel verticalModel = new VerticalSearchModel();
            verticalModel.setTitle("Title ");

            //set title color
            verticalModel.setColorTitle(context.getResources().getDrawable(R.drawable.title_color));

            ArrayList<HorizontalSearchModel> arrayListHorizontal = new ArrayList<>();

            for (int j = 0; j <= 5; j++) {
                HorizontalSearchModel horizontalModel = new HorizontalSearchModel();
                //set each product from db
                horizontalModel.setImage(R.drawable.ad);
                arrayListHorizontal.add(horizontalModel);
            }

            verticalModel.setArrayList(arrayListHorizontal);
            arrayListVertical.add(verticalModel);

        }
        adapter.notifyDataSetChanged();
    }

//    private void setSectionOne(String section, Drawable colorBlack, int colorString) {
//        VerticalModel verticalModel = new VerticalModel();
//        verticalModel.setTitle(section);
//
//        //set title color
//        verticalModel.setColorTitle(colorBlack);
//        verticalModel.setColorString(colorString);
//
//        ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();
//
//        for (Products model : listOne) {
//
//            HorizontalModel horizontalModel = new HorizontalModel();
//            //set each product from db
//            horizontalModel.setProductId(model.getDocId());
//            horizontalModel.setImage(model.getUrl_product());
//            //them add
//            arrayListHorizontal.add(horizontalModel);
//        }
//
//        verticalModel.setArrayList(arrayListHorizontal);
//        arrayListVertical.add(verticalModel);
//    }


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
                        Intent intent3 = new Intent(context, MessagesActivity.class); //ActivityNumber = 2
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

