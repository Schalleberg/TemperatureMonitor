package com.schalleberg.temperaturemonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dropbox.core.android.Auth;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.schalleberg.temperaturemonitor.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static Context context = null;

    public static Context getAppContext() {
        if (context == null)
        {
            throw new RuntimeException("'context' is null (will be set in onCreate()");
        }
        return context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        String accessToken = "sl.Biv7jLz9spQF4O1MFFse6K2_i7TOKblbrSgeQOyvAcWe5nN1eCplPyKCnwNw9Hm-JuLXMPhJpJ9s_ED4svNt_ZWUMniV17I-8plTqaQjlF1IYtrdjDzv4h2oQ7EsJb_6ebc_j-skTa0f";

        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken == null) {
                return;
            }
        }

        //TODO: move in service
        DropboxClientFactory.init(accessToken);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        binding.btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TempMonitorFirebaseMessagingService.getDeviceToken();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}