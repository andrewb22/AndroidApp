package net.FitnessDuo.combination2;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        //mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        mFormat = new DecimalFormat("###,###,###"); // Does not use a decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        //return mFormat.format(value) + " $"; // e.g. append a dollar-sign
        return mFormat.format(value) + " lbs";
    }
}
