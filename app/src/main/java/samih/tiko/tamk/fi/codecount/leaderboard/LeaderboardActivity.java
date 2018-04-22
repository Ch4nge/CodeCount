package samih.tiko.tamk.fi.codecount.leaderboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import samih.tiko.tamk.fi.codecount.goal.GoalActivity;
import samih.tiko.tamk.fi.codecount.R;
import samih.tiko.tamk.fi.codecount.stat.TodayActivity;
import samih.tiko.tamk.fi.codecount.login.Token;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView leaderboard;
    public static Bitmap[] profilePics = new Bitmap[100];
    private static boolean activityRunning = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.rgb(88,88,88));
        setSupportActionBar(myToolbar);
        Drawable drawable = myToolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), Color.WHITE);
            myToolbar.setOverflowIcon(drawable);
        }



        leaderboard = findViewById(R.id.leaderboard);
        TextView emptyView = new TextView(this);
        emptyView.setText("Loading..");
        leaderboard.setEmptyView(emptyView);

        new WakatimeLeaderboardTask(this).execute();
    }

    @Override
    public void onStart(){
        super.onStart();
        activityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch(item.getTitle().toString()){
            case "stats":
                intent = new Intent(this, TodayActivity.class);
                break;
            case "leaderboard":
                intent = new Intent(this, LeaderboardActivity.class);
                break;
            case "goals":
                intent = new Intent(this, GoalActivity.class);
                break;
        }
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    class WakatimeLeaderboardTask extends AsyncTask<Void, Void, String> {

        private LeaderboardActivity activity;
        private Exception exception;

        public WakatimeLeaderboardTask(LeaderboardActivity activity) {
            this.activity = activity;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {
            URL url = null;

            try {
                url = new URL("https://wakatime.com/api/v1/leaders");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer "+ Token.accessToken);
                System.out.println(urlConnection.getResponseCode());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "ERROR";
            }
            try {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray data = jsonObj.getJSONArray("data");
                ArrayList<LeaderboardDataUnit> listData = new ArrayList<>();
                LeaderboardAdapter adapter = new LeaderboardAdapter(listData, activity);
                String[] imgUrls = new String[data.length()];

                for (int i = 0; i < data.length(); i++) {

                    adapter.add(new LeaderboardDataUnit(
                            data.getJSONObject(i).getString("rank"),
                            data.getJSONObject(i).getJSONObject("user").getString("display_name"),
                            data.getJSONObject(i).getJSONObject("running_total").getString("human_readable_total")));

                    imgUrls[i] = data.getJSONObject(i).getJSONObject("user").getString("photo");
                    if(profilePics[i] == null)
                        new DownloadImageTask(i, adapter).execute(data.getJSONObject(i).getJSONObject("user").getString("photo"));
                }

                leaderboard.setAdapter(adapter);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        int index;
        LeaderboardAdapter adapter;

        public DownloadImageTask(int index, LeaderboardAdapter adapter) {
            this.index = index;
            this.adapter = adapter;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            if(activityRunning) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            profilePics[index] = result;
            if(index <= leaderboard.getLastVisiblePosition() && index >= leaderboard.getFirstVisiblePosition()){
                adapter.notifyDataSetChanged();
            }
        }
    }

}
