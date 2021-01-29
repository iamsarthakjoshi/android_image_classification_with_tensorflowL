package com.sarthak.my_object_identifier;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sarthak.my_object_identifier.Fragments.CameraFragment;
import com.sarthak.my_object_identifier.Fragments.LiveCamFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.Toast;

public class ClassificationActivity extends AppCompatActivity {

    // declare fragment objects
    CameraFragment cameraFragment;
    LiveCamFragment liveCamFragment;

    BottomNavigationView bottomNavigationView;

    public Bundle data; // data to hold userOptions from MainActivity
    public boolean quant; // quantization true or false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        // initialize quant and dunble data
        quant = getIntent().getBooleanExtra("isQuant", false);
        data = new Bundle();
        data.putBoolean("quant", quant);

        try{
            // declare camera and liveCam fragment objects
            cameraFragment = new CameraFragment();
            liveCamFragment = new LiveCamFragment();

            // send bundle "data" to the fragments
            cameraFragment.setArguments(data);
            liveCamFragment.setArguments(data);

            // default navigation to cameraFragment
            navigateToFragment(cameraFragment);

            // switch between cam and liveCam fragments through bottomNavigationView button
            bottomNavigationView = findViewById(R.id.bottomNavView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.camera_item:
                            navigateToFragment(cameraFragment);
                            Toast.makeText(ClassificationActivity.this, "Image Classification", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.live_item:
                            navigateToFragment(liveCamFragment);
                            Toast.makeText(ClassificationActivity.this, "Live Image Classification", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
        } catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //setUpNavigation();
    }

    // navigate to given fragments
    public void navigateToFragment(Fragment fragment){
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        } catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // for future reference - please ignore
//    public void setUpNavigation(){
//        bottomNavigationView =findViewById(R.id.bottomNavMenu);
//        // NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.NavHostFragment);
//        //NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
//
//        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return Navigation.findNavController(this, R.id.NavHostFragment).navigateUp();
//    }

}
