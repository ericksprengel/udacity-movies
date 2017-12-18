package br.com.ericksprengel.android.movies.api;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.lang.annotation.Annotation;

import br.com.ericksprengel.android.movies.BuildConfig;
import br.com.ericksprengel.android.movies.R;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDbServicesBuilder {

    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    private static Retrofit mRetrofit;

    private static void initRetrofit(Context context) {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new ChuckInterceptor(context))
                .build();

        httpClient.addInterceptor(chain -> {
            Request originalRequest = chain.request();
            HttpUrl url = originalRequest.url().newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build();

            Request request = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            return chain.proceed(request);
        });

        if (BuildConfig.DEBUG) {
            // add stetho interceptor. See: chrome://inspect/#devices
            httpClient.addNetworkInterceptor(new StethoInterceptor());
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.the_movie_db_api_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

    }

    public static TheMovieDbServices build(Context context) {
        if(mRetrofit == null) {
            initRetrofit(context);
        }
        return mRetrofit.create(TheMovieDbServices.class);
    }

    public static TheMovieDbApiError parseError(retrofit2.Response response, Context context) {
        if(mRetrofit == null) {
            initRetrofit(context);
        }
        Converter<ResponseBody, TheMovieDbApiError> converter =
                mRetrofit.responseBodyConverter(TheMovieDbApiError.class, new Annotation[0]);

        TheMovieDbApiError error;

        try {
            ResponseBody body = response.errorBody();
            if(body != null) {
                error = converter.convert(body);
            } else {
                return new TheMovieDbApiError();
            }
        } catch (IOException e) {
            return new TheMovieDbApiError();
        }

        return error;
    }
}
