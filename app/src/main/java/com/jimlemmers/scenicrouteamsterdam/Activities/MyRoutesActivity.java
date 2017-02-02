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
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupApis();
        mRefString = "my_routes";
        mRef = database.getReference(mRefString).child(getUid());
        adapter = new RouteAdapter(MyRoutesActivity.this, new ArrayList<Route>(),
                mRef, this, this);
        ListView listView = (ListView) findViewById(R.id.route_list);
        listView.setAdapter(adapter);
    }
}
