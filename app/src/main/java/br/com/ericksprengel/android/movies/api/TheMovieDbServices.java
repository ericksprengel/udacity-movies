package br.com.ericksprengel.android.movies.api;

import br.com.ericksprengel.android.movies.models.MovieListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TheMovieDbServices {

    String MOVIE_LIST_TYPE_POPULAR = "popular";
    String MOVIE_LIST_TYPE_TOP_RATED = "top_rated";

    @GET("movie/{list_type}")
    Call<MovieListResponse> getMovieList(@Path("list_type") String listType);

}
