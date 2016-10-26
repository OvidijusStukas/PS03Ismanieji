package edu.stukas.ovidijus.terrarea.fragment;

import android.app.SearchManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import edu.stukas.ovidijus.terrarea.R;
import edu.stukas.ovidijus.terrarea.adapter.TerritoryAdapter;
import edu.stukas.ovidijus.terrarea.data.Territory;

import static android.content.Context.SEARCH_SERVICE;

/**
 * @author Ovidijus Stukas
 */
public class TerritoryListFragment extends TerrareaDialogFragment implements Filterable {

    private TerritoryAdapter territoryAdapter;
    private TerritoryListFilter territoryListFilter;

    public TerritoryListFragment() {
        super(R.layout.territory_layout, R.string.drawer_territories);
        territoryAdapter = new TerritoryAdapter();
        territoryListFilter = new TerritoryListFilter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.territory_list_menu, menu);

        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(SEARCH_SERVICE);

        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search_territory));

        SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete)
                searchView.findViewById(R.id.search_src_text);
        searchTextView.setTextColor(Color.WHITE);

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        if (rootView != null)
        {
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.territory_recycle_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(territoryAdapter);
        }

        return rootView;
    }

    @Override
    public Filter getFilter() {
        return territoryListFilter;
    }

    private class TerritoryListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            List<Territory> filteredTerritories = new ArrayList<>();
            for (Territory territory : territoryAdapter.getAllTerritories()) {
                if (territory.getName().replace("\\s+", "").toLowerCase()
                        .contains(charSequence.toString().toLowerCase())) {

                    filteredTerritories.add(territory);
                }
            }

            filterResults.values = filteredTerritories;
            filterResults.count = filteredTerritories.size();

            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.values != null && filterResults.values instanceof List)
                territoryAdapter.setFilteredTerritories((List<Territory>) filterResults.values);
        }
    }
}
