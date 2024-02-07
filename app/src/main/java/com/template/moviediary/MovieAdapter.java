package com.template.moviediary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    Context context;
    ArrayList<MovieModel> list;

    public MovieAdapter(Context context, ArrayList<MovieModel> arrayList) {
        this.context = context;
        this.list = arrayList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_lay,parent,false);
       // MovieViewHolder holder=new MovieViewHolder(view,fragmentCommunication);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //holder.imageView.setText(memberReceiptArraylist.get(position).getType());
        Log.e("","url 1:"+list.get(position).getPoster());
        holder.movieText.setText(list.get(position).getTitle());
        String imgUrl=list.get(position).getPoster();

        if(imgUrl!=null ) {
            if(imgUrl.contains("http")) {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.movie_ic) // Placeholder image
                                .error(R.drawable.movie_ic) // Error image in case of loading failure
                        )
                        .into(holder.imageView);

            }
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("","card clicked"+position);
                Intent i=new Intent(context,MovieDetailActivity.class);
                i.putExtra("position",String.valueOf(position));
                context.startActivity(i);


//                i.putExtra("Name",list.get(position).getTitle());
//                i.putExtra("Year",list.get(position).getYear());
//                i.putExtra("Rated",list.get(position).getRated());
//                i.putExtra("Released",list.get(position).getReleased());
//                i.putExtra("Runtime",list.get(position).getRuntime());
//                i.putExtra("Writer",list.get(position).getWriter());
//                i.putExtra("Actors",list.get(position).getActor());
//                i.putExtra("Plot",list.get(position).getPlot());
//                i.putExtra("Language",list.get(position).getLanguage());
//                i.putExtra("Language",list.get(position).getLanguage());
//                i.putExtra("Poster",list.get(position).getPoster());


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView movieText;
        MaterialCardView cardView;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.movieImg);
            movieText=itemView.findViewById(R.id.movieName);
            cardView=itemView.findViewById(R.id.card);
        }
    }
}
