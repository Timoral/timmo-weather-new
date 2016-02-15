package xyz.timmo.weather;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class CityDialogFragment extends DialogFragment {

    private static final String CLASS_NAME = "CityDialogFragment";

    private AutoCompleteTextView autoCompleteTextViewCity;

    private ArrayList<String> arrayListCSV;
    private Context context;
    private String city;
    private String[] cities;

    @Override
    @NonNull
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_city, null);

        context = view.getContext();

        autoCompleteTextViewCity = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewCity);

        new GetCitiesFromCSVTask().execute();

        autoCompleteTextViewCity.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                cities));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .setTitle(R.string.action_city)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        city = autoCompleteTextViewCity.getText().toString();
                        new SetCityTask().execute();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CityDialogFragment.this.getDialog().dismiss();
                    }
                })
                .create();
    }


    private class GetCitiesFromCSVTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            arrayListCSV = new ArrayList<>();

            InputStream inputStream = null;
            try {
                URL url = new URL("http://openweathermap.org/help/city_list.txt");
                URLConnection urlConnection = url.openConnection();
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                Log.e(CLASS_NAME, e.toString());
            }

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String csvLine;
                    while ((csvLine = reader.readLine()) != null) {
                        String[] row = csvLine.split("\t");
                        arrayListCSV.add(Arrays.toString(row));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Error in reading CSV file: " + ex);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //noinspection ThrowFromFinallyBlock
                        throw new RuntimeException("Error while closing input stream: " + e);
                    }
                }
            }
            cities = new String[arrayListCSV.size()];
            cities = arrayListCSV.toArray(cities);
            return cities;
        }

    }

    private class SetCityTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            City.SetCity(context, city);
            return null;
        }
    }
}