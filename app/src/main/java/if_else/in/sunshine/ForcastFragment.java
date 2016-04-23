package if_else.in.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForcastFragment extends Fragment {

    public ForcastFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("ForcastFragment ", " :: onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e("ForcastFragment ", " :: onCreateOptionsMenu");
        inflater.inflate(R.menu.forcastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e("ForcastFragment ", " :: onOptionsItemSelected");
        int id = item.getItemId();
        Log.e("ForcastFragment ", " :: onOptionsItemSelected id- " + id);
        if(id==R.id.action_refresh){
            Log.e("JSON Reply","calling internet");
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Creating some dummy data for the ListView
        Log.e("ForcastFragment ", " :: onCreateView");
        String[] data = {
                "Sat 4/23 - Sunny - 31/17",
                "Sun 4/24 - Foggy - 21/8",
                "Mon 4/25 - Cloudy - 22/17",
                "Tue 4/26 - Rainy - 18/11",
                "Wed 4/27 - Foggy - 21/10",
                "Thu 4/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Fri 4/29 - Sunny - 20/7"
        };
        List<String> weekForecast = Arrays.asList(data);

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listViewForcast = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listViewForcast.setAdapter(mForecastAdapter);
        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            Log.e("ForcastFragment","doInBackground");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?zip=764001&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=" + "36cc2818c60730faa360f70e8b608310";
                URL url = new URL(baseUrl.concat(apiKey));

                Log.e("ForcastFragment","doInBackground URL" + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.e("ForcastFragment "," JSON Reply :: "+ forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}