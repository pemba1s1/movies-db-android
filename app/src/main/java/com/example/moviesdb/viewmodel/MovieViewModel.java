package com.example.moviesdb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesdb.model.Movie;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<String> mSearch;
    private final MutableLiveData<List<Movie>> mMovieList;

    public MovieViewModel() {
        mSearch = new MutableLiveData<String>();
        mMovieList = new MutableLiveData<List<Movie>>();
    }

    public LiveData<String> getSearchQuery() {
        return mSearch;
    }

    public LiveData<List<Movie>> getMovieList() { return mMovieList; }

    public void setSearchQuery (String query) {
        mSearch.setValue(query);
    }

    public void setMovieList (List<Movie> movieList) {
        mMovieList.setValue(movieList);
    }
}
