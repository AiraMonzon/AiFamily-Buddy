package monzonation.aifamilybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    NavigationBarView bottomNavigationView;
    private long mLastClickTime =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        loadFragment(new DashboardFragment());

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.dashboard) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fragment = new DashboardFragment();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");
        } else if (itemId == R.id.income) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fragment = new IncomeFragment();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Income");
        } else if (itemId == R.id.expenses) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fragment = new ExpensesFragment();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Expenses");
        }
        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }

    void loadFragment(Fragment fragment) {
        //to attach fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

}