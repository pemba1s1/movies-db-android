package com.example.moviesdb.viewmodel;


import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesdb.BuildConfig;
import com.example.moviesdb.model.Movie;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private final MutableLiveData<List<Movie>> mFavoriteMovieList;
    private final OkHttpClient client = new OkHttpClient();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Favourite");
    private Integer page = 1;
    private Integer totalPages = 1;

    public MovieViewModel() {
        mSearch = new MutableLiveData<>("Superman");
        mMovieList = new MutableLiveData<>();
        mMovieId = new MutableLiveData<>();
        mMovieDetail = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        mFetchedMovieList = new MutableLiveData<>();
        mFavoriteMovieList = new MutableLiveData<>();
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
    public LiveData<List<Movie>> getFavoriteMovieList() {
        return mFavoriteMovieList;
    }
    public void setSearchQuery (String query) {
        if (query.isEmpty()) {
            return;
        }
        mSearch.setValue(query);
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
                        int totalResults = json.getInt("totalResults");
                        totalPages = (int) Math.ceil(totalResults / 10.0);
                        Gson gson = new Gson();
                        Type movieListType = new TypeToken<List<Movie>>() {}.getType();
                        List<Movie> movies = gson.fromJson(json.getString("Search"), movieListType);
                        isLoadingLiveData.postValue(false);
                        callback.onSuccess(movies);
                    } catch (JSONException e) {
                        isLoadingLiveData.postValue(false);
                        callback.onFailure(e);
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
                mFetchedMovieList.postValue(new Pair<>(null, false));
            }
        });

    }

    public void loadMore() {
        page++;
        if (page > totalPages) {
            return;
        }
        String apiKey = BuildConfig.API_KEY;
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&type=movie&s=" + mSearch.getValue()+"&page=" + page;
        makeRequest(url, new MovieListCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                List<Movie> currentMovies = mMovieList.getValue();
                assert currentMovies != null;
                currentMovies.addAll(movies);
                mMovieList.postValue(currentMovies);
                mFetchedMovieList.postValue(new Pair<>(movies, false));
            }
            @Override
            public void onFailure(Exception e) {
                mFetchedMovieList.postValue(new Pair<>(null, false));
            }
        });
    }
    public void fetchFavoriteMovies() {
        List<Movie> favMovieList = new ArrayList<>();
        collectionReference.get().addOnSuccessListener(task -> {
            for (QueryDocumentSnapshot document : task) {
                System.out.println(document);
                Movie movie = document.toObject(Movie.class);
                favMovieList.add(movie);
            }
            mFavoriteMovieList.postValue(favMovieList);
        });
    }
}
