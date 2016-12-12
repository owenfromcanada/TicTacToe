package com.nexteer.nzx5jd.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, Game.class);

        char player;
        if (((RadioGroup)findViewById(R.id.player)).getCheckedRadioButtonId() == R.id.playerO) {
            player = 'O';
        }
        else {
            player = 'X';
        }

        intent.putExtra("EXTRA_PLAYER", player);

        int difficulty;
        int diff = ((RadioGroup)findViewById(R.id.difficulty)).getCheckedRadioButtonId();
        if (diff == R.id.diffgenius) {
            difficulty = 2;
        }
        else if (diff == R.id.diffsmart) {
            difficulty = 1;
        }
        else {
            difficulty = 0;
        }
        intent.putExtra("EXTRA_DIFFICULTY", difficulty);

        startActivity(intent);
    }
}
