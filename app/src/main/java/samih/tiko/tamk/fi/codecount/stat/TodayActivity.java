package samih.tiko.tamk.fi.codecount.stat;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import samih.tiko.tamk.fi.codecount.Login.Token;
import samih.tiko.tamk.fi.codecount.R;
import samih.tiko.tamk.fi.codecount.goal.GoalActivity;

public class TodayActivity extends AppCompatActivity {

    private final int WEEK = 0;
    private final int MONTH = 1;
    private final int YEAR = 2;

    private int selectedItem = 0;
    private boolean asyncTaskRunning = true;

    private PieChart pieChart;
    private Spinner dropdown;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.rgb(88,88,88));
        setSupportActionBar(myToolbar);


        title = (TextView) findViewById(R.id.coding_title);

        //Dropdown
        dropdown = findViewById(R.id.dropdown);
        String[] items = new String[]{"last week", "last month", "last year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedItem = WEEK;
                        startAsynchProcess();
                        break;
                    case 1:
                        selectedItem = MONTH;
                        startAsynchProcess();
                        break;
                    case 2:
                        selectedItem = YEAR;
                        startAsynchProcess();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

        new WakatimeStatsTask().execute();
    }

    private void startAsynchProcess(){
        if(!asyncTaskRunning){
            new WakatimeStatsTask().execute();
        }
    }

    private void setData(JSONArray languages) {


        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < languages.length() ; i++) {

            PieEntry entry = null;
            try{
                float hoursCoded =
                        (float) languages.getJSONObject(i).getInt("total_seconds") / 60 / 60;
                entry = new PieEntry(hoursCoded, i);
                entry.setLabel(languages.getJSONObject(i).getString("name"));
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

    public void switchActivity(View view){
        Intent intent = new Intent(this, GoalActivity.class);
        startActivity(intent);
    }

    class WakatimeStatsTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            asyncTaskRunning = true;
            pieChart.clear();
            dropdown.setEnabled(false);
        }

        protected String doInBackground(Void... urls) {
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

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "ERROR";
            }
            try {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject data = jsonObj.getJSONObject("data");
                JSONArray languages = null;
                if(data.getBoolean("is_up_to_date"))
                    languages = data.getJSONArray("languages");
                if(languages != null) {
                    setData(languages);
                    pieChart.setCenterText(data.getString("human_readable_total"));
                }else{
                    System.out.println(jsonObj.getString("message"));
                    pieChart.setNoDataText(jsonObj.getString("message"));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            asyncTaskRunning = false;
            dropdown.setEnabled(true);
            System.out.println(response);
        }
    }
}
