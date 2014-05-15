package com.battleship.app;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.battleship.config.GlobalState;

public class ConfigActivity extends ActionBarActivity {
    Button applyBtn;
    GlobalState config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        config = GlobalState.getInstance();

        EditText ip       = (EditText)findViewById(R.id.ip_addr);
        EditText username = (EditText)findViewById(R.id.username);

        ip.setText(GlobalState.getInstance().server);
        username.setText(GlobalState.getInstance().playerName);

        applyBtn = (Button)findViewById(R.id.apply_btn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText ip       = (EditText)findViewById(R.id.ip_addr);
                EditText username = (EditText)findViewById(R.id.username);

                config.setServerAddress(ip.getText().toString());
                config.setPlayerName(username.getText().toString());

                SharedPreferences preferences = getSharedPreferences("BattleshipPrefs", MODE_PRIVATE);
                SharedPreferences.Editor edit= preferences.edit();

                edit.putString("PlayerName", config.getPlayerName());
                edit.putString("ServerAddress", config.getServerAddress());
                edit.commit();

                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
