package com.template.moviediary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MovieDetailActivity extends AppCompatActivity {
String name,Year,Rated,Released,Runtime,Writer,Actors,Language,Plot,Country,Awards,imdbID;
TextView tvname,tvrated,tvYear,tvRelease,tvRuntime,tvActor,tvPlot, tvLang,tvWriter,tvCountry,tvAward,tvimdbID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        tvname=findViewById(R.id.tvName);
        tvrated=findViewById(R.id.tvRated);
        tvYear=findViewById(R.id.tvYear);
        tvRelease=findViewById(R.id.tvReased);
        tvRuntime=findViewById(R.id.tvRuntime);
        tvActor=findViewById(R.id.tvActors);
        tvPlot=findViewById(R.id.tvPlot);
        tvLang=findViewById(R.id.tvLang);
        tvWriter=findViewById(R.id.tvWriter);
        tvCountry=findViewById(R.id.tvCountry);
        tvAward=findViewById(R.id.tvAward);
        tvimdbID=findViewById(R.id.tvimdbID);


        Intent i=getIntent();
        String position=i.getStringExtra("position");
        Log.e("","pos:"+position);

        int pos=Integer.parseInt(position);

        name=VariableClass.movie_list.get(pos).getTitle();
        Year=VariableClass.movie_list.get(pos).getYear();
        Rated=VariableClass.movie_list.get(pos).getRated();
        Released=VariableClass.movie_list.get(pos).getReleased();
        Runtime=VariableClass.movie_list.get(pos).getRuntime();
        Writer=VariableClass.movie_list.get(pos).getWriter();
        Actors=VariableClass.movie_list.get(pos).getActor();
        Plot=VariableClass.movie_list.get(pos).getPlot();
        Language=VariableClass.movie_list.get(pos).getLanguage();
        Country=VariableClass.movie_list.get(pos).getCountry();
        Awards=VariableClass.movie_list.get(pos).getAwards();
        imdbID=VariableClass.movie_list.get(pos).getImdbID();



       // Writer=VariableClass.movie_list.get(pos).getWriter();

        Log.e("","Year 1:"+Year);

        tvname.setText(name);
        tvrated.setText(Rated);
        tvYear.setText(Year);
        tvRelease.setText(Released);
        tvRuntime.setText(Runtime);
        tvActor.setText(Actors);
        tvPlot.setText(Plot);
        tvLang.setText(Language);
        tvWriter.setText(Writer);
        tvCountry.setText(Country);
        tvAward.setText(Awards);
        tvimdbID.setText(imdbID);

    }
}