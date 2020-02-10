package com.brandt.powerboxcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter<CharSequence> adapter;

    int selectedPowerBoxId;
    boolean channelOnState[][];

    public void MainActivity() {
        System.out.println("Starting MainActivity ctor()");

        channelOnState = new boolean[2][8];
        printCurrentPowerBoxState();

        System.out.println("MainActivity ctor() finished");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.box_selection_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
            R.array.PowerBox_IDs, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        channelOnState = new boolean[2][8];
        printCurrentPowerBoxState();

        out.println("MainActivity setup complete.");
    }

    public void printCurrentPowerBoxState() {
        System.out.println("Current PowerBox Channel states:");
        for(int i=0; i<channelOnState.length; i++) {
            System.out.println("   PowerBox: " + (int)(i+1));
            for(int j=0; j<channelOnState[i].length; j++) {
                String channelState;
                if (channelOnState[i][j]) {
                   channelState = "ON";
                }
                else {
                    channelState = "OFF";
                }
                System.out.println("      Channel " + (int)(j+1) + ": " + channelState);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        out.println("OnItemSelected called. pos: " + pos + " id: " + id);
        selectedPowerBoxId = Integer.parseInt(adapter.getItem(pos).toString());
        out.println("PowerBox " + selectedPowerBoxId + " selected");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        out.println("OnNothingSelected called.");
    }

    public void changeChannelPowerState(View view) {
        Switch s = (Switch) view;
        Integer channelId = Character.getNumericValue(s.getText().charAt(8));

        System.out.println("Adjusting PowerBox " + selectedPowerBoxId + " channel " + channelId);

        channelOnState[selectedPowerBoxId-1][channelId-1] = !channelOnState[selectedPowerBoxId-1][channelId-1];

        printCurrentPowerBoxState();

    }

}
