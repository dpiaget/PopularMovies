package course.example.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daniel on 2017-04-01.
 */

public class Movie implements Parcelable{


    private String poster_path;
    private String overview;
    private String original_title;
    private String vote_average;
    private String release_date;

    public Movie (String poster_path, String overview ,
                  String original_title, String vote_average ,
                  String release_date )
    {
        this.poster_path = poster_path;
        this.original_title = original_title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public String getPoster_path(){
        return poster_path;
    }

    public String getOriginal_title(){
        return original_title;
    }

    public String getOverview(){
        return overview;
    }

    public String getVote_average(){
        return vote_average;
    }

    public String getRelease_date(){
        return release_date;
    }

    public Movie (Parcel in){
        original_title = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Check to see that the
    //public String toString() { return versionName + "--" + versionNumber + "--" + image; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(vote_average);
        parcel.writeString(poster_path);
        parcel.writeString(release_date);
    }


    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

}


