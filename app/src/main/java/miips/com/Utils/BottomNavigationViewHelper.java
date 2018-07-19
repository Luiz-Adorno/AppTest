package miips.com.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.transition.Transition;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import miips.com.Home.HomeActivity;
import miips.com.Messages.MessagesActivity;
import miips.com.R;
import miips.com.Profile.ProfileActivity;
import miips.com.Search.SearchActivity;

import static miips.com.Home.HomeActivity.activeH;
import static miips.com.Messages.MessagesActivity.activeM;
import static miips.com.Profile.ProfileActivity.activeP;
import static miips.com.Search.SearchActivity.activeS;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ic_home:
                        if(activeH == true){
                            break;
                        }
                        else {
                            Intent intent1 = new Intent(context, HomeActivity.class); //ActivityNumber = 0
                            context.startActivity(intent1);
                            break;
                        }

                    case R.id.ic_search:
                        if(activeS == true){
                            break;
                        }
                        else {
                        Intent intent2 = new Intent(context,SearchActivity.class); //ActivityNumber = 1
                        context.startActivity(intent2);
                        break;
                        }

                    case R.id.ic_messages:
                        if(activeM == true){
                            break;
                        }
                        else {
                            Intent intent3 = new Intent(context, MessagesActivity.class); //ActivityNumber = 2
                            context.startActivity(intent3);
                            break;
                        }

                    case R.id.ic_profile:
                        if(activeP == true){
                            break;
                        }
                        else {
                            Intent intent4 = new Intent(context, ProfileActivity.class); //ActivityNumber = 3
                            context.startActivity(intent4);
                            break;
                        }


                }

                return false;
            }
        });

    }
}
