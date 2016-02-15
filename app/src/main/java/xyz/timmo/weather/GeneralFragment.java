package xyz.timmo.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GeneralFragment extends Fragment {

    private static final String CLASS_NAME = "GeneralFragment";
    private static final String APP_ID = "&appid=efb7b9888b0708746bd71d6251ef709c";
    private static final String UNITS_IMPERIAL = "&units=imperial";
    private static final String UNITS_METRIC = "&units=metric";
    private static final String CURRENT_URL = "http://api.openweathermap.org/data/2.5/weather?id=";
    private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String ARRAY_WEATHER = "weather";
    private static final String ARRAY_MAIN = "main";
    private static final String ARRAY_WIND = "wind";
    private static final String ARRAY_SYS = "sys";

    private String icon, cond, temp, wind, humid, press, rise, set;

    private OnFragmentInteractionListener mListener;

    private LinearLayout linearLayout;
    private TextView textViewIcon, textViewCond, textViewTemp,
            textViewWind, textViewHumid, textViewPress,
            textViewSunrise, textViewSunset;
    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    private Context context;
    private Resources resources;

    private ArrayList<Integer> arrayListDT;
    private ArrayList<String> arrayListCond, arrayListTemp, arrayListWind;

    public GeneralFragment() {
        // Required empty public constructor
    }

//    // TODO: Rename and change types and number of parameters
//    public static GeneralFragment newInstance(String param1, String param2) {
//        GeneralFragment fragment = new GeneralFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        context = getActivity();
        resources = context.getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        textViewIcon = (TextView) view.findViewById(R.id.textViewIcon);
        textViewCond = (TextView) view.findViewById(R.id.textViewCond);
        textViewTemp = (TextView) view.findViewById(R.id.textViewTemp);
        textViewWind = (TextView) view.findViewById(R.id.textViewWind);
        textViewHumid = (TextView) view.findViewById(R.id.textViewHumid);
        textViewPress = (TextView) view.findViewById(R.id.textViewPress);
        textViewSunrise = (TextView) view.findViewById(R.id.textViewSunrise);
        textViewSunset = (TextView) view.findViewById(R.id.textViewSunset);

        textViewIcon.setTypeface(FontCache.get("font/weather_icons.ttf", context));

        arrayListDT = new ArrayList<>();
        arrayListCond = new ArrayList<>();
        arrayListTemp = new ArrayList<>();
        arrayListWind = new ArrayList<>();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, 1, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        HourlyRecyclerViewAdapter hourlyRecyclerViewAdapter = new HourlyRecyclerViewAdapter(
                context, arrayListDT, arrayListCond, arrayListTemp, arrayListWind);
        recyclerView.setAdapter(hourlyRecyclerViewAdapter);

        new GetData().execute();

//        HourlyRecyclerViewAdapter hourlyRecyclerViewAdapter =
//                new HourlyRecyclerViewAdapter(context,
//                        arrayListDT, arrayListCond, arrayListTemp, arrayListWind);
//        recyclerView.setAdapter(hourlyRecyclerViewAdapter);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, 1, false);
//        recyclerView.setLayoutManager(gridLayoutManager);

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String setIconFromID(int actualId, long sunrise, long sunset, long time) {
        int id = actualId / 100;

        long sunriseTime = new Date(sunrise).getHours();
        long sunsetTime = new Date(sunset).getHours();
        long timeNow = new Date(time).getHours();

        if (timeNow >= sunriseTime && timeNow < sunsetTime) {
            //Toast.makeText(resources, sunriseTime + "\n" + timeNow + "\n" + sunsetTime+ "\n" + "DAY", Toast.LENGTH_LONG).show();
            if (actualId == 800) {
                return resources.getString(R.string.wi_day_sunny);
            } else {
                switch (id) {
                    case 2:
                        return resources.getString(R.string.wi_day_thunderstorm);
                    case 3:
                        return resources.getString(R.string.wi_day_sprinkle);
                    case 7:
                        return resources.getString(R.string.wi_day_fog);
                    case 8:
                        return resources.getString(R.string.wi_day_cloudy);
                    case 6:
                        return resources.getString(R.string.wi_day_snow);
                    case 5:
                        return resources.getString(R.string.wi_day_rain);
                }
            }
        } else {
            //Toast.makeText(resources, sunriseTime + "\n" + timeNow + "\n" + sunsetTime+ "\n" + "NIGHT", Toast.LENGTH_LONG).show();
            if (actualId == 800) {
                return resources.getString(R.string.wi_night_clear);
            } else {
                switch (id) {
                    case 2:
                        return resources.getString(R.string.wi_night_alt_thunderstorm);
                    case 3:
                        return resources.getString(R.string.wi_night_alt_sprinkle);
                    case 7:
                        return resources.getString(R.string.wi_night_fog);
                    case 8:
                        return resources.getString(R.string.wi_night_alt_cloudy);
                    case 6:
                        return resources.getString(R.string.wi_night_alt_snow);
                    case 5:
                        return resources.getString(R.string.wi_night_alt_rain);
                }
            }
        }
        return resources.getString(R.string.wi_day_cloudy);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayout.setVisibility(View.INVISIBLE);

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            JSONHandler jsonHandler = new JSONHandler();
            // CURRENT
            String jsonStrCurr = jsonHandler.makeServiceCall(
                    CURRENT_URL + City.getCity(context) + UNITS_METRIC + APP_ID, JSONHandler.GET);
            if (jsonStrCurr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStrCurr);

                    JSONObject jsonObjectWeather = jsonObj.getJSONArray(ARRAY_WEATHER).getJSONObject(0);
                    JSONObject jsonObjectMain = jsonObj.getJSONObject(ARRAY_MAIN);
                    JSONObject jsonObjectWind = jsonObj.getJSONObject(ARRAY_WIND);
                    JSONObject jsonObjectSys = jsonObj.getJSONObject(ARRAY_SYS);

                    cond = jsonObjectWeather.getString("description").toUpperCase();
                    if (cond.equals("SKY IS CLEAR")) cond = "CLEAR SKIES";
                    temp = String.format(Locale.getDefault(), "%.2f",
                            jsonObjectMain.getDouble("temp")) + "\u2103";
                    wind = String.valueOf(jsonObjectWind.getDouble("speed")) + " mph";
                    humid = String.valueOf(jsonObjectMain.getInt("humidity")) + "%";
                    press = String.valueOf(jsonObjectMain.getInt("pressure")) + " hPa";

                    Date time = new Date();
                    time.setTime(jsonObjectSys.getLong("sunrise"));
                    rise = DateFormat.getTimeFormat(context).format(time);
                    time.setTime(jsonObjectSys.getLong("sunset"));
                    set = DateFormat.getTimeFormat(context).format(jsonObjectSys.getLong("sunset"));
                    icon = setIconFromID(jsonObjectWeather.getInt("id"),
                            jsonObjectSys.getLong("sunrise"),
                            jsonObjectSys.getLong("sunset"),
                            new Date().getTime());
                } catch (JSONException e) {
                    Log.e(CLASS_NAME, e.toString());
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            // FORECAST
            String jsonStrForc = jsonHandler.makeServiceCall(
                    FORECAST_URL + City.getCity(context) + UNITS_METRIC + APP_ID, JSONHandler.GET);
            if (jsonStrForc != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStrForc);
                    JSONArray jsonArrayList = jsonObj.getJSONArray("list");

                    arrayListDT.clear();
                    arrayListCond.clear();
                    arrayListTemp.clear();
                    arrayListWind.clear();
                    for (int i = 0; i >= jsonArrayList.length(); i++) {
                        JSONArray jsonArrayWeather = jsonArrayList.getJSONObject(i)
                                .getJSONObject(ARRAY_WEATHER).getJSONArray(ARRAY_WEATHER);
                        JSONArray jsonArrayMain = jsonArrayList.getJSONObject(i)
                                .getJSONObject(ARRAY_MAIN).getJSONArray(ARRAY_MAIN);

                        arrayListDT.add(jsonArrayList.getJSONObject(i).getInt("dt"));
                        arrayListCond.add(jsonArrayWeather.getString(0));
                        arrayListTemp.add(jsonArrayMain.getString(0));
                    }
                } catch (JSONException e) {
                    Log.e(CLASS_NAME, e.toString());
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            linearLayout.setVisibility(View.VISIBLE);

            progressDialog.dismiss();

            textViewIcon.setText(icon);
            textViewCond.setText(cond);
            textViewTemp.setText(temp);
            textViewWind.setText(wind);
            textViewHumid.setText(humid);
            textViewPress.setText(press);
            textViewSunrise.setText(rise);
            textViewSunset.setText(set);

            HourlyRecyclerViewAdapter hourlyRecyclerViewAdapter = new HourlyRecyclerViewAdapter(
                    context, arrayListDT, arrayListCond, arrayListTemp, arrayListWind);
            recyclerView.setAdapter(hourlyRecyclerViewAdapter);
        }
    }
}