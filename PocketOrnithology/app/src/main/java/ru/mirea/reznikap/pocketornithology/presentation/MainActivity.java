package ru.mirea.reznikap.pocketornithology.presentation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.RecognitionViewModel;

public class MainActivity extends BaseActivity {

    private RecognitionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.class.getSimpleName().toString(), "MainActivity created");

        setContentView(R.layout.activity_main);

        ViewModelFactory factory = new ViewModelFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(RecognitionViewModel.class);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_recognition) {
                selectedFragment = new RecognitionFragment();
            } else if (itemId == R.id.nav_journal) {
                if (viewModel.isGuest()) {
                    Toast.makeText(this, "Журнал доступен только зарегистрированным пользователям", Toast.LENGTH_SHORT).show();
                    return false;
                }
                selectedFragment = new JournalFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        bottomNav.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_recognition) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof RecognitionFragment) {
                    ((RecognitionFragment) currentFragment).reset();
                }
            }else if (item.getItemId() == R.id.nav_journal) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecognitionFragment())
                    .commit();
        }
    }
}