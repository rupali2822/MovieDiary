package com.template.moviediary;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String url;
    RequestQueue requestQueue;
    MovieModel movieModel;
    ArrayList<MovieModel> movieList,searchedList;
    RecyclerView recyclerView;
    MovieAdapter adapter;
    Context context;
    ArrayAdapter searchAdapter;
    AutoCompleteTextView etSearch;
    String itemToSearch=null;
    ImageView btnSearch, btnHome;
    ProgressBar progressBar;
    DatabaseHelper databaseHelper;

    LinearLayoutManager layoutManager;

    //pagination
private int totalItemsCount;
private int firstVisibleItem;
private int visibleItemsCount;
private int page=1;
private int prevTotal;
boolean load=true;


    //network
    Boolean isNetConnected=false;
    private boolean monitoringConnectivity = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recView);
        etSearch=findViewById(R.id.et_search);
        btnSearch=findViewById(R.id.btnSearch);
        progressBar=findViewById(R.id.v_progressBar);
        btnHome=findViewById(R.id.home);

        databaseHelper=new DatabaseHelper(MainActivity.this);
        movieList=new ArrayList<>();
        searchedList=new ArrayList<>();

       // isNetConnected=isConnectedToNetwork();
        Log.e("","network connection:"+isNetConnected);

        if(isNetConnected){
            //while net is conneted
           getMovieDetails();
       }else{
          //while offline
            //get from DB

            recyclerView.setAdapter(null);
            movieList.clear();
           // VariableClass.movie_list.clear();

          movieList=  databaseHelper.getMovieDtFromDb();

          Log.e("","retrivrd size:"+movieList.size());

          if(movieList.size()<1){
                   String msg="No Internet !!!";
                     showAlertDialog(msg);
          }else {

              VariableClass.movie_list = movieList;

              layoutManager=new LinearLayoutManager(MainActivity.this);
             // recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
             recyclerView.setLayoutManager(layoutManager);
              adapter = new MovieAdapter(MainActivity.this, movieList);
              recyclerView.setAdapter(adapter);
          }

        }

        //add pagination...to load data smoothly
        pagination();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
            }
        });


