package com.example.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Team teamA;
    private Team teamB;
    private int limitSets;

    private View.OnTouchListener mScoreListener = new View.OnTouchListener() {
        private float y1;
        private float y2;
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String action = "";
            String team = v.getTag().toString();
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y1 = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    y2 = event.getY();
                    if (y1 < y2) {
                        // DOWN
                        action = "SUB";
                    }
                    if (y1 > y2) {
                        // UP
                        action = "ADD";
                    }
                    break;
            }
            switch (action) {
                case "ADD":
                    addPoint(team);
                    break;
                case "SUB":
                    subtractPoint(team);
                    break;
            }
            return true;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
        this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
        this.limitSets = 3;

        setBoard();

        final TextView scoreA = findViewById(R.id.score_team_a);
        scoreA.setOnTouchListener(mScoreListener);

        final TextView scoreB = findViewById(R.id.score_team_b);
        scoreB.setOnTouchListener(mScoreListener);
    }

    private void setBoard() {
        final TextView scoreA = findViewById(R.id.score_team_a);
        scoreA.setText(this.teamA.getScoreText());
        final TextView setA = findViewById(R.id.set_team_a);
        setA.setText(this.teamA.getSetText());
        final TextView scoreB = findViewById(R.id.score_team_b);
        scoreB.setText(this.teamB.getScoreText());
        final TextView setB = findViewById(R.id.set_team_b);
        setB.setText(this.teamB.getSetText());
    }

    private void addPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        int diffScores = Math.abs(this.teamA.getScore() - this.teamB.getScore());
        int sets = this.teamA.getSet() + this.teamB.getSet();
        int limitPoints = (sets < this.limitSets) ? 25 : 15;
        if ((this.teamA.getScore() < limitPoints && this.teamB.getScore() < limitPoints) || diffScores < 2) {
            team.setScore(team.getScore() + 1);
        }
        setBoard();
    }

    private void subtractPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getScore() > 0) {
            team.setScore(team.getScore() - 1);
        }
        setBoard();
    }
}
