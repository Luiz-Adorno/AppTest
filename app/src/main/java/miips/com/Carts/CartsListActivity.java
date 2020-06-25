package miips.com.Carts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import miips.com.Home.HomeActivity;
import miips.com.Profile.AccountActivity;
import miips.com.R;
import miips.com.Search.SearchActivity;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.MyPreference;
import miips.com.Utils.SectionPagerAdapter;

public class CartsListActivity extends AppCompatActivity {
    private static final String TAG = CartsListActivity.class.getName();
    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private MyPreference myPreference;
    private FirebaseFirestore db;
    private static final int ACTIVITY_NUMBER = 2;
    private Context context = CartsListActivity.this;
    public static boolean activeM = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = findViewById(R.id.recycler_messages);
        db = FirebaseFirestore.getInstance();
        myPreference = new MyPreference(context);

        mAuth = FirebaseAuth.getInstance();

        setupBottomNavigationViewEx();
    }

    // Responsible for adding the tabs with liked tab
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CartListFragment()); //index = 0
        adapter.addFragment(new OrdersFragment()); //index = 1

        ViewPager viewPager = findViewById(R.id.containerViewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsFragment);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.cart);
        tabLayout.getTabAt(0).setText("Carrinho");
        tabLayout.getTabAt(1).setIcon(R.drawable.checkorder);
        tabLayout.getTabAt(1).setText("Encomenda");
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
                            Intent intent3 = new Intent(context, CartsListActivity.class); //ActivityNumber = 2
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
        setupViewPager();
        activeM = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeM = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
