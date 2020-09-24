package com.richa.myspace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav=findViewById(R.id.bottom_nav);

        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.myspace,true);
            fragmentManager=getSupportFragmentManager();
            SpaceFragment spaceFragment=new SpaceFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,spaceFragment).commit();
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment=null;
                switch (id){
                    case R.id.myspace:
                        fragment= new SpaceFragment();
                        break;
                    case R.id.myprofile:
                        fragment= new ProfileFragment();
                        break;
                }

                if (fragment!=null){
                    fragmentManager=getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                } else {
                    Log.e(TAG,"Error in creating fragment!");
                }
            }
        });


    }
}