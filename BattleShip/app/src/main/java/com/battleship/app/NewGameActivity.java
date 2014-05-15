package com.battleship.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.battleship.config.Board;
import com.battleship.config.GlobalState;
import com.battleship.config.Game;
import com.battleship.config.ServerThread;
import com.battleship.config.Ship;

import java.util.ArrayList;
import java.util.logging.Handler;

public class NewGameActivity extends ActionBarActivity {
    public Button start;
    public Button clear;
    public static ImageView[][] imageMap = new ImageView[10][10];
    public Handler uiHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        setToFalse(GlobalState.getInstance().boolMap);

        GlobalState.getInstance().newGameActivity = this;

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        TableLayout setup_matrix = (TableLayout) getWindow().getDecorView().findViewById(R.id.setup_matrix);

        for(int i = 0; i < 10; i++) {
            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(rowParams);
            for(int j = 0; j < 10; j++) {
                ImageView img = new ImageView(getApplicationContext());
                img.setBackgroundResource(R.drawable.white);
                img.setTag(new Integer(i * 10 + j));

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ImageView img = (ImageView) arg0;
                        boolean boolMap[][] = GlobalState.getInstance().boolMap;
                        Integer count = (Integer) img.getTag();
                        if (boolMap[count / 10][count % 10] == false) {
                            img.setBackgroundResource(R.drawable.black);
                        } else {
                            img.setBackgroundResource(R.drawable.white);
                        }
                        boolMap[count / 10][count % 10] = !boolMap[count / 10][count % 10];

                        Board eb = GlobalState.getInstance().emptyBoard;
                        boolean [][] tmp = new boolean[10][10];
                        for (int i = 0; i < 10; i++)
                            for (int j = 0; j < 10; j++)
                                tmp[i][j] = boolMap[i][j];
                        ArrayList<Ship> ships = eb.load_board(tmp);
                        if (eb.fullShips(ships)) {
                            GlobalState.getInstance().game = new Game(boolMap);
                            start.setEnabled(true);
                        }
                        else
                            start.setEnabled(false);
                    }
                });

                row.addView(img);
                imageMap[i][j] = img;
            }
            setup_matrix.addView(row);
        }

        start = (Button) findViewById(R.id.setup_btn);
        clear = (Button)findViewById(R.id.clear_btn);

        start.setEnabled(false);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                start.setEnabled(false);
                clear.setEnabled(false);
                start.setText("Waiting for enemy...");
                (new Thread(new ServerThread())).start();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setEnabled(false);

                setToFalse(GlobalState.getInstance().boolMap);
                for (int i = 0; i < 10; ++i) {
                    for (int j = 0; j < 10; ++j) {
                        imageMap[i][j].setBackgroundResource(R.drawable.white);
                    }
                }
            }
        });
    }

    protected void setToFalse(boolean[][] map) {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                map[i][j] = false;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                ViewGroup parent = (ViewGroup) imageMap[i][j].getParent();
                int index = parent.indexOfChild(imageMap[i][j]);
                parent.removeViewAt(index);
                imageMap[i][j].setClickable(false);
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_game, menu);
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
