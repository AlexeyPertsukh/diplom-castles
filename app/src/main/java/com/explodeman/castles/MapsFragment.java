package com.explodeman.castles;

import static com.explodeman.castles.constants.IConst.START_GEO_V;
import static com.explodeman.castles.constants.IConst.START_GEO_V1;
import static com.explodeman.castles.constants.IConst.START_GEO_ZOOM;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.explodeman.castles.models.Castle;
import com.explodeman.castles.utils.IBasicDialog;
import com.explodeman.castles.utils.IToast;
import com.explodeman.castles.utils.UtilAssets;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MapsFragment extends Fragment implements IToast, IBasicDialog,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        IOnBackProcessed,
        OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;

    private GoogleMap googleMap;

    private IMain iMain;
    private ArrayList<Castle> castles;
    private ClusterManager<Castle> clusterManager;
    private IChangeFragment iChangeFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iMain = (IMain) context;
        iChangeFragment = (IChangeFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void clickOnMarker(Marker marker) {
        Castle castle = getCastleByName(marker.getTitle());
        iChangeFragment.showCastleDetailFragment(castle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.clear();
        castles = new ArrayList<>(iMain.getCastles());
        clusterManager = new ClusterManager<>(getContext(), googleMap);

        googleMap.setOnCameraIdleListener(clusterManager);
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new CustomInfoWindowAdapter());
        clusterManager.getMarkerCollection().setOnInfoWindowClickListener(this::clickOnMarker);

        clusterManager.addItems(castles);
        clusterManager.setAnimation(true);

        LatLng start = new LatLng(START_GEO_V, START_GEO_V1);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, START_GEO_ZOOM));
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

//        https://developers.google.cn/maps/documentation/android-sdk/location?hl=ru
        initMyLocation(googleMap);
    }

    private void initMyLocation(GoogleMap googleMap) {
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        enableMyLocation(googleMap);

    }

    private void enableMyLocation(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //
        return false;
    }

    @Override
    public void onBackProcessed() {
//        String title = "";
//        String message = "Выйти из приложения?";
//        showBasicDialogTwoBtn(getContext(), title, message, this::exit);
        getActivity().finish();
    }

    private void exit(DialogInterface dialogInterface, int i) {
        getActivity().finish();
    }


    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = getLayoutInflater().inflate(R.layout.custom_castle_info_window, null);

            ImageView iv = view.findViewById(R.id.ivMarkerInfo);
            TextView tv = view.findViewById(R.id.tvMarkerName);

            Castle castle = getCastleByName(marker.getTitle());
            if (castle != null) {
                String directory = castle.getDirectory();
                String id = castle.getId();
                String way = String.format("info/%s/%s/", directory, id);

                iv.setImageDrawable(UtilAssets.getImageDrawable(getContext(), way + id + ".jpg"));
                tv.setText(castle.getName());
            }
            return view;
        }
    }

    private Castle getCastleByName(String name) {
        for (Castle castle : castles) {
            if (castle.getName().equals(name)) {
                return castle;
            }
        }
        return null;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_maps, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_maps_type) {
            changeMapsType();
        } else if (id == R.id.menu_maps_random) {
            randomCastle();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMapsType() {
        if(googleMap.getMapType() == MAP_TYPE_NORMAL) {
            googleMap.setMapType(MAP_TYPE_HYBRID);
        } else {
            googleMap.setMapType(MAP_TYPE_NORMAL);
        }
    }

    private int numRandomCastle;

    private void randomCastle() {
        if (castles.isEmpty()) {
            return;
        }

        MarkerManager mm = clusterManager.getMarkerManager();
        Collection<Castle> cs = clusterManager.getAlgorithm().getItems();
        ArrayList<Castle> castles = new ArrayList<>(cs);
        Random random = new Random();
        int num = numRandomCastle;
        while (num == numRandomCastle) {
            num = random.nextInt(castles.size());
        }
        numRandomCastle = num;

        Castle randomCastle = castles.get(numRandomCastle);
        goToMarker(randomCastle);
    }

    public void goToMarker(Castle castle) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(castle.getPosition(), 10f);
        googleMap.moveCamera(cu);

        Collection<Marker> markers = clusterManager.getMarkerCollection().getMarkers();

        List<Marker> list = new ArrayList<>(markers);

        for (Marker marker : list) {
            if (marker.getTitle().equals(castle.getName())) {
                marker.showInfoWindow();
            }
        }
    }

}