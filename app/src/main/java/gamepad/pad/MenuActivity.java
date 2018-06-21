package gamepad.pad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OffersFragment.OnFragmentInteractionListener, ActivesFragment.OnFragmentInteractionListener{

    private long _userID;
    private LocationManager _locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        _userID = intent.getLongExtra("id", -1);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.menu_contentFrame, OffersFragment.newInstance(_userID)).commit();
        getSupportActionBar().setTitle("Ofertas");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        String name = DBConnection.db(getApplicationContext()).getUserName(_userID);
        TextView vuname = findViewById(R.id.sidebar_name);
        vuname.setText(name);

        String email = DBConnection.db(getApplicationContext()).getUserEmail(_userID);
        TextView vemail = findViewById(R.id.sidebar_email);
        vemail.setText(email);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getFragmentManager();

        if (id == R.id.menu_offersItem) {
            getSupportActionBar().setTitle("Ofertas");
            fm.beginTransaction().replace(R.id.menu_contentFrame, OffersFragment.newInstance(_userID)).commit();
        } else if (id == R.id.menu_activeItem) {
            getSupportActionBar().setTitle("Activos");
            fm.beginTransaction().replace(R.id.menu_contentFrame, ActivesFragment.newInstance(_userID)).commit();
        } else if (id == R.id.menu_rentItem) {
            Intent intent = new Intent(MenuActivity.this, RentActivity.class);
            intent.putExtra("id", _userID);
            startActivityForResult(intent, 1);
        } else if (id == R.id.menu_accountItem) {

        } else if (id == R.id.menu_historyItem) {

        } else if (id == R.id.menu_settingsItem) {

        } else if (id == R.id.menu_exitItem) {
            SharedPreferences loginToken = getSharedPreferences("loginToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginToken.edit();
            editor.remove("login");
            editor.commit();

            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Se ha puesto un nuevo juego en alquiler.
                GameListing gameListing = (GameListing) data.getSerializableExtra("game");

                //gameListing es el juego
                DBConnection.db(getApplicationContext()).addListing(gameListing);

                FragmentManager fm = getFragmentManager();
                getSupportActionBar().setTitle("Activos");
                fm.beginTransaction().replace(R.id.menu_contentFrame, ActivesFragment.newInstance(_userID)).commit();

            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
