package com.brandt.powerboxcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter<CharSequence> adapter;

    private int selectedPowerBoxId = 1;
    private boolean currentChannelState[][];
    private String powerBoxAddress;

    public void MainActivity() {
        System.out.println("Starting MainActivity ctor()");

        printCurrentPowerBoxState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.box_selection_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
            R.array.PowerBox_IDs, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        // Set the PowerBox address based on the PowerBox Id selected
        powerBoxAddress = "http://" + getApplicationContext().getResources().getStringArray(R.array.PowerBoxAddresses)[selectedPowerBoxId-1];

        currentChannelState = new boolean[2][16];
        printCurrentPowerBoxState();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = powerBoxAddress + "/PowerBox/getChannelStatus";
        JsonObjectRequest changeChannelStateReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateCurrentPowerBoxState(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST Response", error.toString());
                    }
                }
        );
        requestQueue.add(changeChannelStateReq);

    }

    public void printCurrentPowerBoxState() {
        System.out.println("Current PowerBox Channel states:");
        for(int i=0; i<currentChannelState.length; i++) {
            System.out.println("   PowerBox: " + (i+1));
            for(int j=0; j<currentChannelState[i].length; j++) {
                String channelState;
                if (currentChannelState[i][j]) {
                   channelState = "ON";
                }
                else {
                    channelState = "OFF";
                }
                System.out.println("      Channel " + (j+1) + ": " + channelState);
            }
        }
    }

    public void updateCurrentPowerBoxState(JSONObject powerBoxState) {
        System.out.println(powerBoxState);
        try {
            for(int i=0; i<powerBoxState.length(); i++) {
                boolean newChannelState = false;
                if(powerBoxState.get(String.valueOf(i+1)).toString().equals("ON")) {
                    newChannelState = true;
                    System.out.println("Channel " + i + " is ON!");
                }
                else {
                    System.out.println("Channel " + i + " is OFF!");
                }
                currentChannelState[selectedPowerBoxId-1][i] = newChannelState;
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }

        printCurrentPowerBoxState();

        // Set Switch states
        Switch channelSwitch;
        channelSwitch = findViewById(R.id.switch1);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][0]);
        channelSwitch = findViewById(R.id.switch2);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][1]);
        channelSwitch = findViewById(R.id.switch3);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][2]);
        channelSwitch = findViewById(R.id.switch4);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][3]);
        channelSwitch = findViewById(R.id.switch5);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][4]);
        channelSwitch = findViewById(R.id.switch6);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][5]);
        channelSwitch = findViewById(R.id.switch7);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][6]);
        channelSwitch = findViewById(R.id.switch8);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][7]);
        channelSwitch = findViewById(R.id.switch9);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][8]);
        channelSwitch = findViewById(R.id.switch10);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][9]);
        channelSwitch = findViewById(R.id.switch11);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][10]);
        channelSwitch = findViewById(R.id.switch12);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][11]);
        channelSwitch = findViewById(R.id.switch13);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][12]);
        channelSwitch = findViewById(R.id.switch14);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][13]);
        channelSwitch = findViewById(R.id.switch15);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][14]);
        channelSwitch = findViewById(R.id.switch16);
        channelSwitch.setChecked(currentChannelState[selectedPowerBoxId-1][15]);

    }

    // onItemSelected() is called when a power box is selected from the spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        out.println("OnItemSelected called. pos: " + pos + " id: " + id);
        selectedPowerBoxId = Integer.parseInt(adapter.getItem(pos).toString());
        out.println("PowerBox " + selectedPowerBoxId + " selected");

        // Set the powerBoxAddress to the correct address (specified in strings.xml) based on the
        // PowerBox selected.
        powerBoxAddress = "http://" + getApplicationContext().getResources().getStringArray(R.array.PowerBoxAddresses)[selectedPowerBoxId-1];
        out.println("PowerBox " + selectedPowerBoxId + " address: " + powerBoxAddress);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = powerBoxAddress + "/PowerBox/getChannelStatus";
        JsonObjectRequest changeChannelStateReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateCurrentPowerBoxState(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST Response", error.toString());
                    }
                }
        );
        requestQueue.add(changeChannelStateReq);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        out.println("OnNothingSelected called.");
    }

    // changeChannelPowerState() is called when a channel switch is flipped.
    public void changeChannelPowerState(View view) {
        Switch s = (Switch) view;
        Integer channelId = Integer.parseInt(s.getText().toString().substring(8).trim());

        System.out.println("Adjusting PowerBox " + selectedPowerBoxId + " channel " + channelId);

        currentChannelState[selectedPowerBoxId-1][channelId-1] = !currentChannelState[selectedPowerBoxId-1][channelId-1];

        String cmd;
        if (currentChannelState[selectedPowerBoxId-1][channelId-1]) {
            cmd = "ON";
        }
        else {
            cmd = "OFF";
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = powerBoxAddress + "/PowerBox/setChannelState";

        JSONObject data = new JSONObject();
        try {
            data.put("channel", channelId);
            data.put("state", cmd);
        }
        catch(Exception e) {
            //do nothing
        }

        JsonObjectRequest changeChannelStateReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateCurrentPowerBoxState(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST Response", error.toString());
                    }
                }
        );
        requestQueue.add(changeChannelStateReq);

    }

}
