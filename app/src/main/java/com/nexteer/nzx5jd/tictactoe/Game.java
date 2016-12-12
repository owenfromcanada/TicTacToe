package com.nexteer.nzx5jd.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Game extends AppCompatActivity {
    int wins = 0;
    int losses = 0;
    int ties = 0;
    char player = 'X';
    char comp = 'O';
    int numMoves = 0;
    boolean thinking = false;
    boolean playing = false;
    int difficulty = 1;

    char[] board = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    final int[][] combos = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    final int[] boardId = {
            R.id.board0,
            R.id.board1,
            R.id.board2,
            R.id.board3,
            R.id.board4,
            R.id.board5,
            R.id.board6,
            R.id.board7,
            R.id.board8,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        char player = intent.getCharExtra("EXTRA_PLAYER", 'X');
        difficulty = intent.getIntExtra("EXTRA_DIFFICULTY", 1);

        if (player == 'X') {
            comp = 'O';
        }
        else {
            comp = 'X';
        }

        startGame();
    }

    boolean evaluate() {
        // check for winning condition

        for (int[] combo : combos) {
            if (board[combo[0]] != 0 &&
                    board[combo[0]] == board[combo[1]] &&
                    board[combo[0]] == board[combo[2]]) {

                // set color of winning combo
                for (int j = 0; j < 3; j++) {
                    Button button = (Button) findViewById(boardId[combo[j]]);
                    button.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                // update stats
                if (board[combo[0]] == player) {
                    wins++;
                }
                else {
                    losses++;
                }

                // update static data
                playing = false;
                thinking = false;

                return true;
            }
        }

        // check for tie game
        for (char cell : board) {
            if (cell == 0) {
                // if there is an empty space, not a tie yet
                return false;
            }
        }

        // update stats
        ties++;

        // update static data
        playing = false;
        thinking = false;

        return true;
    }

    int getRandom(Integer... ar) {
        return ar[(int)(Math.random() * ar.length)];
    }

    int checkWinningMove(char pl) {
        for (int[] combo : combos) {
            int c1 = board[combo[0]];
            int c2 = board[combo[1]];
            int c3 = board[combo[2]];
            if (c1 == 0 && c2 == pl && c3 == pl) return combo[0];
            if (c1 == pl) {
                if (c2 == 0 && c3 == pl) return combo[1];
                if (c2 == pl && c3 == 0) return combo[2];
            }
        }
        return -1;
    }

    // check for potential forks - return -1 if none
    int checkForks() {

        // assumes I always take the middle square first
        // so only available on highest difficulty

        if (numMoves == 1 && board[4] == player) {
            // if opponent took the center, take a corner
            return getRandom(0, 2, 6, 8);
        }

        if (numMoves == 3) {
            if (board[4] == player) {
                // if opponent has the center and the corner opposite
                // my corner, take a different corner
                if (board[0] != 0 && board[8] != 0) {
                    return getRandom(2, 6);
                }
                if (board[2] != 0 && board[6] != 0) {
                    return getRandom(0, 8);
                }
            }
            // if I have the center
            else {
                // if I have the center and opponent has opposite corners,
                // take one of the sides
                if ((board[0] == player && board[8] == player) ||
                        (board[2] == player && board[6] == player)) {
                    return getRandom(1, 3, 5, 7);
                }

                // if opponent has a corner and a far side, take the opposite corner
                if (board[0] == player) {
                    return 8;
                }
                if (board[2] == player) {
                    return 6;
                }
                if (board[6] == player) {
                    return 2;
                }
                if (board[8] == player) {
                    return 0;
                }

                // if opponent has two adjacent sides, pick one of the close corners
                if (board[1] == player && board[3] == player) {
                    return getRandom(0, 2, 6);
                }
                if (board[1] == player && board[5] == player) {
                    return getRandom(0, 2, 8);
                }
                if (board[3] == player && board[7] == player) {
                    return getRandom(0, 6, 8);
                }
                if (board[5] == player && board[7] == player) {
                    return getRandom(2, 6, 8);
                }

            }
        }

        if (numMoves == 2) {
            // if opponent is O and chooses a side, take a far corner
            if (board[1] == player) {
                return getRandom(6, 8);
            }
            if (board[3] == player) {
                return getRandom(2, 8);
            }
            if (board[5] == player) {
                return getRandom(0, 6);
            }
            if (board[7] == player) {
                return getRandom(0, 2);
            }
        }

        if (numMoves == 4) {
            // if opponent takes corner and I have opposite corner, fork
            if (board[0] == comp && board[8] == player) {
                if (board[5] == 0) return getRandom(2, 5);
                else return getRandom(6, 7);
            }
            if (board[8] == comp && board[0] == player) {
                if (board[1] == 0) return getRandom(1, 2);
                else return getRandom(3, 6);
            }
            if (board[2] == comp && board[6] == player) {
                if (board[1] == 0) return getRandom(0, 1);
                else return getRandom(5, 8);
            }
            if (board[6] == comp && board[2] == player) {
                if (board[3] == 0) return getRandom(0, 3);
                else return getRandom(7, 8);
            }
        }

        // no forks to consider
        return -1;
    }

    // gets a "good" move - returns -1 if it doesn't matter
    int checkGoodMove() {
        // one condition where we can set up a fork, but doesn't look "good"
        if (numMoves == 2) {
            if (board[0] == player || board[2] == player ||
                    board[6] == player || board[8] == player) {
                return -1;
            }
        }

        // get list of possible good moves
        // - a good move is one that creates a line of two
        ArrayList<Integer> moves = new ArrayList<>();
        for (int[] combo : combos) {
            int c1 = board[combo[0]];
            int c2 = board[combo[1]];
            int c3 = board[combo[2]];
            if (c1 == 0 && c2 == 0 && c3 == comp) {
                if (moves.indexOf(combo[0]) < 0) moves.add(combo[0]);
                if (moves.indexOf(combo[1]) < 0) moves.add(combo[1]);
            }
            else if (c1 == 0 && c2 == comp && c3 == 0) {
                if (moves.indexOf(combo[0]) < 0) moves.add(combo[0]);
                if (moves.indexOf(combo[2]) < 0) moves.add(combo[2]);
            }
            else if (c1 == comp && c2 == 0 && c3 == 0) {
                if (moves.indexOf(combo[1]) < 0) moves.add(combo[1]);
                if (moves.indexOf(combo[2]) < 0) moves.add(combo[2]);
            }
        }
        if (moves.size() > 0) return getRandom(moves.toArray(new Integer[moves.size()]));
        return -1;
    }

    // main AI function - get the next move
    int getMove() {

        int move;

        // check for winning move
        move = checkWinningMove(comp);
        if (move >= 0) {
            if (difficulty > 0) return move;
            else if (Math.random() < 0.8) return move;
        }

        // check for necessary blocking move
        move = checkWinningMove(player);
        if (move >= 0) {
            if (difficulty > 1) return move;
            else if (difficulty > 0 && Math.random() < 0.9) return move;
            else if (Math.random() < 0.6) return move;
        }

        // take center square if available
        if (board[4] == 0) {
            if (difficulty > 1) return 4;
            else if (difficulty > 0 && Math.random() < 0.8) return 4;
        }

        // check for forks
        if (difficulty > 1) {
            // checkForks makes assumptions based on difficulty > 1
            move = checkForks();
            if (move >= 0) return move;
        }

        // take a "good" square
        move = checkGoodMove();
        if (move >= 0) {
            if (difficulty > 0) return move;
        }

        // take a random square
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) possibleMoves.add(i);
        }
        return getRandom(possibleMoves.toArray(new Integer[possibleMoves.size()]));

    }

    // play the next move for the computer
    void playMove() {

        // get the next move
        int move = getMove();

        // sanity check
        if (move < 0) {
            thinking = false;
            return;
        }

        // set the move in static data
        board[move] = comp;
        numMoves++;

        // show the move
        Button button = (Button)findViewById(boardId[move]);
        button.setText(String.valueOf(comp));

        // post-processing
        evaluate();
        thinking = false;

    }

    // setup function for a new game
    void startGame() {

        // clear board
        for (int i = 0; i < board.length; i++) {
            board[i] = 0;
            Button button = (Button)findViewById(boardId[i]);
            button.setTextColor(getResources().getColor(R.color.colorPrimary));
            button.setText("");
        }

        // set static data
        playing = true;
        numMoves = 0;

        // if computer is 'X', make the first move
        if (comp == 'X') {
            thinking = true;
            playMove();
        }

    }

    // button click handler
    public void buttonClick(View view) {
        Button button = (Button)view;

        // if we're busy, ignore the click
        if (thinking) {
            return;
        }

        // if we've finished a game, start a new one
        if (!playing) {
            startGame();
            return;
        }

        // get cell number
        int cell = -1;
        for (int i = 0; i < boardId.length; i++) {
            if (boardId[i] == button.getId()) {
                cell = i;
            }
        }

        if (cell < 0) {
            return;
        }

        // ignore the click if it was on an occupied cell
        if (board[cell] != 0) {
            return;
        }

        // set indicator to ignore further clicks until ready
        thinking = true;

        // set static data
        board[cell] = player;
        numMoves++;

        // show the move
        button.setText(String.valueOf(player));

        // if game isn't over, play a move
        if (!evaluate()) {
            playMove();
        }
    }
}
