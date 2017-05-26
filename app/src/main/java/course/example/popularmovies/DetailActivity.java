package course.example.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static course.example.popularmovies.R.id.container;

/**
 * Created by daniel on 2017-04-02.
 */

public class DetailActivity  extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity (intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view
     */
     public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String MOVIE_SHARE_HASHTAG = " PopularMoviesApp";

        private String mPoster;
        private String mTitle;
        private String mOverview;
        private String mVote_average;
        private String mRelease;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            ImageView movieView =((ImageView) rootView.findViewById(R.id.detail_movie_image));

            String baseUrl = "http://image.tmdb.org/t/p/w92//";
            mPoster =intent.getStringExtra("poster_path");;
            String url = baseUrl.concat(mPoster);

            Picasso.with(getContext())
                    .load(url)//.fit.centerCrop
                    .fit()
                    .into(movieView);

            mTitle = intent.getStringExtra("original_title");
            ((TextView) rootView.findViewById(R.id.title_text))
                    .setText(mTitle);

            mOverview = intent.getStringExtra("overview");
            ((TextView) rootView.findViewById(R.id.overview_text))
                    .setText(mOverview);

            mVote_average = intent.getStringExtra("vote_average");
            ((TextView) rootView.findViewById(R.id.vote_text))
                    .setText(mVote_average + "/10");


            String TempRelease = intent.getStringExtra("release_date");
            String s[] = TempRelease.split("-");
            mRelease  = s[0];
            ((TextView) rootView.findViewById(R.id.year))
                    .setText(mRelease);

            return rootView;
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            //inflate the menu; this adds items to the action bar if is present
            inflater.inflate(R.menu.detailfragment, menu);

            //Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            //Get the provider and hold onto it to set/change the share intent
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);


            //Attach an intent to this ShareActionProvider. You can update this at any time.
            //like when the user selects a new piece of data they might like to share
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }
        private Intent createShareMovieIntent(){
            Intent shareIntent = new Intent();
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mTitle + MOVIE_SHARE_HASHTAG);
            return shareIntent;
        }

    }
}
