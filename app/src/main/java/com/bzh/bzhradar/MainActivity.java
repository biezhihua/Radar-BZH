package com.bzh.bzhradar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RadarView radarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radarView = (RadarView) findViewById(R.id.radarView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_3:
                radarView
                        .setTitles(new String[]{"一", "二", "三"})
                        .setValues(new int[]{100, 80, 60});
                break;
            case R.id.action_4:
                radarView
                        .setTitles(new String[]{"一", "二", "三", "四"})
                        .setValues(new int[]{100, 80, 60, 40});
                break;
            case R.id.action_5:
                radarView
                        .setTitles(new String[]{"一", "二", "三", "四", "五"})
                        .setValues(new int[]{100, 80, 60, 40, 60});
                break;
            case R.id.action_6:
                radarView
                        .setTitles(new String[]{"一", "二", "三", "四", "五", "六"})
                        .setValues(new int[]{100, 80, 60, 40, 60, 78});
                break;
            case R.id.action_7:
                radarView
                        .setTitles(new String[]{"一", "二", "三", "四", "五", "六", "七"})
                        .setValues(new int[]{100, 80, 60, 40, 60, 78, 99});
                break;

        }
        return true;
    }

}
