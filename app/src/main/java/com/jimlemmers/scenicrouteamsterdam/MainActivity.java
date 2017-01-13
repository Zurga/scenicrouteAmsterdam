package com.jimlemmers.scenicrouteamsterdam;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO add address autocompletion to the text fields.
        Button previewButton = (Button) findViewById(R.id.preview_route_button);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText from = (EditText) findViewById(R.id.from);
                EditText to = (EditText) findViewById(R.id.to);
                //if (from.getText().toString() != "" & to.getText().toString() != "") {
                    // TODO get all the routing from the backend
                    String test_from = "52.356077,4.9534857";
                    String test_to = "52.3578326,4.8743141";
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    intent.putExtra("from", test_from);
                    intent.putExtra("to", test_to);
                    intent.putExtra("preview", true);
                    startActivity(intent);
                //}
            }
        });
    }
    public void createLocationFromAddress(String address){
        //Location createLocationFromAdress(String address){
        //TODO add all the code necessary.
    }
}