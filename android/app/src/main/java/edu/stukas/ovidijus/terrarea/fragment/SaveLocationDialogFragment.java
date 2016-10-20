package edu.stukas.ovidijus.terrarea.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import edu.stukas.ovidijus.terrarea.R;

/**
 * @author Ovidijus Stukas
 */

public class SaveLocationDialogFragment extends DialogFragment {

    private EditText editTerritoryName;
    private TextInputLayout layoutTerritoryName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.save_location_dialog_layout, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(R.string.save_location_dialog_title);
        toolbar.setTitleTextColor(Color.WHITE);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
        setHasOptionsMenu(true);

        editTerritoryName = (EditText) rootView.findViewById(R.id.input_territory_name);
        layoutTerritoryName = (TextInputLayout) rootView.findViewById(R.id.input_layout_territory_name);

        editTerritoryName.addTextChangedListener(new SaveLocationDialogTextWatcher(editTerritoryName));
        
        return rootView;
    }

    private void submitForm() {
        if (!isFormValid())
            return;
    }

    private boolean isFormValid() {
        if (editTerritoryName.getText().toString().trim().isEmpty()) {
            layoutTerritoryName.setError(getString(R.string.error_territory_name));
            editTerritoryName.requestFocus();
            return false;
        }

        layoutTerritoryName.setErrorEnabled(false);
        return true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.dialog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            submitForm();
            return true;
        } else if (id == android.R.id.home) {
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SaveLocationDialogTextWatcher implements TextWatcher {

        private View view;

        private SaveLocationDialogTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.input_territory_name:
                    isFormValid();
                    break;
            }
        }
    }
}
