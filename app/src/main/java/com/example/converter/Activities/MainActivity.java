package com.example.converter.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.converter.Fragments.ConverterFragment;
import com.example.converter.Fragments.CurrentRatesFragment;
import com.example.converter.Fragments.HistoryFragment;
import com.example.converter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.Bottom_nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment(CurrentRatesFragment.newInstance());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.convert:
                    loadFragment(ConverterFragment.newInstance());
                    return true;
                case R.id.history:
                    loadFragment(HistoryFragment.newInstance());
                    return true;
                case R.id.rates:
                    loadFragment(CurrentRatesFragment.newInstance());
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ContainerForCardsOnMainScreen, fragment);
        ft.commit();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        loadFragment(ConverterFragment.newInstance());
        ConverterFragment.setDateInMillis(c.getTimeInMillis());
    }
}