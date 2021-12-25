package com.explodeman.castles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.explodeman.castles.constants.IConst;
import com.explodeman.castles.models.Castle;
import com.explodeman.castles.models.FirebaseImage;
import com.explodeman.castles.models.FirebaseImageNull;
import com.explodeman.castles.utils.ILog;
import com.explodeman.castles.utils.IToast;
import com.explodeman.castles.utils.UtilAssets;
import com.explodeman.castles.utils.UtilNet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CastleDetailFragment extends Fragment implements IConst, IToast, ILog,
        IOnBackProcessed {

    private TextView tvDescription;
    private ViewPager2 vpPicture;
    private SliderAdapter sliderAdapter;
    private ProgressBar pbLoad;

    private Castle castle;
    private IMain iMain;
    private IChangeFragment iChangeFragment;

    private DatabaseReference databaseReference;
    private String GROUP_IMAGES = "images";

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    public CastleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iMain = (IMain) context;
        iChangeFragment = (IChangeFragment) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_castle_detail, container, false);
        initView(view);
        databaseReference = FirebaseDatabase.getInstance().getReference(GROUP_IMAGES);

        if (getArguments() != null) {
            loadFromArguments();
        }

        setInfo(castle);

        setHasOptionsMenu(true);
        return view;
    }

    public void setInfo(Castle castle) {

        String directory = castle.getDirectory();
        String id = castle.getId();
        String way = String.format("info/%s/%s/", directory, id);

        String description = UtilAssets.readTextFile(getContext(), way + id + ".txt");
        tvDescription.setText(description);

        setImagePlug();
        if (UtilNet.isOnline(getContext())) {
            setImagesFromFirebase(castle);
        } else {
            hideProgressBar();
        }

        iMain.setActionBarTittle(castle.getName());

    }

    private void hideProgressBar() {
        pbLoad.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        pbLoad.setVisibility(View.VISIBLE);
    }

    private void loadFromArguments() {
        castle = getArguments().getParcelable(KEY_ONE_CASTLE);
    }

    private void initView(View view) {
        tvDescription = view.findViewById(R.id.tvDescription);
        vpPicture = view.findViewById(R.id.vpPicture);
        pbLoad = view.findViewById(R.id.pbLoad);
    }

    private void setImagePlug() {
        List<FirebaseImage> list = new ArrayList<>();
        list.add(FirebaseImageNull.getInstance());
        sliderAdapter = new SliderAdapter(getContext(), list);
        vpPicture.setAdapter(sliderAdapter);
    }
    private void setImagesFromFirebase(Castle castle) {
        showProgressBar();
        String castleId = castle.getId();
        Query query = databaseReference.orderByChild("castleId").equalTo(castleId);
        query.addValueEventListener(valueEventListener);
    }

    private void goToMapsMarker() {
        iChangeFragment.showMapsFragment(castle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_castle_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        if(id == R.id.menu_castle_detail_position) {
//            goToMapsMarker();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackProcessed() {
        iMain.setActionBarTittle("");
        iMain.popBackStack();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            hideProgressBar();
            List<FirebaseImage> list = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                FirebaseImage firebaseImage = ds.getValue(FirebaseImage.class);
                list.add(firebaseImage);
            }

            if (list.isEmpty()) {
                setImagePlug();
                return;
            }

            sliderAdapter = new SliderAdapter(getContext(), list);
            sliderAdapter.setOnClickItem(this::clickItem);
            vpPicture.setAdapter(sliderAdapter);
        }

        private void clickItem(Bitmap bitmap) {
            iChangeFragment.showPictureFragment(bitmap);
        }


        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            hideProgressBar();
        }
    };

}