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
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesdb.ItemClickListener;
import com.example.moviesdb.MyAdapter;
import com.example.moviesdb.R;
import com.example.moviesdb.databinding.FragmentHomeBinding;
import com.example.moviesdb.model.Movie;
import com.example.moviesdb.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ItemClickListener {

    private FragmentHomeBinding binding;
    List<Movie> fetchedMovieList;
    MovieViewModel viewModel;
    NavController navController;
    private boolean isLoading = false;
    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), viewModel::searchMovie);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navController = NavHostFragment.findNavController(this);

        layoutManager = new LinearLayoutManager(requireActivity());
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        viewModel.getFetchedMovieList().observe(getViewLifecycleOwner(), fetchedMovieList -> {
            if (this.fetchedMovieList == null) {
                this.fetchedMovieList = viewModel.getMovieList().getValue();
            }
            if (fetchedMovieList.second || adapter == null) {
                adapter = new MyAdapter();
                binding.recyclerView.setAdapter(adapter);
                adapter.setClickListener(this);
            }
            this.fetchedMovieList.addAll(fetchedMovieList.first);
            adapter.addItems(fetchedMovieList.first);
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    isLoading = true;
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        adapter.setLoading(true);
                        viewModel.loadMore(); // Load more data
                    }
                    isLoading = false;
                }
            }
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
        viewModel.setMovieId(fetchedMovieList.get(pos).getimdbID());
        navController.navigate(R.id.navigation_moviedetails);
    }

}