package br.com.ericksprengel.android.movies.models;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import br.com.ericksprengel.android.movies.R;

@Parcel
public class Movie {

	@SerializedName("overview")
	String overview;

	@SerializedName("original_language")
	String originalLanguage;

	@SerializedName("original_title")
	String originalTitle;

	@SerializedName("video")
	boolean video;

	@SerializedName("title")
	String title;

	@SerializedName("genre_ids")
	List<Integer> genreIds;

	@SerializedName("poster_path")
	String posterPath;

	@SerializedName("backdrop_path")
	String backdropPath;

	@SerializedName("release_date")
	String releaseDate;

	@SerializedName("vote_average")
	double voteAverage;

	@SerializedName("popularity")
	double popularity;

	@SerializedName("id")
	int id;

	@SerializedName("adult")
	boolean adult;

	@SerializedName("vote_count")
	protected int voteCount;

	public void setOverview(String overview){
		this.overview = overview;
	}

	public String getOverview(){
		return overview;
	}

	public void setOriginalLanguage(String originalLanguage){
		this.originalLanguage = originalLanguage;
	}

	public String getOriginalLanguage(){
		return originalLanguage;
	}

	public void setOriginalTitle(String originalTitle){
		this.originalTitle = originalTitle;
	}

	public String getOriginalTitle(){
		return originalTitle;
	}

	public void setVideo(boolean video){
		this.video = video;
	}

	public boolean isVideo(){
		return video;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setGenreIds(List<Integer> genreIds){
		this.genreIds = genreIds;
	}

	public List<Integer> getGenreIds(){
		return genreIds;
	}

	public void setPosterPath(String posterPath){
		this.posterPath = posterPath;
	}

	public String getPosterPath(){
		return posterPath;
	}

	public void setBackdropPath(String backdropPath){
		this.backdropPath = backdropPath;
	}

	public String getBackdropPath(){
		return backdropPath;
	}

	public void setReleaseDate(String releaseDate){
		this.releaseDate = releaseDate;
	}

	public String getReleaseDate(){
		return releaseDate;
	}

	public void setVoteAverage(double voteAverage){
		this.voteAverage = voteAverage;
	}

	public double getVoteAverage(){
		return voteAverage;
	}

	public void setPopularity(double popularity){
		this.popularity = popularity;
	}

	public double getPopularity(){
		return popularity;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setAdult(boolean adult){
		this.adult = adult;
	}

	public boolean isAdult(){
		return adult;
	}

	public void setVoteCount(int voteCount){
		this.voteCount = voteCount;
	}

	public int getVoteCount(){
		return voteCount;
	}

	@Override
 	public String toString(){
		return 
			"Movie{" +
			"overview = '" + overview + '\'' + 
			",original_language = '" + originalLanguage + '\'' + 
			",original_title = '" + originalTitle + '\'' + 
			",video = '" + video + '\'' + 
			",title = '" + title + '\'' + 
			",genre_ids = '" + genreIds + '\'' + 
			",poster_path = '" + posterPath + '\'' + 
			",backdrop_path = '" + backdropPath + '\'' + 
			",release_date = '" + releaseDate + '\'' + 
			",vote_average = '" + voteAverage + '\'' + 
			",popularity = '" + popularity + '\'' + 
			",id = '" + id + '\'' + 
			",adult = '" + adult + '\'' + 
			",vote_count = '" + voteCount + '\'' + 
			"}";
		}

    public String getPosterThumbnailUrl(Resources res) {
        // Example: http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        return res.getString(R.string.the_movie_db_api_image_url,
                res.getString(R.string.the_movie_db_api_poster_thumbnail_size),
                this.getPosterPath());
    }

    public String getPosterUrl(Resources res) {
        // Example: http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        return res.getString(R.string.the_movie_db_api_image_url,
                res.getString(R.string.the_movie_db_api_poster_size),
                this.getPosterPath());
    }

    public String getReleaseYear() {
        return releaseDate.substring(0, 4);
    }
}