package net.FitnessDuo.combination2;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GraphActivity extends AppCompatActivity {

    private static final String TAG = "GraphActivity";

    //To access database
    DatabaseHelper mDatabaseHelper;

    //For Graph
    LineChart lineChart;

    //For displaying dates
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yy", cal).toString();
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_graph);


        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        Cursor data = mDatabaseHelper.getData();

        ArrayList<String> weights = new ArrayList<>();
        final ArrayList<Long> dates = new ArrayList<>();

        while (data.moveToNext()) {
            weights.add(data.getString(3));
            dates.add(data.getLong(2));
        }

        if (!weights.isEmpty()) {
            //Graph Part
            List<Integer> daysSinceStart = new ArrayList<>();
            //SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
            Date startDate = new Date(dates.get(0));
            daysSinceStart.add(0);
            for (int i = 0; i < dates.size(); i++) {
                long diff = dates.get(i) - dates.get(0);
                String daysSinceStartString = "" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                daysSinceStart.add(Integer.parseInt(daysSinceStartString));
                lineChart = (LineChart) findViewById(net.FitnessDuo.combination2.R.id.lineChart);
                ArrayList<Entry> yVals = new ArrayList<>();
                if (!weights.isEmpty()) {
                    for (int j = 0; j < dates.size(); j++) {
                        yVals.add(new Entry(dates.get(j) , Float.parseFloat(weights.get(j))));
                    }
                }
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                LineDataSet lineDataSet1 = new LineDataSet(yVals, "Weight [lbs]");
                lineDataSet1.setColor(Color.BLACK);
                lineDataSet1.setCircleColor(Color.BLACK);
                lineDataSet1.setValueFormatter(new MyValueFormatter());
                lineDataSets.add(lineDataSet1);
                lineChart.setData(new LineData(lineDataSets));
                //Axis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        int v = Math.round(value);
                        if (v == 0) {
                            //return dates.get(0).substring(0,dates.get(0).length() - 5); //Do this or rotate the labels
                            //return getDate(dates.get(0)); //Do this or rotate the labels
                            //return Float.toString(value);
                            long longVersionOfValue = (long) value;
                            return getDate(longVersionOfValue);
                        } else {

                            //return getNextDateAfterDays(dates.get(0), v);
                            //return getNextDateAfterDays(dates.get(0), v).substring(0, getNextDateAfterDays(dates.get(0), v).length() - 5); //Do this or rotate the labels
                            //Log.d(TAG, "getFormattedValue: " + dates.get(0)+(value * 24 * 60 * 60 * 1000));
                            //long valueLong = (long) v;
                            //return getDate(dates.get(0) + (valueLong * 24 * 60 * 60 * 1000)); //Do this or rotate the labels
                            //return Float.toString(value);
                            long longVersionOfValue = (long) value;
                            return getDate(longVersionOfValue);
                        }
                    }
                });
                xAxis.setGranularity(1f);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.getDescription().setEnabled(false);
                lineChart.getLegend().setEnabled(false);
            }
        }

    }
}
