package com.hfad.sportsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hfad.sportsapp.global.Addresses;
import com.hfad.sportsapp.global.App;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private JSONObject currentUser;

    @Override
    public void onStart() {
        super.onStart();

        this.currentUser = App.getCurrentUser();

        System.out.println("USER: " + currentUser);

        try {
            insertName();
            insertEmail();
            insertPhoto();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_map);

        //NOTE:  Open fragment1 initially.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MapFragment());
        ft.commit();
    }

    public void insertName() throws JSONException {
        View view =  navigationView.getHeaderView(0);
        TextView nav_header_name = view.findViewById(R.id.nav_header_name);
        String firstName = currentUser.getString("firstName");
        String lastName = currentUser.getString("lastName");
        String name = firstName + " " + lastName;
        nav_header_name.setText(name);
    }

    public void insertEmail() throws JSONException {
        View view =  navigationView.getHeaderView(0);
        TextView nav_header_email = view.findViewById(R.id.nav_header_email);
        String email = currentUser.getString("email");
        nav_header_email.setText(email);
    }

    public void insertPhoto() {
        View view =  navigationView.getHeaderView(0);
        ImageView nav_header_photo = view.findViewById(R.id.nav_header_photo);

        if(!currentUser.isNull("photoId")) {
            Uri photoUrl = Uri.parse(Addresses.GET_AVATAR);
            Picasso picasso = App.getUserAvatar();
            picasso
                    .load(photoUrl)
                    .resize(250, 250)
                    .into(nav_header_photo);
        }
    }

//    public static Drawable LoadImageFromWebOperations(String url) {
//        try {
//            InputStream is = (InputStream) new URL(url).getContent();
//            Drawable d = Drawable.createFromStream(is, "src name");
//            return d;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_map) {
            fragment = new MapFragment();
        } else if (id == R.id.nav_current_events) {

        } else if (id == R.id.nav_memories) {

        } else if (id == R.id.nav_editProfile) {
            goToActivity(EditProfileActivity.class, currentUser);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            goToActivity(LoginActivity.class);
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToActivity(Class<?> cls, JSONObject... currentUser) {
        Intent intent = new Intent(this, cls);
        if(currentUser.length == 1) {
            intent.putExtra("currentUser", currentUser[0].toString());
        }
        startActivity(intent);
    }
}
