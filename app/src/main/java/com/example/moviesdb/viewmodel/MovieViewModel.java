package com.example.moviesdb.viewmodel;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesdb.BuildConfig;
import com.example.moviesdb.model.Movie;
import com.example.moviesdb.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


interface MovieListCallback {
    void onSuccess(List<Movie> movies);
    void onFailure(Exception e); // Handle potential errors
}
public class MovieViewModel extends ViewModel {
    private final MutableLiveData<String> mSearch;
    private final MutableLiveData<List<Movie>> mMovieList;
    private final MutableLiveData<Pair<List<Movie>, Boolean>> mFetchedMovieList;
    private final MutableLiveData<String> mMovieId;
    private final MutableLiveData<Movie> mMovieDetail;
    private final MutableLiveData<Boolean> isLoadingLiveData;
    private final OkHttpClient client = new OkHttpClient();
    private Integer page = 1;

    public MovieViewModel() {
        mSearch = new MutableLiveData<String>("Superman");
        mMovieList = new MutableLiveData<List<Movie>>();
        mMovieId = new MutableLiveData<String>();
        mMovieDetail = new MutableLiveData<Movie>();
        isLoadingLiveData = new MutableLiveData<Boolean>();
        mFetchedMovieList = new MutableLiveData<Pair<List<Movie>, Boolean>>();
    }
    public LiveData<String> getSearchQuery() {
        return mSearch;
    }
    public LiveData<List<Movie>> getMovieList() { return mMovieList; }
    public LiveData<Pair<List<Movie>, Boolean>> getFetchedMovieList() { return mFetchedMovieList; }
    public LiveData<Movie> getMovieDetail() { return mMovieDetail; }
    public LiveData<String> getMovieId() {
        return mMovieId;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
    public void setSearchQuery (String query) {
        if (query.isEmpty()) {
            return;
        }
        mSearch.setValue(query);
    }
    public void setMovieList (List<Movie> movieList) {
        mMovieList.setValue(movieList);
    }
    public void setMovieDetail (Movie movieDetail) { mMovieDetail.setValue(movieDetail); }
    public void setMovieId (String id) {
        mMovieId.setValue(id);
    }

    private void makeRequest (String url, MovieListCallback callback) {
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue()) || Objects.requireNonNull(mSearch.getValue()).isEmpty()) {
            return;
        }
        isLoadingLiveData.postValue(true);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray jsonArray = new JSONArray(json.getString("Search"));
                        Gson gson = new Gson();
                        Type movieListType = new TypeToken<List<Movie>>() {}.getType();
                        List<Movie> movies = gson.fromJson(json.getString("Search"), movieListType);
                        isLoadingLiveData.postValue(false);
                        callback.onSuccess(movies);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void searchMovie (String searchQuery) {
        String apiKey = BuildConfig.API_KEY;
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&type=movie&s=" + searchQuery;
        page = 1;
        makeRequest(url, new MovieListCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                mMovieList.postValue(movies);
                mFetchedMovieList.postValue(new Pair<>(movies, true));
            }
            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    public void loadMore() {
        page++;
        String apiKey = BuildConfig.API_KEY;
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&type=movie&s=" + mSearch.getValue()+"&page=" + page;
        makeRequest(url, new MovieListCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                List<Movie> currentMovies = mMovieList.getValue();
                currentMovies.addAll(movies);
                mMovieList.postValue(currentMovies);
                mFetchedMovieList.postValue(new Pair<>(movies, false));
            }
            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
