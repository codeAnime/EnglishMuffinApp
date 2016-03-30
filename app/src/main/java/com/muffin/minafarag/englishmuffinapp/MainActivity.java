package com.muffin.minafarag.englishmuffinapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.muffin.minafarag.englishmuffinapp.models.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private TextView tvData;
    private ListView tvMovies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMovies = (ListView) findViewById(R.id.tvMovies);

    }

    public class JSONTask extends AsyncTask<String, String, List<MovieModel>> {

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                StringBuffer buffer = new StringBuffer();

                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("movies");

                List<MovieModel> movieModelList = new ArrayList<>();
                //StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0; i< parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    MovieModel movieModel = new MovieModel();
                    movieModel.setMovie(finalObject.getString("movie"));
                    movieModel.setYear(finalObject.getInt("year"));
                    movieModel.setRating((float) finalObject.getDouble("rating"));
                    movieModel.setDirector(finalObject.getString("director"));

                    movieModel.setDuration(finalObject.getString("duration"));
                    movieModel.setTagline(finalObject.getString("tagline"));
                    movieModel.setImage(finalObject.getString("image"));
                    movieModel.setStory(finalObject.getString("story"));

                    List<MovieModel.Cast> castList = new ArrayList<>();
                    for(int j=0; j<finalObject.getJSONArray("cast").length(); j++)
                    {
                        MovieModel.Cast cast = new MovieModel().new Cast();
                        cast.setName(finalObject.getJSONArray("cast").getJSONObject(j).getString("name"));
                        castList.add(cast);
                    }
                    movieModel.setCastList(castList);
                    //adding each movieModel object into a list
                    movieModelList.add(movieModel);
                    //finalBufferedData.append(movieName + " - " + year + "\n");
                }
                return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(List<MovieModel> result){
            super.onPostExecute(result);








        }
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        if(id == R.id.action_refresh){
            new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }







}
