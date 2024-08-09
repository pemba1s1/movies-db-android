package com.example.moviesdb.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesdb.ItemClickListener;
import com.example.moviesdb.MyAdapter;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        viewModel.getMovieList().observe(getViewLifecycleOwner(), movieList -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
            binding.recyclerView.setLayoutManager(layoutManager);
            myAdapter = new MyAdapter(movieList);
            binding.recyclerView.setAdapter(myAdapter);
        });



//        myAdapter.setClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCLick(View v, int pos) {

    }

}