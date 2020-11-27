package com.example.movierecommender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    JSONArray moviesArray;
    private EditText searchBox;
    ArrayList<Movie> searchedMovies = new ArrayList<>();
    ArrayList<Movie> fixedMoviesList = new ArrayList<>();
    Map<Integer, Movie> movieMap = new HashMap<>();
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    Map<Integer, Integer> moviePackets = new HashMap<>();
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchAdapter = new SearchAdapter(searchedMovies);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        queue = Volley.newRequestQueue(MainActivity.this);


        try {
            moviesArray = new JSONArray(loadJSONFromAsset());
            searchedMovies = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++) {
                try {
                    JSONObject movieObject = moviesArray.getJSONObject(i);
                    if(i==0)
                    Log.d("xxxx","Added"+movieObject);
                    searchedMovies.add(new Movie(movieObject.getString("title"), movieObject.getString("genres"), false, movieObject.getInt("movieId")));
                    movieMap.put(movieObject.getInt("movieId"),new Movie(movieObject.getString("title"), movieObject.getString("genres"), false, movieObject.getInt("movieId")));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                if(searchedMovies.size()>0)
                    findViewById(R.id.placeholder).setVisibility(View.GONE);
                else
                    findViewById(R.id.placeholder).setVisibility(View.VISIBLE);
                fixedMoviesList = searchedMovies;
                searchAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        searchBox = findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!userEntered) return;
                searchedMovies = new ArrayList<>();
                findViewById(R.id.edittext_progress).setVisibility(View.VISIBLE);
                for (int i = 0; i < moviesArray.length(); i++) {
                    try {
                        JSONObject movieObject = moviesArray.getJSONObject(i);
//                        Log.d("xxxx","Checking "+ movieObject.getString("title"));
                        if (movieObject.getString("title").toLowerCase().contains(s.toString().toLowerCase())) {
                            Log.d("xxxx",movieObject.getString("title")+" Contains "+s.toString());
                            searchedMovies.add(new Movie(movieObject.getString("title"), movieObject.getString("genres"),false,movieObject.getInt("movieId")));
                        }
                    }
                     catch (Exception e){
                        e.printStackTrace();
                     }
                    if(searchedMovies.size()>0)
                        findViewById(R.id.placeholder).setVisibility(View.GONE);
                    else
                        findViewById(R.id.placeholder).setVisibility(View.VISIBLE);
                    searchAdapter.notifyDataSetChanged();
                }
                findViewById(R.id.edittext_progress).setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
userEntered = true;
            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("movies.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
        private ArrayList<Movie> moviesList;

        public SearchAdapter(ArrayList<Movie> list){
            moviesList = list;
        }
        @NonNull
        @Override
        public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, final int position) {
            holder.movieTitle.setText(searchedMovies.get(position).title);
            holder.movieGenre.setText(searchedMovies.get(position).genres);
            holder.movieImage.setImageURI("");
            if(moviePackets.containsKey(searchedMovies.get(position).movieId))
                holder.movieRating.setRating(moviePackets.get(searchedMovies.get(position).movieId));
            else
                holder.movieRating.setRating(0);
            holder.movieRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser) {
                        Log.d("xxxx", "Rating changedd for " + searchedMovies.get(position).movieId + " -> " + searchedMovies.get(position).title);
                        moviePackets.put(searchedMovies.get(position).movieId, (int) rating);
                        sendData();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return searchedMovies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView movieImage;
            TextView movieTitle, movieGenre;
            RatingBar movieRating;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                movieImage = itemView.findViewById(R.id.movie_image);
                movieTitle = itemView.findViewById(R.id.movie_name);
                movieGenre = itemView.findViewById(R.id.movie_genres);
                movieRating = itemView.findViewById(R.id.movie_rating);
            }
        }
    }
    private boolean isRequesting = false;
    private boolean userEntered = true;
    private void sendData() {
        if(isRequesting) return;
        isRequesting = true;
        String url = "https://movie-arsundram-api-heroku.herokuapp.com/predict";
        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                userEntered = false;
                searchBox.setText("");
                Log.d("xxxx","Response =>"+response.toString());
                searchedMovies.clear();
                searchAdapter.notifyDataSetChanged();
                isRequesting = false;
                for(int i=0; i<response.length(); i++) {
                    findViewById(R.id.progress_layout).setVisibility(View.GONE);
                    try {
                        searchedMovies.add( movieMap.get(response.getInt(i)));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    searchAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRequesting =false;
                findViewById(R.id.progress_layout).setVisibility(View.GONE);
            }
        }){
            @Override
            public byte[] getBody() {
                JSONObject body = new JSONObject();
                try {
                    for (Map.Entry<Integer, Integer> entry : moviePackets.entrySet()) {
                        body.put(entry.getKey()+"",entry.getValue());
                    }
                    Log.e("xxxx", "body => "+body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.toString().getBytes();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        queue.add(request);
    }
    }
    class MoviePacket{
        int movieId, movieRating;
        public MoviePacket(int movieId, int movieRating){
            this.movieId = movieId;
            this.movieRating = movieRating;
        }
    }
