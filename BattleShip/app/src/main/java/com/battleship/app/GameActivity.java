package com.battleship.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.battleship.config.Board;
import com.battleship.config.Cell;
import com.battleship.config.CellState;
import com.battleship.config.GlobalState;

public class GameActivity extends Activity {
    public ImageView[][] enemyMap   = new ImageView[10][10];
    public boolean enemyBoolMap[][] = new boolean[10][10];

    public ImageView[][] myMap   = new ImageView[10][10];
    public boolean myBoolMap[][] = new boolean[10][10];

    boolean myView = false;

    TableLayout currentMap;
    TableRow rows[] = new TableRow[10];
    TableLayout.LayoutParams rowParams;

    CharSequence enemyLabel = "Your turn!";
    CharSequence myLabel    = "Waiting for ";

    public Button surrender;
    public TextView finalMessage;
    public LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GlobalState.getInstance().gameActivity = this;

        finalMessage = (TextView) findViewById(R.id.final_msg);

        surrender = (Button) findViewById(R.id.surrender_btn);
        surrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalState.getInstance().socketOut.println("result:fail");
                finish();
            }
        });

        myBoolMap = GlobalState.getInstance().boolMap;

        rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        currentMap = (TableLayout) getWindow().getDecorView().findViewById(R.id.current_map);

        setupMap();
        setupEnemy();
        if (GlobalState.getInstance().to_move == false)
            switchViews();
    }

    public void setupMap() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                ImageView img = new ImageView(getApplicationContext());
                if (GlobalState.getInstance().game.me.enemy_board[i][j].state == CellState.UNDEFINED)
                    img.setBackgroundResource(R.drawable.white);
                else if (GlobalState.getInstance().game.me.enemy_board[i][j].state == CellState.MISSED)
                    img.setBackgroundResource(R.drawable.missed);
                else if (GlobalState.getInstance().game.me.enemy_board[i][j].state == CellState.DAMAGED)
                    img.setBackgroundResource(R.drawable.damaged);
                else if (GlobalState.getInstance().game.me.enemy_board[i][j].state == CellState.DEAD)
                    img.setBackgroundResource(R.drawable.dead);
                else if (GlobalState.getInstance().game.me.enemy_board[i][j].state == CellState.INTACT)
                    img.setBackgroundResource(R.drawable.black);
                myMap[i][j] = img;
            }
        }
    }

    public void updateMap(Cell[][] board, ImageView[][] map) {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(board[i][j].state == CellState.MISSED) {
                    map[i][j].setBackgroundResource(R.drawable.missed);
                } else if(board[i][j].state == CellState.DAMAGED) {
                    map[i][j].setBackgroundResource(R.drawable.damaged);
                } else if(board[i][j].state == CellState.DEAD) {
                    map[i][j].setBackgroundResource(R.drawable.dead);
                } else if(board[i][j].state == CellState.INTACT) {
                    map[i][j].setBackgroundResource(R.drawable.black);
                } else if(board[i][j].state == CellState.UNDEFINED){
                    map[i][j].setBackgroundResource(R.drawable.white);
                }
                //map[i][j].invalidate();
            }
        }
        getWindow().getDecorView().findViewById(R.id.game_layout).invalidate();
    }

    public void setupEnemy() {
        for(int i = 0; i < 10; i++) {
            rows[i] = new TableRow(getApplicationContext());
            rows[i].setLayoutParams(rowParams);
            for(int j = 0; j < 10; j++) {
                ImageView img = new ImageView(getApplicationContext());
                if (GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.UNDEFINED)
                    img.setBackgroundResource(R.drawable.white);
                else if (GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.MISSED)
                    img.setBackgroundResource(R.drawable.missed);
                else if (GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.DAMAGED)
                    img.setBackgroundResource(R.drawable.damaged);
                else if (GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.DEAD)
                    img.setBackgroundResource(R.drawable.dead);
                else if (GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.INTACT)
                    img.setBackgroundResource(R.drawable.black);
                img.setTag(new Integer(i * 10 + j));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ImageView img = (ImageView) arg0;
                        Integer count = (Integer) img.getTag();
                        GlobalState.getInstance().socketOut.println("move:" + (count / 10) + ":" + (count % 10));
                        enemyBoolMap[count / 10][count % 10] = true;
                        setClickable(true);
                    }
                });

                rows[i].addView(img);
                enemyMap[i][j] = img;
                enemyBoolMap[i][j] = false;
            }
            currentMap.addView(rows[i]);
        }
    }

    public void setClickable(boolean lock) {
        if(lock) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (!enemyBoolMap[i][j]) {
                        enemyMap[i][j].setClickable(false);
                    }
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (!enemyBoolMap[i][j]) {
                        enemyMap[i][j].setClickable(GlobalState.getInstance().game.enemy.enemy_board[i][j].state == CellState.UNDEFINED);
                    }
                }
            }
        }
    }

    public void displayFinalMessage(String message) {
        ViewGroup parent = (ViewGroup) surrender.getParent();
        parent.removeView(surrender);

//        LinearLayout layout = (LinearLayout) findViewById(R.id.game_layout);
//        TextView textView = new TextView(this);
//        textView.setText(message);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        layout.addView(textView);
        finalMessage.setText(message);
        finalMessage.setVisibility(View.VISIBLE);
    }

    public void switchViews() {
        myView = !myView;
        for(int i = 0; i < 10; i++) {
            rows[i].removeAllViews();
            for(int j = 0; j < 10; j++) {
                if (myView) {
                    rows[i].addView(myMap[i][j], j);
                } else {
                    rows[i].addView(enemyMap[i][j], j);
                }
            }
        }

        TextView label = (TextView) findViewById(R.id.map_title);
        if(myView) {
            label.setText(myLabel + GlobalState.getInstance().enemyName + "...");
        } else {
            label.setText(enemyLabel);
        }

        if(myView)
            setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
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
