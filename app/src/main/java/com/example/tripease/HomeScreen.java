package com.example.tripease;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);

        if (savedInstanceState == null) {
            loadFragment(new ChatFragment(), true, false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment currentFragment = getCurrentFragment();
                boolean isNavigatingBack = false;

                if (itemId == R.id.chat_menu) {
                    if (!(currentFragment instanceof ChatFragment)) {
                        isNavigatingBack = currentFragment != null && currentFragment instanceof HistoryFragment;
                        loadFragment(new ChatFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.history_menu) {
                    if (!(currentFragment instanceof HistoryFragment)) {
                        isNavigatingBack = currentFragment != null && currentFragment instanceof ProfileFragment;
                        loadFragment(new HistoryFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.profile_menu) {
                    if (!(currentFragment instanceof ProfileFragment)) {
                        loadFragment(new ProfileFragment(), false, false);
                    }
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment newFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        Fragment currentFragment = getCurrentFragment();
        loadFragment(newFragment, currentFragment, isAppInitialized, isNavigatingBack);
    }

    private void loadFragment(Fragment newFragment, Fragment currentFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            if (currentFragment instanceof ProfileFragment && newFragment instanceof ChatFragment) {
                // Navigating back from ProfileFragment to ChatFragment
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right, R.anim.slide_out_left
                );
            } else if (isNavigatingBack) {
                // General back navigation animations
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right, R.anim.slide_out_left
                );
            } else {
                // Forward navigation animations
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right
                );
            }
        }

        if (isAppInitialized && fragmentManager.findFragmentById(R.id.frameLayout) == null) {
            fragmentTransaction.add(R.id.frameLayout, newFragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, newFragment);
        }

        fragmentTransaction.commit();
    }


    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    }
}
