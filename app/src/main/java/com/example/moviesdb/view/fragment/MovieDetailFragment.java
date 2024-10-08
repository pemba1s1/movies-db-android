package com.example.moviesdb.view.fragment;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moviesdb.BuildConfig;
import com.example.moviesdb.databinding.FragmentMovieDetailBinding;
import com.example.moviesdb.model.Movie;
import com.example.moviesdb.viewmodel.MovieViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailFragment} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {
    private interface IsFavoriteCallback {
        void onSuccess(boolean isFavorite, DocumentReference ref);
    }
    FragmentMovieDetailBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Favourite");
    private final OkHttpClient client = new OkHttpClient();
    MovieViewModel viewModel;
    private Movie movieDetail;
    private void addFavorite (Movie movie) {
        collectionReference.add(movie).addOnSuccessListener( documentReference -> binding.favBtn.setOnClickListener(v -> removeFavorite(documentReference)));
        binding.favBtn.setText("Remove from Favourites");
    }
    private void removeFavorite(DocumentReference ref) {
        ref.delete();
        binding.favBtn.setText("Add to Favourites");
        binding.favBtn.setOnClickListener(v -> addFavorite(this.movieDetail));
    }
    private void isFavorite(String id, IsFavoriteCallback callback) {
        collectionReference.whereEqualTo("imdbID", id).get().addOnSuccessListener(task -> {
            if (!task.isEmpty()) {
                DocumentReference ref = task.getDocuments().get(0).getReference();
                callback.onSuccess(true, ref);
            } else {
                callback.onSuccess(false, null);
            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMovieDetailBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.detailLayout.setVisibility(View.GONE);
        viewModel.getMovieId().observe(getViewLifecycleOwner(), this::getMovieDetails);

        viewModel.getMovieDetail().observe(getViewLifecycleOwner(), movieDetails -> {
            if (movieDetails == null) {
                return;
            }
            this.movieDetail = movieDetails;
            isFavorite(movieDetails.getimdbID(), (isFavorite, ref) -> {
                if (isFavorite) {
                    binding.favBtn.setText("Remove from Favourites");
                    binding.favBtn.setOnClickListener(v -> removeFavorite(ref));
                } else {
                    binding.favBtn.setText("Add to Favourites");
                    binding.favBtn.setOnClickListener(v -> addFavorite(movieDetails));
                }
            });
            binding.progressBar.setVisibility(View.GONE);
            binding.detailLayout.setVisibility(View.VISIBLE);
            binding.titleTxt.setText(movieDetails.getTitle());
            binding.year.setText(movieDetails.getReleased());
            binding.plot.setText(movieDetails.getPlot());
            binding.director.setText(movieDetails.getDirector());
            binding.genre.setText(movieDetails.getGenre());
            binding.cast.setText(movieDetails.getActors());
            List<Movie.Rating> ratings =  movieDetails.getRatings();
            TextView[] ratingValues = new TextView[]{
                    binding.rating1Value,
                    binding.rating2Value,
                    binding.rating3Value,
            };
            for (int i = 0; i < ratings.size() && i < ratingValues.length; i++) {
                Movie.Rating rating = ratings.get(i);
                ratingValues[i].setText(rating.getValue());
            }
            Picasso.get().load(movieDetails.getPoster()).into(binding.imageview);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        viewModel.setMovieDetail(null);
        super.onDestroyView();
    }

    private void getMovieDetails (String id) {
        String apiKey = BuildConfig.API_KEY;
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&plot=full&i=" + id;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> makeText(requireActivity(), "Failed to fetch data", LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String responseData = response.body().string();
                    requireActivity().runOnUiThread(() -> {
                        Gson gson = new Gson();
                        Movie movieData = gson.fromJson(responseData, Movie.class);
                        viewModel.setMovieDetail(movieData);
                    });
                }
            }
        });
    }


}