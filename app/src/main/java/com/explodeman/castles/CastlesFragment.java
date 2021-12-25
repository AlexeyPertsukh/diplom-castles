package com.explodeman.castles;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explodeman.castles.constants.IConst;
import com.explodeman.castles.models.Castle;
import com.explodeman.castles.utils.ILog;
import com.explodeman.castles.utils.IToast;

import java.util.ArrayList;
import java.util.Locale;

public class CastlesFragment extends Fragment implements IConst, IToast, ILog,
        IOnBackProcessed {

    private SearchView searchView;

    private IMain iMain;

    private ArrayList<Castle> castles;
    private RecyclerView rvCastles;
    private CastleAdapter castleAdapter;
    private IChangeFragment iChangeFragment;

    private String searchText;

    public CastlesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printLog("CastlesFragment: onCreate");

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iChangeFragment = (IChangeFragment) context;
        iMain = (IMain) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_castles, container, false);
//        if (getArguments() != null) {
//            readArguments();
//        }

        printLog("CastlesFragment: onCreateView");

        castles = new ArrayList<>(iMain.getCastles());

        initViews(view);
        initRvCastles();
        setAdapter(castles);
        initListeners();

        setHasOptionsMenu(true);
        return view;
    }

    private void initListeners() {
        castleAdapter.setOnClickItem(this::clickCastle);
    }

    private void clickCastle(Castle castle) {
        iChangeFragment.showCastleDetailFragment(castle);
    }

    private void setAdapter(ArrayList<Castle> castles) {
        castleAdapter = new CastleAdapter(getContext(), castles);
        castleAdapter.setOnClickItem(this::clickCastle);
        rvCastles.setAdapter(castleAdapter);
    }

    private void setDefaultAdapter() {
        setAdapter(castles);
    }

    private void initRvCastles() {
        rvCastles.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCastles.setLayoutManager(layoutManager);
        rvCastles.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    private void initViews(View view) {
        rvCastles = view.findViewById(R.id.rvCastles);
    }

    private void readArguments() {
        assert getArguments() != null;
        castles = getArguments().getParcelableArrayList(KEY_CASTLES);
    }

    private ArrayList<Castle> getFilterCastles(String filter) {

        filter = filter.toLowerCase(Locale.ROOT);

        ArrayList<Castle> filterList = new ArrayList<>();
        for (Castle castle : castles) {
            if (castle.getName().toLowerCase(Locale.ROOT).contains(filter)) {
                filterList.add(castle);
            }
        }
        return filterList;
    }

    public void setSearchQuery() {
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        printLog("CastlesFragment: onCreateOptionsMenu");

        menu.clear();
        inflater.inflate(R.menu.menu_castles, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_castles_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnCloseListener(this::onCloseSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = null;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                ArrayList<Castle> list = getFilterCastles(newText);
                setAdapter(list);
                return false;
            }
        });

        if (searchText != null && !searchText.isEmpty()) {
            searchView.setQuery(searchText, false);
            searchView.setIconified(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean onCloseSearch() {
        searchText = null;
        setDefaultAdapter();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        if(id == R.id.menu_mapstype) {
//            changeMapsType();
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackProcessed() {
        if(isSearchOpen()) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);
        } else {
            iChangeFragment.showMapsFragment();
        }
    }

    private boolean isSearchOpen() {
        return searchView != null && !searchView.isIconified();
    }


}