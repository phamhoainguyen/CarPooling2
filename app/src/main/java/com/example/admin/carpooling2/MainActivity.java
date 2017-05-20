package com.example.admin.carpooling2;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MAIN";
    private FragmentManager fragmentManager;
    //User dang dang nhap
   public  static User currentUser;
    //control
    ImageView imgProfile;
    TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         Log.e(TAG,"onCreate");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //load hinh va ten



        txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtOrigin);
        imgProfile = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgProfile);
        if(currentUser.urlProfile != null)
            Glide.with(MainActivity.this).load(currentUser.urlProfile)
                                     .bitmapTransform(new CropCircleTransformation(MainActivity.this))
                                     .into(imgProfile);
        else
            Glide.with(MainActivity.this).load(R.drawable.default_avatar)
                .bitmapTransform(new CropCircleTransformation(MainActivity.this))
                .into(imgProfile);

        txtName.setText(currentUser.name);


        fragmentManager = getFragmentManager();

    }






    @Override
    public void onBackPressed() {
       int count = fragmentManager.getBackStackEntryCount();
        Log.e(TAG,"onBackPress1 count=" + String.valueOf(count));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.e(TAG,"drawOpen");
        } else {
            Log.e(TAG,"drawClose");
            if(count == 0)
            {
                   moveTaskToBack(true);
            }
            else
            {
                //super.onBackPressed();
                fragmentManager.popBackStack();

            }


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // chuyển đổi sang fragment search
        if (id == R.id.nav_search) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new Search()).addToBackStack(null).commit();

        }

        //chuyển sang fragment Post
        else if (id == R.id.nav_post) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new Post()).addToBackStack(null).commit();


        }
        // chuyển sang fragment Profile
        else if (id == R.id.nav_profile) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new Profile()).addToBackStack(null).commit();

        }
        else  if(id == R.id.nav_history)
        {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new History()).addToBackStack(null).commit();

        }
        else if (id == R.id.nav_log_out)
        {
            MainActivity.currentUser = null;
            FirebaseAuth.getInstance().signOut();
            getSharedPreferences("cache",MODE_PRIVATE).edit().clear().commit();
            Intent intent = new Intent(MainActivity.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

}
