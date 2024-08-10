package com.example.moviesdb.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesdb.ItemClickListener;
import com.example.moviesdb.MyAdapter;
import com.example.moviesdb.R;
import com.example.moviesdb.databinding.FragmentHomeBinding;
import com.example.moviesdb.model.Movie;
import com.example.moviesdb.viewmodel.MovieViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements ItemClickListener {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;

    List<Movie> movieList;

    MyAdapter myAdapter;
    MovieViewModel viewModel;
    NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navController = NavHostFragment.findNavController(this);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        viewModel.getMovieList().observe(getViewLifecycleOwner(), movieList -> {
            this.movieList = movieList;
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
            binding.recyclerView.setLayoutManager(layoutManager);
            myAdapter = new MyAdapter(movieList);
            binding.recyclerView.setAdapter(myAdapter);
            myAdapter.setClickListener(this);
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCLick(View v, int pos) {
        String id = movieList.get(pos).getimdbID();
        viewModel.setMovieId(movieList.get(pos).getimdbID());
        navController.navigate(R.id.navigation_moviedetails);
    }

}