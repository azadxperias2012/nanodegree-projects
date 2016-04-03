package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.StockDataSet;

/**
 * Created by aabbasal on 3/27/2016.
 */
public class DetailStocksActivity extends AppCompatActivity {

    private LineChartView mLineChartView;

    //private String[] mLabels = { "Previous Close", "OPEN", QuoteColumns.DAYSLOW, QuoteColumns.DAYSHIGH, QuoteColumns.BIDPRICE};
    private String[] mLabels = { "Close", "Open", "Low", "High", "Actual"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        StockDataSet stockDataSet = (StockDataSet) intent.getSerializableExtra(MyStocksActivity.STOCK_DATA_SET);

        mLineChartView = (LineChartView) findViewById(R.id.linechart);

        float[] values = {
                Float.parseFloat(stockDataSet.getPreviousClosePrice()),
                Float.parseFloat(stockDataSet.getOpenPrice()),
                Float.parseFloat(stockDataSet.getDaysLowPrice()),
                Float.parseFloat(stockDataSet.getDaysHighPrice()),
                Float.parseFloat(stockDataSet.getBidPrice())
        };

        Double lowValue = new Double(Double.parseDouble(stockDataSet.getDaysLowPrice()));
        Double highValue = new Double(Double.parseDouble(stockDataSet.getDaysHighPrice()));
        Double step = Double.valueOf(Math.round((highValue - lowValue) / 9));

        LineSet dataSet = new LineSet(mLabels, values);
        dataSet.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f,10f});

        mLineChartView.addData(dataSet);
        if(step > 0) {
            mLineChartView.setAxisBorderValues((lowValue.intValue() - 1), (highValue.intValue() + 1), step.intValue());
        } else {
            mLineChartView.setAxisBorderValues((lowValue.intValue() - 1), (highValue.intValue() + 1));
        }
        mLineChartView.show();
    }
}
