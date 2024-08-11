package com.example.moviesdb.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moviesdb.ItemClickListener;
import com.example.moviesdb.MyAdapter;
import com.example.moviesdb.R;
import com.example.moviesdb.databinding.FragmentFavoriteBinding;
import com.example.moviesdb.model.Movie;
import com.example.moviesdb.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements ItemClickListener {
    private FragmentFavoriteBinding binding;
    List<Movie> favMovieList;
    MovieViewModel viewModel;
    NavController navController;
    private MyAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        View root = binding.getRoot();

        navController = NavHostFragment.findNavController(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        viewModel.getFavoriteMovieList().observe(getViewLifecycleOwner(), fetchedMovieList -> {
            if (fetchedMovieList.isEmpty()) {
                binding.noResult.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                return;
            }
            this.favMovieList = new ArrayList<>();
            adapter = new MyAdapter();
            binding.recyclerView.setAdapter(adapter);
            this.favMovieList.addAll(fetchedMovieList);
            adapter.addItems(fetchedMovieList);
            adapter.setClickListener(this);
            binding.progressBar.setVisibility(View.GONE);
            binding.noResult.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        });

        viewModel.fetchFavoriteMovies();

        return root;
    }

    @Override
    public void onCLick(View v, int pos) {
        viewModel.setMovieId(favMovieList.get(pos).getimdbID());
        navController.navigate(R.id.navigation_moviedetails);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}