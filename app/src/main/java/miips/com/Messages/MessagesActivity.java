package miips.com.Messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import miips.com.Adapters.MessagesAdapters.RecyclerViewMessagesAdapter;
import miips.com.Home.HomeActivity;
import miips.com.LoginActivity.LoginActivity;
import miips.com.Models.MessagesModels.MessageModel;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;

public class MessagesActivity extends AppCompatActivity {
    private static final String TAG = MessagesActivity.class.getName();

    private ArrayList<MessageModel> list = new ArrayList<>();
    private RecyclerView recyclerView;

    private Toolbar toolbar;

    private static final int ACTIVITY_NUMBER = 2;
    private Context context = MessagesActivity.this;

    public static boolean activeM = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = findViewById(R.id.recycler_messages);

        //setup toolbar
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mensagens");

        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser(mAuth.getCurrentUser());


        setupBottomNavigationViewEx();
    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user != null) {
            setData();
            setupRecycler();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //set action to delete selected items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecycler() {

        recyclerView.setHasFixedSize(true);
        RecyclerViewMessagesAdapter adapter = new RecyclerViewMessagesAdapter(context, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    private void setData() {
        for (int i = 1; i <= 7; i++) {
            MessageModel message = new MessageModel();
            message.setDate("25/01/2019");
            message.setTitle("Hellow mundus");
            message.setSubtitle("\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae");

            list.add(message);
        }
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
                        Intent intent2 = new Intent(context, SearchActivity.class); //ActivityNumber = 1
                        context.startActivity(intent2);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.ic_messages:
                        if (activeM) {
                            break;
                        } else {
                            Intent intent3 = new Intent(context, MessagesActivity.class); //ActivityNumber = 2
                            context.startActivity(intent3);
                            overridePendingTransition(0, 0);
                            break;
                        }

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
        activeM = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeM = false;
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
