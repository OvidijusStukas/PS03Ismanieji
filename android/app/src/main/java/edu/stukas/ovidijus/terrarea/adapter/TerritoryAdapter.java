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

    private List<Territory> territories;

    public TerritoryAdapter() {
        territories = new ArrayList<>();
        territories.add(new Territory("Testas", 15.3, 4.2));
    }

    @Override
    public TerritoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.territory_list_item, parent, false);

        return new TerritoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TerritoryViewHolder holder, int position) {
        Territory territory = territories.get(position);
        holder.textName.setText(territory.getName());
    }

    @Override
    public int getItemCount() {
        return territories.size();
    }

    static class TerritoryViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        TerritoryViewHolder(View view) {
            super(view);

            textName = (TextView) view.findViewById(R.id.territory_name);
        }
    }
}
