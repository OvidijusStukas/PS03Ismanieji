package edu.stukas.ovidijus.terrarea.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import edu.stukas.ovidijus.terrarea.R;
import edu.stukas.ovidijus.terrarea.data.Territory;
import edu.stukas.ovidijus.terrarea.database.TerrareaDatabaseHelper;
import edu.stukas.ovidijus.terrarea.fragment.TerritoryListFragment;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapTerritoryHandler;
import edu.stukas.ovidijus.terrarea.util.ConversionUtil;
import edu.stukas.ovidijus.terrarea.util.TerrareaSettingKeys;

/**
 * @author Ovidijus Stukas
 */

public class TerritoryAdapter extends RecyclerView.Adapter<TerritoryAdapter.TerritoryViewHolder> {

    private TerrareaDatabaseHelper databaseHelper;
    private TerritoryListFragment dialogFragment;

    private List<Territory> allTerritories;
    private List<Territory> filteredTerritories;
    private String areaTemplate;
    private String perimeterTemplate;
    private String areaNotSaved;
    private String perimeterNotSaved;

    private Context context;

    public TerritoryAdapter(Context context, TerritoryListFragment territoryListFragment) {
        this.context = context;
        this.dialogFragment = territoryListFragment;

        databaseHelper = new TerrareaDatabaseHelper(context);
        allTerritories = databaseHelper.getTerritories();
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
        if (areaNotSaved == null)
            areaNotSaved = itemView.getResources().getString(R.string.territory_item_area_not_saved);
        if (perimeterNotSaved == null)
            perimeterNotSaved = itemView.getResources().getString(R.string.territory_item_perimeter_not_saved);

        return new TerritoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TerritoryViewHolder holder, int position) {
        Territory territory = filteredTerritories.get(position);
        holder.setTerritory(territory);
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

    class TerritoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Territory territory;
        private TextView name;
        private TextView area;
        private TextView perimeter;

        TerritoryViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            name = (TextView) view.findViewById(R.id.territory_name);
            area = (TextView) view.findViewById(R.id.territory_area);
            perimeter = (TextView) view.findViewById(R.id.territory_perimeter);
        }

        void setTerritory(Territory territory)
        {
            this.territory = territory;

            double areaAmount = territory.getArea();
            double perimeterAmount = territory.getPerimeter();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int areaTypePosition = Integer.parseInt(preferences.getString(TerrareaSettingKeys.SETTING_KEY_AREA_POSITION, "0"));
            int perimeterTypePosition = Integer.parseInt(preferences.getString(TerrareaSettingKeys.SETTING_KEY_PERIMETER_POSITION, "0"));

            String areaMeasurement = context.getResources().getStringArray(R.array.area_measurement_types)[areaTypePosition];
            String perimeterMeasurement = context.getResources().getStringArray(R.array.perimeter_measurement_types)[perimeterTypePosition];

            String areaText = areaNotSaved;
            String perimeterText = perimeterNotSaved;

            if (areaAmount > 0) {
                areaText = String.format(areaTemplate, ConversionUtil.ConvertArea(areaTypePosition, territory.getArea()));
                areaText += String.format(" %s.", areaMeasurement);
            }

            if (perimeterAmount > 0) {
                perimeterText = String.format(perimeterTemplate, ConversionUtil.ConvertPerimeter(perimeterTypePosition, territory.getPerimeter()));
                perimeterText += String.format(" %s.", perimeterMeasurement);
            }

            name.setText(territory.getName());
            area.setText(areaText);
            perimeter.setText(perimeterText);
        }

        @Override
        public void onClick(View view) {
            List<LatLng> positions = territory.getPositions();
            if (positions == null || positions.size() < 3) {
                String message = String.format(context.getString(R.string.territory_no_positions), territory.getName());
                Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            GoogleMapTerritoryHandler.instance.focusTerritory(territory);
            dialogFragment.dismiss();
        }

        @Override
        public boolean onLongClick(View view) {
            int filteredPosition = getAdapterPosition();
            Territory territory = filteredTerritories.get(filteredPosition);

            int position = allTerritories.indexOf(territory);

            allTerritories.remove(position);
            notifyItemRemoved(position);

            String message = String.format(context.getString(R.string.territory_removed), territory.getName());
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                    .show();

            databaseHelper.deleteTerritory(territory.getId());

            return true;
        }
    }
}
