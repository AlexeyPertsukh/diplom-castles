package com.explodeman.castles;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.explodeman.castles.constants.IConst;
import com.explodeman.castles.models.Castle;
import com.explodeman.castles.roomdb.App;
import com.explodeman.castles.roomdb.CastleDao;
import com.explodeman.castles.roomdb.CastleDb;
import com.explodeman.castles.utils.IBasicDialog;
import com.explodeman.castles.utils.IToast;
import com.explodeman.castles.utils.PermissionUtils;
import com.explodeman.castles.utils.UtilPicture;
import com.explodeman.castles.utils.UtilKeyboard;
import com.explodeman.castles.utils.UtilOther;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IMain, IToast, IConst, IChangeFragment, IBasicDialog {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private LocationManager locationManager;

    private CastleDetailFragment castleDetailFragment;
    private CastlesFragment castlesFragment;
    private MapsFragment mapsFragment;
    private PictureFragment pictureFragment;

    private CastleDao castleDao;

    private ArrayList<Castle> castles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initNavigationView();
        initRoom();
        initFragments();
        initPermissions();

        castles = readCastlesFromDatabase();

        if (isBackStackEmpty()) {
            showMapsFragment();
        }

    }

    private void initRoom() {
        CastleDb castleDb = App.getInstance().getCastleDb();
        castleDao = castleDb.castleDao();
    }

    private void initNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::navigationItemSelect);
    }

    private void initFragments() {
        castlesFragment = new CastlesFragment();
        castleDetailFragment = new CastleDetailFragment();
        mapsFragment = new MapsFragment();
        pictureFragment = new PictureFragment();
    }

    private boolean navigationItemSelect(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_menu_maps) {
            showMapsFragment();
        } else if (itemId == R.id.nav_menu_castles) {
            showCastlesFragment();
        } else if (itemId == R.id.nav_menu_send_mail) {
            sendMail();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initPermissions() {
        final int LOCATION_PERMISSION_REQUEST_CODE = 99;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission((AppCompatActivity) this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void showMapsFragment() {
        showMapsFragment(null);
    }

    @Override
    public void showMapsFragment(Castle castle) {
        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(KEY_FRAGMENT_MAPS);
        if (checkTopMapsFragment()) {
            return;
        }

        UtilKeyboard.hideKeyboard(this);

        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, mapsFragment, KEY_FRAGMENT_MAPS)
                    .addToBackStack(KEY_FRAGMENT_MAPS)
                    .commit();
        } else {
            fm.popBackStackImmediate(KEY_FRAGMENT_MAPS, 0);
            if (castle != null) {
                mapsFragment.goToMarker(castle);
            }
        }

        setActionBarTittleDefault();
    }


    private void showCastlesFragment() {
        if (checkTopCastlesFragment()) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(KEY_FRAGMENT_CASTLES);

        ArrayList<Castle> castles = readCastlesFromDatabase();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_CASTLES, castles);

        if (fragment == null) {

            castlesFragment.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.fragment_container, castlesFragment, KEY_FRAGMENT_CASTLES)
                    .addToBackStack(KEY_FRAGMENT_CASTLES)
                    .commit();
        } else {
//            castlesFragment.setDefaultAdapter();
            fm.popBackStackImmediate(KEY_FRAGMENT_CASTLES, 0);
        }

        setActionBarTittleDefault();
    }


    @Override
    public void showCastleDetailFragment(Castle castle) {
        UtilKeyboard.hideKeyboard(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(KEY_FRAGMENT_CASTLE_DETAIL);

        Bundle args = new Bundle();
        args.putParcelable(KEY_ONE_CASTLE, castle);

        if (fragment == null) {
            castleDetailFragment.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.fragment_container, castleDetailFragment, KEY_FRAGMENT_CASTLE_DETAIL)
                    .addToBackStack(KEY_FRAGMENT_CASTLE_DETAIL)
                    .commit();
        } else {

            castleDetailFragment.setInfo(castle);
//            fm.popBackStackImmediate(KEY_FRAGMENT_CASTLE_DETAIL, 0);
            fm.beginTransaction()
                    .replace(R.id.fragment_container, castleDetailFragment, KEY_FRAGMENT_CASTLE_DETAIL)
//                    .addToBackStack(KEY_FRAGMENT_CASTLE_DETAIL)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void showPictureFragment(Bitmap bitmap) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(KEY_FRAGMENT_PICTURE);

        Bundle args = new Bundle();

        byte[] bytes = UtilPicture.bitmapToBytes(bitmap);

        args.putByteArray(KEY_BYTES_PICTURE, bytes);
        pictureFragment.setArguments(args);

        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, pictureFragment, KEY_FRAGMENT_PICTURE)
                    .addToBackStack(KEY_FRAGMENT_PICTURE)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, pictureFragment, KEY_FRAGMENT_PICTURE)
                    .addToBackStack(KEY_FRAGMENT_PICTURE)
                    .commit();
        }
    }

    public boolean checkTopMapsFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() == 0) {
            return false;
        }
        return fragments.get(fragments.size() - 1) instanceof MapsFragment;
    }


    public boolean checkTopCastlesFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() == 0) {
            return false;
        }
        return fragments.get(fragments.size() - 1) instanceof CastlesFragment;
    }

    public boolean checkTopCastleDetailFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() == 0) {
            return false;
        }
        return fragments.get(fragments.size() - 1) instanceof CastleDetailFragment;
    }

    public IOnBackProcessed getTopBackProcessedFromStack() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() == 0) {
            return null;
        }
        return (IOnBackProcessed) fragments.get(fragments.size() - 1);
    }

    private boolean isBackStackEmpty() {
        return getFragmentManager().getBackStackEntryCount() == 0;
    }

    private ArrayList<Castle> readCastlesFromDatabase() {
//        String query = String.format("SELECT * FROM castle %s %s", filter.getQuery(), sort.getQuery());
        String query = "SELECT * FROM castle ORDER BY name";

        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query);
        return new ArrayList<>(castleDao.get(simpleSQLiteQuery));
    }


    private void setActionBarTittleDefault() {
        getSupportActionBar().setTitle("");
    }

    private void sendMail() {
        String to = "cpu486-blog@rambler.ru";
        String subject = "Feedback app 'Замки Украины'";
        String message = "---" +
                "\nНе удаляйте это" +
                "\nApp version: " + BuildConfig.VERSION_CODE + "/" + BuildConfig.VERSION_NAME +
                "\nDevice: " + UtilOther.getDeviceName() +
                "\nAndroid: " + UtilOther.getAndroidVersion() +
                "\n" + UtilOther.getLanguage() +
                "\n---\n\n";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //для того чтобы запросить email клиент устанавливаем тип
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Выберите email клиент :"));

    }

    @Override
    public void setActionBarTittle(String tittle) {
        getSupportActionBar().setTitle(tittle);
    }


    @Override
    public List<Castle> getCastles() {
        return castles;
    }

    @Override
    public void popBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        IOnBackProcessed iOnBackProcessed = getTopBackProcessedFromStack();
        if (iOnBackProcessed == null) {
            finish();
        } else {
            iOnBackProcessed.onBackProcessed();
        }
    }


}