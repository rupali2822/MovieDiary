package com.template.moviediary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "db_movie";
    //database version
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tb_movie_details";

MovieModel movieModel;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        //creating table
//        String technician_id,name,phone1,phone2,address1,address2,area,landmark,city, state,country,pincode,active,provider_id,
//                            company_name;
        query = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY,  mname TEXT ,poster TEXT, year TEXT, rated  TEXT,released TEXT,runtime TEXT,writer TEXT,actors TEXT,plot TEXT,language TEXT,country TEXT,imdbid TEXT, awards TEXT  )";
        db.execSQL(query);

        Log.e("","table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertMovieDetails(ArrayList<MovieModel> list){
        Log.e("Databasehelper", "inserting data");

        SQLiteDatabase database=this.getWritableDatabase();

        database.delete(TABLE_NAME,"ID="+1,null);
        Log.e("Databasehelper", "existing deleted ");

        for(int i=0 ; i<list.size(); i++){
            ContentValues values=new ContentValues();
            values.put("mname",list.get(i).getTitle());
            values.put("poster",list.get(i).getPoster());
            values.put("year",list.get(i).getYear());
            values.put("rated",list.get(i).getRated());
            values.put("released",list.get(i).getReleased());
            values.put("runtime",list.get(i).getRuntime());
            values.put("writer",list.get(i).getWriter());
            values.put("actors",list.get(i).getActor());
            values.put("plot",list.get(i).getPlot());
            values.put("language",list.get(i).getLanguage());
            values.put("country",list.get(i).getCountry());
            values.put("imdbid",list.get(i).getImdbID());
            values.put("awards",list.get(i).getAwards());

            database.insert(TABLE_NAME,null,values);
        }



        Log.e("Databasehelper", "data inserted");
        database.close();

    }

    public ArrayList<MovieModel> getMovieDtFromDb(){
        Log.e("Databasehelper", "getting data ");
            SQLiteDatabase database = this.getReadableDatabase();
           // MovieModel model = new MovieModel();
            ArrayList<MovieModel> result = new ArrayList<>();
        try {
            Cursor c = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            while (c.moveToNext()) {
                @SuppressLint("Range")
                String name = c.getString(c.getColumnIndex("mname"));
                @SuppressLint("Range")
                String year = c.getString(c.getColumnIndex("year"));
                String rated = c.getString(c.getColumnIndex("rated"));
                String released = c.getString(c.getColumnIndex("released"));
                String runtime = c.getString(c.getColumnIndex("runtime"));
                String writer = c.getString(c.getColumnIndex("writer"));
                String actors = c.getString(c.getColumnIndex("actors"));
                String plot = c.getString(c.getColumnIndex("plot"));
                String language = c.getString(c.getColumnIndex("language"));
                String country = c.getString(c.getColumnIndex("country"));
                String imdbid = c.getString(c.getColumnIndex("imdbid"));
                String awards = c.getString(c.getColumnIndex("awards"));
                String poster=c.getString(c.getColumnIndex("poster"));


                movieModel=new MovieModel();
                movieModel.setTitle(name);
                movieModel.setYear(year);
                movieModel.setRated(rated);
                movieModel.setReleased(released);
                movieModel.setRuntime(runtime);
                movieModel.setWriter(writer);
                movieModel.setActor(actors);
                movieModel.setPlot(plot);
                movieModel.setLanguage(language);
                movieModel.setPoster(poster);
                movieModel.setCountry(country);
                movieModel.setAwards(awards);
                movieModel.setImdbID(imdbid);

               result.add(movieModel);
                Log.e("","retrived data");
            }
            c.close();
            database.close();


        }catch (Exception e){
            e.printStackTrace();
            Log.e("","error while retriving "+e.getMessage());

        }
        return result;

    }

}
