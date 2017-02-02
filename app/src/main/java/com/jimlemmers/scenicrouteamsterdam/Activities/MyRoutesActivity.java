package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;
import com.jimlemmers.scenicrouteamsterdam.Adapters.RouteAdapter;
import com.jimlemmers.scenicrouteamsterdam.R;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;

import java.util.ArrayList;

/**
 * This activity will display the saved routes from the user.
 */

public class MyRoutesActivity extends BaseActivity {
    public String TAG = "MyRoutesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupApis();
        if (user != null) {
            DatabaseReference myRef = database.getReference("my_routes").child(getUid());
            adapter = new RouteAdapter(MyRoutesActivity.this, new ArrayList<Route>(),
                    myRef, this, this);
            ListView listView = (ListView) findViewById(R.id.route_list);
            listView.setAdapter(adapter);
        }
    }
}
