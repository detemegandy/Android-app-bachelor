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
    private TextView outPutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get output textview
        outPutTextView = findViewById(R.id.output_textview);

        //get numberpickers and set min and max values and no wrapping of values
        final NumberPicker nPizzas_NumP = findViewById(R.id.pizzanumber_numberpicker);
        nPizzas_NumP.setMaxValue(MAX_PIZZAS);
        nPizzas_NumP.setMinValue(MIN_PIZZAS);
        nPizzas_NumP.setWrapSelectorWheel(false);
        final NumberPicker nOvens_NumP = findViewById(R.id.ovennumber_numberpicker);
        nOvens_NumP.setMaxValue(MAX_OVENS);
        nOvens_NumP.setMinValue(MIN_OVENS);
        nOvens_NumP.setWrapSelectorWheel(false);

        //listener for changes and update text for both numberPickers
        NumberPicker.OnValueChangeListener listener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                //get numberpickers values
                int nOfPizzas = nPizzas_NumP.getValue();
                int nOfOvens = nOvens_NumP.getValue();

                //get time through ovens
                int time = timeNOvens(nOfPizzas, nOfOvens);

                //get hours through oven
                int hours = (int)Math.floor((double)time/(double)60);

                //get minutes through ovens
                int minutes = time % 60;

                String output = "Du trenger " + ((hours > 0) ?  hours + " timer og ":"") + minutes + " minutter for å steke " + nOfPizzas + " pizza.";

                outPutTextView.setText(output);
            }
        };

        //add listener to both NumberPickers
        nPizzas_NumP.setOnValueChangedListener(listener);
        nOvens_NumP.setOnValueChangedListener(listener);


    }

    //time through one oven
    int timeOneOven (int nOfPizzas){
        return (int)Math.ceil(TIME_THROUGH_OVEN + (TIME_BETWEEN_PIZZAS * (double) nOfPizzas));
    }

    //time through n-ovens
    int timeNOvens (int nOfPizzas, int nOfOvens) {
        return timeOneOven((int)Math.ceil(((double)nOfPizzas / (double)nOfOvens)));
    }

}
