package com.brandt.powerboxcontrol;

import androidx.appcompat.app.AppCompatActivity;

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

    int selectedPowerBoxId = 1;
    boolean currentChannelState[][];
    String host = "http://192.168.86.67:5000";

    public void MainActivity() {
        System.out.println("Starting MainActivity ctor()");

        currentChannelState = new boolean[2][8];
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

        currentChannelState = new boolean[1][8];
        printCurrentPowerBoxState();
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
                }
                currentChannelState[selectedPowerBoxId-1][i] = newChannelState;
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }

        printCurrentPowerBoxState();

    }

    // onItemSelected() is called when a power box is selected from the spinner
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

    // changeChannelPowerState() is called when a channel switch is flipped.
    public void changeChannelPowerState(View view) {
        Switch s = (Switch) view;
        Integer channelId = Character.getNumericValue(s.getText().charAt(8));

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
        String url = host + "/PowerBox/setChannelState";

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
