package com.example.itsme.steketid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final double TIME_BETWEEN_PIZZAS  = 2.63;
    private static final double TIME_THROUGH_OVEN = 7.88;
    private static final int MAX_PIZZAS = 120;
    private static final int MAX_OVENS = 5;
    private static final int MIN_PIZZAS = 1;
    private static final int MIN_OVENS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberPicker nPizzas_NumP = (NumberPicker) findViewById(R.id.pizzanumber_numberpicker);
        nPizzas_NumP.setMaxValue(MAX_PIZZAS);
        nPizzas_NumP.setMinValue(MIN_PIZZAS);
        NumberPicker nOvens_NumP = (NumberPicker) findViewById(R.id.ovennumber_numberpicker);
        nOvens_NumP.setMaxValue(MAX_OVENS);
        nOvens_NumP.setMinValue(MIN_OVENS);
        TextView nPizzas_TextV = (TextView) findViewById(R.id.pizzanumber_textview);
        TextView nOvens_TextV = (TextView) findViewById(R.id.ovennumber_textview);

    }
}
