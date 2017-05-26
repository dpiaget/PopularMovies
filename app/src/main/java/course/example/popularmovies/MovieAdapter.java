package course.example.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by daniel on 2017-04-02.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;

    /**
     * New custom construction use to
     *
     */
     public MovieAdapter(Activity context, List<Movie> movies ){
         super(context, 0, movies);
         mContext = context;
     }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the Movie object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_movies, parent, false);
            }

        ImageView movieView = (ImageView) convertView.findViewById(R.id.movie_image);


        if (isOnline() == true) {
            String baseUrl = "http://image.tmdb.org/t/p/w185//";
            String posterPath = movie.getPoster_path();
            String url = baseUrl.concat(posterPath);

            Picasso.with(mContext)
                    .load(url)//.fit.centerCrop
                    .fit()
                    .into(movieView);
            }
       else{
            return null;
        }

        return convertView;
        }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
