package miips.com.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import miips.com.LoginActivity.LoginActivity;
import miips.com.R;
import miips.com.Utils.BottomNavigationViewHelper;
import miips.com.Utils.SectionStatePagerAdapter;
import miips.com.Utils.UniversalImageLoader;

public class ProfileActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUMBER = 3;
    private Context mContext;
    private SectionStatePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;
    private ImageView profilePhoto;

    public static boolean activeP = false;


    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = ProfileActivity.this;

        //FIREBASE GOOGLE GETUSER
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        viewPager = findViewById(R.id.containerViewPager);
        relativeLayout = findViewById(R.id.profileLay1);
        profilePhoto = findViewById(R.id.ic_profile);

        setupBottomNavigationViewEx();

        CircleImageView circleImageView = findViewById(R.id.ic_profile);

        setupSettingsList();

        setupFragments();
        //setProfileImage();
    }

//    private void setProfileImage(){
//        String imgURL ="";
//        UniversalImageLoader.setImage(imgURL, profilePhoto, null,  "https://");
//    }

    private void setupFragments(){
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile)); //fragment=0
        pagerAdapter.addFragment(new PasswordFragment(), getString(R.string.password)); //fragment=1
    }

    private void setViewPager(int fragmentNumber){
        relativeLayout.setVisibility(View.GONE);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    // Bottom Navigation view setup
    private void setupBottomNavigationViewEx() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupSettingsList(){
        ListView listViewSettings = findViewById(R.id.AccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile)); //fragment=0
        options.add(getString(R.string.password)); //fragment=1
        options.add(getString(R.string.sign_out)); //fragment=2

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listViewSettings.setAdapter(adapter);

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setViewPager(position);
                if(position == 2){
                    mAuth.signOut();
                    mGoogleSignInClient.signOut();

                    Intent intentSingOut = new Intent(mContext, LoginActivity.class);
                    startActivity(intentSingOut);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeP = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeP = false;
    }
}
