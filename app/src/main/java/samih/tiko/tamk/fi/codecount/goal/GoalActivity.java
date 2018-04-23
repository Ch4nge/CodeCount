package samih.tiko.tamk.fi.codecount.goal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import samih.tiko.tamk.fi.codecount.R;
import samih.tiko.tamk.fi.codecount.stat.StatsActivity;
import samih.tiko.tamk.fi.codecount.login.Token;
import samih.tiko.tamk.fi.codecount.util.Util;
import samih.tiko.tamk.fi.codecount.leaderboard.LeaderboardActivity;

/**
 * Activity that holds users Wakatime Goals in chart
 */
public class GoalActivity extends AppCompatActivity {

    /**
     * Chart that holds users goal data
     */
    private CombinedChart chart;
    /**
     * Charts data
     */
    private CombinedData data;
    /**
     * xAxis of chart, defined to set custom values to axis
     */
    private XAxis xAxis;
    /**
     * Dropdown menu where use can change goal
     */
    private Spinner dropdown;

    /**
     * is Async task running or not
     */
    private boolean asyncTaskRunning = true;
    /**
     * is user interacting with activity
     */
    private boolean isUserInteracting;
    /**
     * selected goal that is shown in chart(used in switch case)
     */
    private int selectedGoal = 0;

    /**
     * Gets data and sets it to UI
     * @param savedInstanceState savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.rgb(88,88,88));
        setSupportActionBar(myToolbar);

        Drawable drawable = myToolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), Color.WHITE);
            myToolbar.setOverflowIcon(drawable);
        }

        chart = (CombinedChart) findViewById(R.id.goals_chart);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setNoDataText("Loading...");
        chart.setDescription(null);
        chart.setScaleEnabled(false);

        Legend l = chart.getLegend();
        l.setTextColor(Color.WHITE);

        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setTextColor(Color.WHITE);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        setTitle("CodeCount");

        data = new CombinedData();

        //DROPDOWN
        dropdown = findViewById(R.id.goalDropdown);
        String[] items = {"Loading.."};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);
        dropdown.setEnabled(false);

        new WakatimeGoalsTask(this).execute();
    }

    /**
     * sets isUserInteraction to true when user interacts with app
     */
    @Override
    public void onUserInteraction(){
        super.onUserInteraction();
        isUserInteracting = true;
    }

    /**
     * Called when user selects MenuItem, changes activity depenting on select
     * @param item MenuItem that is selected
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
     * Sets line data to combinet chart
     * @param chart_data JSONArray where data is fetched
     * @return LineData for chart
     */
    private LineData setLineData(JSONArray chart_data) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < chart_data.length(); i++) {
            try {
                entries.add(new Entry(i + 0.5f,
                        (float) chart_data.getJSONObject(i).getInt("goal_seconds") / 60 / 60));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        LineDataSet set = new LineDataSet(entries, "Goal");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    /**
     * Sets BarData of combined chart
     * @param chart_data JSONArray where data is fetched
     * @return BarData for chart
     */
    private BarData setBarData(JSONArray chart_data) {

        BarData d = new BarData();

        ArrayList<BarEntry> entries1 = new ArrayList<>();

        String[] dates = new String[chart_data.length()];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < chart_data.length(); i++) {
            try {
                entries1.add(new BarEntry(i, (float)chart_data.getJSONObject(i).getInt("actual_seconds")/60/60));
                Date date = sdf.parse(chart_data.getJSONObject(i).getJSONObject("range").getString("date"));
                System.out.println(date);
                String dayOfTheWeek = (String) DateFormat.format("EEE", date);

                dates[i] = dayOfTheWeek;


                System.out.println(dates[i]);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        final String[] tempDates = dates;
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return tempDates[(int) value];
            }
        });

        BarDataSet set1 = new BarDataSet(entries1, "Daily activity");
        set1.setColor(Color.parseColor("#0c94ee"));
        set1.setValueTextColor(Color.parseColor("#0c94ee"));
        set1.setValueTextSize(10f);
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        d.addDataSet(set1);
        return d;
    }


    /**
     * AsynchTask that gets data from wakatime, and sets them in chart
     */
    class WakatimeGoalsTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private GoalActivity activity;

        /**
         * constructor
         * @param activity current activity
         */
        public WakatimeGoalsTask(GoalActivity activity){
            this.activity = activity;
        }


        /**
         * On AsyncTask start, inits chart and disables dropdown
         */
        protected void onPreExecute() {
            dropdown.setEnabled(false);
            asyncTaskRunning = true;
            chart.setData(null);
            chart.invalidate();
        }

        /**
         * Gets data from given url
         * @param urls url where data is taken
         * @return JSON data as String
         */
        protected String doInBackground(Void... urls) {
            System.out.println("TOKENI: " + Token.accessToken);

            try {
                URL url = new URL(Util.apiUrl+"/users/current/goals");
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
         * Turns string data into JSONData, takes needed data and sets it to chart.
         * enables dropdown.
         * @param response Response from doInBackground
         */
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "ERROR";
            }
            try {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                String[] dropdownItems = new String[jsonObj.getJSONArray("data").length()];
                for (int i = 0; i < dropdownItems.length; i++) {
                    dropdownItems[i] =
                            jsonObj.getJSONArray("data").getJSONObject(i).getString("title");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        activity, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
                dropdown.setAdapter(adapter);
                dropdown.setEnabled(true);
                dropdown.setSelection(selectedGoal);

                JSONObject jsonData = jsonObj.getJSONArray("data").getJSONObject(selectedGoal);
                JSONArray chart_data = jsonData.getJSONArray("chart_data");
                data.setData(setBarData(chart_data));
                data.setData(setLineData(chart_data));
                chart.setData(data);
                chart.invalidate();

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedGoal = position;
                        if(!asyncTaskRunning && isUserInteracting)
                            new WakatimeGoalsTask(activity).execute();
                        isUserInteracting = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(response);
            asyncTaskRunning = false;
        }
    }
}
