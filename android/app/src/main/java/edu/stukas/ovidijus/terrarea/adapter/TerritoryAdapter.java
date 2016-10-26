package edu.stukas.ovidijus.terrarea.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.stukas.ovidijus.terrarea.R;
import edu.stukas.ovidijus.terrarea.data.Territory;

/**
 * @author Ovidijus Stukas
 */

public class TerritoryAdapter extends RecyclerView.Adapter<TerritoryAdapter.TerritoryViewHolder> {

    private List<Territory> allTerritories;
    private List<Territory> filteredTerritories;
    private String areaTemplate;
    private String perimeterTemplate;

    public TerritoryAdapter() {
        allTerritories = new ArrayList<>();
        allTerritories.add(new Territory("Testas", 15.3, 4.2));
        filteredTerritories = allTerritories;
    }

    @Override
    public TerritoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.territory_list_item, parent, false);

        if (areaTemplate == null)
            areaTemplate = itemView.getResources().getString(R.string.territory_item_area_text);
        if (perimeterTemplate == null)
            perimeterTemplate = itemView.getResources().getString(R.string.territory_item_perimeter_text);

        return new TerritoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TerritoryViewHolder holder, int position) {
        Territory territory = filteredTerritories.get(position);
        holder.textName.setText(territory.getName());
        holder.textArea.setText(String.format(areaTemplate, territory.getArea()));
        holder.textPerimeter.setText(String.format(perimeterTemplate, territory.getPerimeter()));
    }

    @Override
    public int getItemCount() {
        return filteredTerritories.size();
    }

    public List<Territory> getAllTerritories() {
        return allTerritories;
    }

    public void setFilteredTerritories(List<Territory> filteredTerritories) {
        this.filteredTerritories = filteredTerritories;
        notifyDataSetChanged();
    }

    static class TerritoryViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textArea;
        TextView textPerimeter;

        TerritoryViewHolder(View view) {
            super(view);

            textName = (TextView) view.findViewById(R.id.territory_name);
            textArea = (TextView) view.findViewById(R.id.territory_area);
            textPerimeter = (TextView) view.findViewById(R.id.territory_perimeter);
        }
    }
}
