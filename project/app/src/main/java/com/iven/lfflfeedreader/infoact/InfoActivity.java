package com.iven.lfflfeedreader.infoact;

import com.iven.lfflfeedreader.R;
import com.iven.lfflfeedreader.utils.Preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class InfoActivity extends AppCompatActivity {

    //ContextThemeWrapper
    ContextThemeWrapper themewrapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //apply preferences

        //apply activity's theme if dark theme is enabled
        themewrapper = new ContextThemeWrapper(getBaseContext(), this.getTheme());
        Preferences.applyTheme(themewrapper, getBaseContext());

        //set the navbar tint if the preference is enabled
        Preferences.applyNavTint(this, getBaseContext());

        //set LightStatusBar
        Preferences.applyLightIcons(this);

        //set the view
        setContentView(R.layout.activity_settings);

        //set the toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

        //provide back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });

        //set toolbar title
        getSupportActionBar().setTitle(R.string.settings);
    }

}
