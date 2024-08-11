package com.example.moviesdb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesdb.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final List<Movie> movieList = new ArrayList<>();
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading = false;

    public ItemClickListener clickListener;

    public void setClickListener(ItemClickListener myListener) {
        this.clickListener = myListener;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_layout, parent, false);
            return new MovieViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            Movie movie = movieList.get(position);
            ((MovieViewHolder) holder).title.setText(movie.getTitle());
            ((MovieViewHolder) holder).year.setText(movie.getYear());
            Picasso.get().load(movie.getPoster()).into(((MovieViewHolder) holder).image);
        } else if (holder instanceof LoadingViewHolder) {
            // No need to bind data for the loading item
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size() + (isLoading ? 1 : 0); // Add 1 if loading
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieList.size() && isLoading) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void addItems(List<Movie> newMovies) {
        int oldItemCount = movieList.size();
        movieList.addAll(newMovies);
        notifyItemRangeInserted(oldItemCount, newMovies.size());
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyItemChanged(getItemCount() - 1);
    }

    // Abstract base class for both ViewHolders
    public abstract class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MovieViewHolder extends MyViewHolder implements View.OnClickListener {
        TextView title;
        TextView year;
        ImageView image;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_txt);
            year = itemView.findViewById(R.id.year);
            image = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onCLick(v, getAdapterPosition());
            }
        }
    }

    public class LoadingViewHolder extends MyViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}