//        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        adapter=new MovieAdapter(this,movieList);
//        recyclerView.setAdapter(adapter);

        searchAdapter = new ArrayAdapter<MovieModel>(this, android.R.layout.simple_list_item_1, movieList);
        etSearch.setAdapter(searchAdapter);
        etSearch.setThreshold(1);

        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel model= (MovieModel) parent.getAdapter().getItem(position);
            String  selectedText=model.getTitle();
                Log.e("","selected Item:"+selectedText);

                etSearch.setText(selectedText);
               // searchMovie(itemToSearch);

                page=1;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemToSearch=etSearch.getText().toString();

                Log.e("","etSearch item"+itemToSearch);


                if(!isNetConnected){
                    //Toast.makeText(MainActivity.this, "Oops, Please check internet connection !!", Toast.LENGTH_SHORT).show();
                    String msg="No Internet, Please check internet connection !!!";
                    showAlertDialog(msg);
                }else {
                 searchMovie(itemToSearch,page);
                }
                

            }
        });


    }

    private void pagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
               // Log.e("","scroling page.:"+page);
                if(dy>0){
                    firstVisibleItem=layoutManager.findFirstVisibleItemPosition();
                    totalItemsCount=layoutManager.getChildCount();
                    visibleItemsCount=layoutManager.getItemCount();

                    if(load){
                        if(totalItemsCount>prevTotal){
                            prevTotal=totalItemsCount;
                            page++;
                            load=false;
                            Log.e("","page.:"+page);
                        }
                    }

                    if(!load && (firstVisibleItem + visibleItemsCount)>=totalItemsCount){

                      //  callToNext;
                        if(!isNetConnected){
                            //Toast.makeText(MainActivity.this, "Oops, Please check internet connection !!", Toast.LENGTH_SHORT).show();
                            String msg="No Internet, Please check internet connection !!!";
                            showAlertDialog(msg);
                        }else {
                            Log.e("","getting next page.:"+page);
                            searchMovie(itemToSearch,page);
                        }
                        load=true;
                        Log.e("","page:"+page);
                    }
                }
            }
        });
    }

    private void getNext() {

    }

    public boolean isConnectedToNetwork() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Toast.makeText(this, "Connectivity Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

   // method to check network connectivity
    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isNetConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isNetConnected) {

            Log.e("","NO NETWORK!");
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    private void searchMovie(String movieName,int pageNo) {

        if(itemToSearch.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter something  to search..", Toast.LENGTH_SHORT).show();
        }else {
            movieList.clear();
            searchedList.clear();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);

            String page = String.valueOf(pageNo);

            movieName = movieName.trim();
            Log.e("", "searching movie details...");

            //http://www.omdbapi.com/?apikey={API_KEY}&s={SEARCH_STRING}&page={PAGE_NO}
            url = "http://www.omdbapi.com/?apikey=73e19e07&s=" + movieName + "&page=" + page;

            Log.e("", "getting movie details.1.." + url);

            requestQueue = Volley.newRequestQueue(MainActivity.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e("", "getMovie details respose:" + response);
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("Response");

                                if (status.equals("True")) {
                                    JSONArray array = jsonObject.getJSONArray("Search");
                                    Log.e("", "getMovie details status:" + array.length());

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        String title = obj.getString("Title");
                                        String Year = obj.getString("Year");
                                        String imdbID = obj.getString("imdbID");
                                        String Poster = obj.getString("Poster");


                                        movieModel = new MovieModel();
                                        movieModel.setTitle(title);
                                        movieModel.setYear(Year);
                                        movieModel.setPoster(Poster);
                                        movieModel.setImdbID(imdbID);

                                        searchedList.add(movieModel);
                                    }

                                    // recyclerView.notify();
                                    Log.e("", "movie list:" + movieList.size());
                                    Log.e("", "search list:" + searchedList.size());
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    adapter = new MovieAdapter(MainActivity.this, searchedList);
                                    recyclerView.setAdapter(adapter);

                                    VariableClass.movie_list = searchedList;

                                } else {
                                    Log.e("", "Movie not found"+page);
                                    if (page.equals("1")) {
                                        Log.e("", "Movie not found");
                                        String error = jsonObject.getString("Error");
                                        showAlertDialog(error);
                                    } else {

                                    }

                                }

                            } catch (JSONException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e("", "exp while getting response..." + e.toString());
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e("", "Error while getting movie details..." + error.toString());
                            Toast.makeText(MainActivity.this, "Error while getting movie details.." + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
            );

            stringRequest.setRetryPolicy(
                    new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );
            requestQueue.add(stringRequest);

        }

    }

    public void showAlertDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // builder.setIcon(getResources().getDrawable(R.drawable.applicationicon));
        builder.setTitle(msg);
        builder.setIcon(R.drawable.error_ic);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                etSearch.setText("");
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    private void getMovieDetails() {
        progressBar.setVisibility(View.VISIBLE);
        movieList.clear();
        Log.e("","getting movie details...");
        //http://www.omdbapi.com/?i={imdbID}&apikey={API_KEY}
        url="http://www.omdbapi.com/?i=tt3896198&apikey=73e19e07";
        Log.e("","getting movie details.1.."+url);
        requestQueue= Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest=new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e("","getMovie details respose:"+response);
                            JSONObject object=new JSONObject(response);
                            Log.e("","response len:"+response.length());

//                          JSONArray  parentObject = object.getJSONObject();
                            //JSONArray obj=new JSONArray(object);
                            Log.e("","object len:"+object.length());

                            String title=object.getString("Title");
                            String Year=object.getString("Year");
                            String Rated=object.getString("Rated");
                            String Released=object.getString("Released");
                            String Runtime=object.getString("Runtime");
                            String Writer=object.getString("Writer");
                            String Actors=object.getString("Actors");
                            String Plot=object.getString("Plot");
                            String Language=object.getString("Language");
                            String Poster=object.getString("Poster");
                            String Country=object.getString("Country");
                            String Awards=object.getString("Awards");
                            String imdbID=object.getString("imdbID");

//                            String Country=object.getString("Country");
//                            String Country=object.getString("Country");

                            Log.e("","Title:"+title);
                            movieModel=new MovieModel();
                            movieModel.setTitle(title);
                            movieModel.setYear(Year);
                            movieModel.setRated(Rated);
                            movieModel.setReleased(Released);
                            movieModel.setRuntime(Runtime);
                            movieModel.setWriter(Writer);
                            movieModel.setActor(Actors);
                            movieModel.setPlot(Plot);
                            movieModel.setLanguage(Language);
                            movieModel.setPoster(Poster);
                            movieModel.setCountry(Country);
                            movieModel.setAwards(Awards);
                            movieModel.setImdbID(imdbID);

                            movieList.add(movieModel);

                           // recyclerView.notify();
                            Log.e("","movie list:"+movieList.size());
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            adapter=new MovieAdapter(MainActivity.this,movieList);
                            recyclerView.setAdapter(adapter);

                            VariableClass.movie_list=movieList;

                            Log.e("Databasehelper", "data inserting into db");

                            databaseHelper.insertMovieDetails(movieList);

//                            for(int i=0;i<object.length();i++){
//                                Log.e("","loop "+i);
//                                Log.e("","object Title:"+object.getString("Title"));
//
//                            }

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e("","exp while getting response..."+e.toString());
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(error.toString().contains("NoConnectionError")){
                            String msg="No Internet !!!";
                            showAlertDialog(msg);
                        }
                        Log.e("","Error while getting movie details..."+error.toString());
                        Toast.makeText(MainActivity.this, "Error while getting movie details.."+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        requestQueue.add(stringRequest);

    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isNetConnected = true;
            Log.e("","internet connected");

        }

        @Override
        public void onLost(Network network) {
            isNetConnected = false;
            Log.e("","No internet ");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("","onResume called");
        checkConnectivity();
    }

    @Override
    protected void onPause() {
        Log.e("","onPause called");
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}