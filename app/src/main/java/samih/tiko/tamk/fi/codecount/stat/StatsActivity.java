package samih.tiko.tamk.fi.codecount.stat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import samih.tiko.tamk.fi.codecount.leaderboard.LeaderboardActivity;
import samih.tiko.tamk.fi.codecount.login.Token;
import samih.tiko.tamk.fi.codecount.R;
import samih.tiko.tamk.fi.codecount.goal.GoalActivity;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class StatsActivity extends AppCompatActivity{

    /**
     * integer that refers to week
     */
    private final int WEEK = 0;
    /**
     * integer that refers to month
     */
    private final int MONTH = 1;
    /**
     * integer that refers to year
     */
    private final int YEAR = 2;

    /**
     * selectedItem that tells what data should be fetched, uses WEEK, MONTH and YEAR
     */
    private int selectedItem = 0;
    /**
     * Tells if async task is running or not
     */
    private boolean asyncTaskRunning = true;

    /**
     * Sort data by this value, can be editors, projects or languages
     */
    private String sorting = "languages";

    /**
     * Pie Chart where data is shown
     */
    private PieChart pieChart;
    /**
     * Json object that contains data of pie chart
     */
    private JSONObject data;
    /**
     * object that contains potential errors and messages from wakatime
     */
    private JSONObject jsonObj;
    /**
     * radio buttons for time ranges
     */
    private RadioGroup timeRanges;
    /**
     * radio buttons for sorting data
     */
    private RadioGroup sortby;
    /**
     * Title textview
     */
    private TextView title;

    /**
     * inits chart, gets data and sets it to chart.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.rgb(88,88,88));
        setSupportActionBar(myToolbar);
        Drawable drawable = myToolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), Color.WHITE);
            myToolbar.setOverflowIcon(drawable);
        }

        title = (TextView) findViewById(R.id.coding_title);
        RadioButton btn = (RadioButton)findViewById(R.id.languages);
        btn.setChecked(true);
        btn = (RadioButton) findViewById(R.id.week);
        btn.setChecked(true);

        timeRanges = (RadioGroup) findViewById(R.id.time_ranges);
        sortby = (RadioGroup) findViewById(R.id.sort_by);


        //Create pie chart
        pieChart = (PieChart) findViewById(R.id.pieChart_today);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setNoDataText("Loading..");
        pieChart.setNoDataTextColor(Color.rgb(255, 170, 0));
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(15f);
        pieChart.getDescription().setEnabled(false);


        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setTextColor(Color.WHITE);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        l.setMaxSizePercent(0.32f);
        l.setWordWrapEnabled(true);

        new WakatimeStatsTask().execute();
    }

    /**
     * changes selected item and starts async task.
     * called when timerange radiobutton is selected
     * @param view radiobutton
     */
    public void changeTimeRange(View view){
        switch (view.getId()) {
            case R.id.week:
                selectedItem = WEEK;
                startAsynchProcess();
                break;
            case R.id.month:
                selectedItem = MONTH;
                startAsynchProcess();
                break;
            case R.id.year:
                selectedItem = YEAR;
                startAsynchProcess();
                break;
        }
    }

    /**
     * Changes sorting and re-sets piecharts data, called when sorting radiobutton is selected
     * @param view radiobutton
     */
    public void changeSorting(View view){
        switch(view.getId()){
            case R.id.languages:
                sorting = "languages";
                setPieData();
                break;
            case R.id.projects:
                sorting = "projects";
                setPieData();
                break;
            case R.id.editors:
                sorting = "editors";
                setPieData();
                break;
        }
    }

    /**
     * Navigation
     * @param item selected item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch(item.getTitle().toString()){
            case "stats":
                intent = new Intent(this, StatsActivity.class);
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

    /**
     * Gets menu layout from R.menu.top_menu
     * @param menu Menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * Starts async process, if it is not running
     */
    private void startAsynchProcess(){
        if(!asyncTaskRunning){
            new WakatimeStatsTask().execute();
        }
    }

    /**
     * Gets data from JSONArray and sets it to piechart
     * @param sortedData array where data is taken
     */
    private void setData(JSONArray sortedData) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < sortedData.length() ; i++) {

            PieEntry entry = null;
            try{
                float hoursCoded =
                        (float) sortedData.getJSONObject(i).getInt("total_seconds") / 60 / 60;
                entry = new PieEntry(hoursCoded, i);
                entry.setLabel(sortedData.getJSONObject(i).getString("name"));
            }catch(JSONException e){
                e.printStackTrace();
            }

            entries.add(entry);
        }
        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);



        data.setValueTextSize(14f);
        data.setValueTextColor(Color.DKGRAY);

        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(Color.DKGRAY);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    /**
     * Called from async task, sets data to piedata depending on response
     */
    public void setPieData(){
        try {
            JSONArray sortedData = null;
            if (data.getBoolean("is_up_to_date"))
                sortedData = data.getJSONArray(sorting);
            if (sortedData != null) {
                setData(sortedData);
                pieChart.setCenterText(data.getString("human_readable_total"));
            } else {
                pieChart.setNoDataText(jsonObj.getString("message"));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Sets radiobutton enabled or diabled
     * @param enabled boolean value of enabled
     */
    private void setRadioEnabled(boolean enabled){
        for (int i = 0; i < sortby.getChildCount(); i++) {
            sortby.getChildAt(i).setEnabled(enabled);
        }
        for (int i = 0; i < timeRanges.getChildCount(); i++) {
            timeRanges.getChildAt(i).setEnabled(enabled);
        }
    }

    /**
     * Async task that gets data from wakatime and sets it to
     * pieChart
     */
    class WakatimeStatsTask extends AsyncTask<Void, Void, String> {

        /**
         * sets radiobuttons disabled and clears piechart data on task start
         */
        protected void onPreExecute() {
            asyncTaskRunning = true;
            pieChart.clear();
            setRadioEnabled(false);
        }

        /**
         * gets data from url and returns JSON as String
         * @param urls url where data is gotten
         * @return JSON as string
         */
        protected String doInBackground(Void... urls) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            System.out.println("TOKENI: " + Token.accessToken);
            URL url = null;

            try {
                switch(selectedItem){
                    case 0:
                        url = new URL("https://wakatime.com/api/v1/users/current/stats/last_7_days");
                        break;
                    case 1:
                        url = new URL("https://wakatime.com/api/v1/users/current/stats/last_30_days");
                        break;
                    case 2:
                        url = new URL("https://wakatime.com/api/v1/users/current/stats/last_year");
                }
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

        /**
         * Turns response into JSONObject, gets data from it and sets it to pieChart
         * @param response JSON data as a string.
         */
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "ERROR";
            }
            try {
                jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                data = jsonObj.getJSONObject("data");

            } catch (JSONException e){
                e.printStackTrace();
            }
            asyncTaskRunning = false;
            setPieData();
            setRadioEnabled(true);
            System.out.println(response);
        }
    }
}
