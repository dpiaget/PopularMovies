package course.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by daniel on 2017-04-02.
 */

public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;

    private ArrayList<Movie>  movieList =  new ArrayList<Movie>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            //movieList = new ArrayList<Movie>(Arrays.asList(resultStrs));
        }

        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    public MovieFragment(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /* Get a reference to the

        **/
        movieAdapter = new MovieAdapter(getActivity(), movieList);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);


        // Launch the
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position,long l){
                Movie movieClick = movieAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("poster_path", movieClick.getPoster_path())
                        .putExtra("original_title", movieClick.getOriginal_title())
                        .putExtra("overview", movieClick.getOverview())
                        .putExtra("vote_average", movieClick.getVote_average())
                        .putExtra("release_date", movieClick.getRelease_date());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);  // Use filter.xml from step 1
    }


    private void updateMovies(){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = pref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        movieTask.execute(sort_by);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movie[] getMovieDataFromJson(String movieResultJsonStr )
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(movieResultJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);

            // OWM returns 20 of the most popular movies from the IMDB movie database
            Movie[] resultStrs = new Movie[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {
                /* For now, using the format "Day, description, hi/low"
                String poster_path;
                String overview;
                String original_title;
                String vote_average;*/

                // Get the JSON object representing the movie
                JSONObject movie = moviesArray.getJSONObject(i);


                /*poster_path = movie.getString(OWM_POSTER_PATH);
                overview = movie.getString(OWM_OVERVIEW);
                original_title = movie.getString(OWM_ORIGINAL_TITLE);
                vote_average = movie.getString(OWM_VOTE_AVERAGE);
                */

                resultStrs[i] = new Movie (movie.getString(OWM_POSTER_PATH),
                                         movie.getString(OWM_OVERVIEW),
                                         movie.getString(OWM_ORIGINAL_TITLE),
                                         movie.getString(OWM_VOTE_AVERAGE),
                                         movie.getString(OWM_RELEASE_DATE));
            }
            return resultStrs;
        }


        //overide the indoBacground
        @Override
        public  Movie[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr;

            String api_key = "9b0c4ee2ea7ab067ce9011075192de12";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(params[0])   //params[0]
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                // Create the request to the moviedb, and open the connection
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
                movieJsonStr = buffer.toString();


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
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(Movie dayForecastStr : result) {
                    movieAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }

    }




}
