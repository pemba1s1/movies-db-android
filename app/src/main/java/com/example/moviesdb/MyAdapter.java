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

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final List<Movie> movieList;

    public ItemClickListener clickListener;

    public MyAdapter(List<Movie> itemList) {
        this.movieList = itemList;
    }
    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_layout,parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        // Binds the data from your dataset to the views within the view holder
        Movie movie = movieList.get(position);

        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        Picasso.get().load(movie.getPoster()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView title;
        TextView year;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_txt);
            year = itemView.findViewById(R.id.year);
            image = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.onCLick(v,getAdapterPosition());
            }

        }
    }
}
