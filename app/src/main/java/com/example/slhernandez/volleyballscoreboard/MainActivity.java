package com.example.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Team teamA;
    private Team teamB;
    private int limitSets;
    private String lastTeam;

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

    private View.OnTouchListener mSetListener = new View.OnTouchListener() {
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
                    addSet(team);
                    break;
                case "SUB":
                    subtractSet(team);
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

        this.limitSets = 3;
        this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
        this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
        this.teamA.setScores(limitSets);
        this.teamB.setScores(limitSets);
        this.lastTeam = "";

        setBoard();

        final TextView scoreA = findViewById(R.id.score_team_a);
        scoreA.setOnTouchListener(mScoreListener);

        final TextView scoreB = findViewById(R.id.score_team_b);
        scoreB.setOnTouchListener(mScoreListener);

        final TextView setA = findViewById(R.id.set_team_a);
        setA.setOnTouchListener(mSetListener);

        final TextView setB = findViewById(R.id.set_team_b);
        setB.setOnTouchListener(mSetListener);
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
        int limitPoints = (sets < this.limitSets - 1) ? 25 : 15;
        if ((this.teamA.getScore() < limitPoints && this.teamB.getScore() < limitPoints) || diffScores < 2) {
            team.setScore(team.getScore() + 1);
            this.lastTeam = current_team;
        }
        setBoard();
    }

    private void subtractPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getScore() > 0 && current_team.equals(this.lastTeam)) {
            team.setScore(team.getScore() - 1);
            this.lastTeam = "";
        }
        setBoard();
    }

    private void addSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        int diffScores = Math.abs(this.teamA.getScore() - this.teamB.getScore());
        int sets = this.teamA.getSet() + this.teamB.getSet();
        int limitPoints = (sets < this.limitSets - 1) ? 25 : 15;
        if (sets < this.limitSets && team.getScore() >= limitPoints && diffScores >= 2) {
            this.teamA.setScoresIndex(sets, this.teamA.getScore());
            this.teamB.setScoresIndex(sets, this.teamB.getScore());
            team.setSet(team.getSet() + 1);
            sets = this.teamA.getSet() + this.teamB.getSet();
            if (sets < this.limitSets) {
                this.teamA.setScore(0);
                this.teamB.setScore(0);
            }
            this.lastTeam = current_team;
        }
        setBoard();
    }

    private void subtractSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getSet() > 0 && current_team.equals(this.lastTeam)) {
            team.setSet(team.getSet() - 1);
            int sets = this.teamA.getSet() + this.teamB.getSet();
            this.teamA.setScore(this.teamA.getScoresIndex(sets));
            this.teamB.setScore(this.teamB.getScoresIndex(sets));
            this.lastTeam = current_team;
        }
        setBoard();
    }
}
