package vn.edu.tlu.nhom7.calendar.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.home.CalendarFragment;
import vn.edu.tlu.nhom7.calendar.activity.task.CreateTaskActivity;
import vn.edu.tlu.nhom7.calendar.activity.task.TaskFragment;
import vn.edu.tlu.nhom7.calendar.activity.user.UserProfile;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        intentTask();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.nav_home){
                    loadFragment(new CalendarFragment(), false);
                } else if (itemId == R.id.nav_taskManager) {
                    loadFragment(new TaskFragment(), false);
                } else if (itemId == R.id.nav_userProfile) {
//                    loadFragment(new UserProfileFragment(), false); Activity -> Fragment

                    Intent intent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }
    private void loadFragment (Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }

    private void intentTask() {
        if (getIntent().hasExtra("key_task") && getIntent().getStringExtra("key_task").equals("task")) {
            loadFragment(new TaskFragment(), true);
        } else {
            loadFragment(new CalendarFragment(), true);
        }

    }
}