package com.example.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        configInitial();

        final TextView scoreA = findViewById(R.id.score_team_a);
        scoreA.setOnTouchListener(mScoreListener);

        final TextView scoreB = findViewById(R.id.score_team_b);
        scoreB.setOnTouchListener(mScoreListener);

        final TextView setA = findViewById(R.id.set_team_a);
        setA.setOnTouchListener(mSetListener);

        final TextView setB = findViewById(R.id.set_team_b);
        setB.setOnTouchListener(mSetListener);

        final Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.reset)
                        .setMessage(R.string.really_reset)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                configInitial();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                final AlertDialog dialog = builder.create();
                try {
                    dialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", e.getMessage());
                }
            }
        });
    }

    private void configInitial() {
        this.limitSets = 3;
        this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
        this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
        this.teamA.setScores(limitSets);
        this.teamB.setScores(limitSets);
        this.lastTeam = "";
        configBoard();
        configScores();
    }

    private void configBoard() {
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
        configBoard();
    }

    private void subtractPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getScore() > 0 && current_team.equals(this.lastTeam)) {
            team.setScore(team.getScore() - 1);
            this.lastTeam = "";
        }
        configBoard();
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
            configScores();
        }
        configBoard();
    }

    private void subtractSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        int sets;
        if (team.getSet() > 0 && current_team.equals(this.lastTeam)) {
            sets = this.teamA.getSet() + this.teamB.getSet();
            this.teamA.setScore(this.teamA.getScoresIndex(sets - 1));
            this.teamB.setScore(this.teamB.getScoresIndex(sets - 1));
            this.teamA.setScoresIndex(sets - 1, 0);
            this.teamB.setScoresIndex(sets - 1, 0);
            team.setSet(team.getSet() - 1);
            this.lastTeam = current_team;
            configScores();
        }
        configBoard();
    }

    public void configScores() {
        int id_a, id_b;
        String text_a, text_b;
        for (int set = 0; set < this.limitSets; set++) {
            switch (set) {
                case 1:
                    id_a = R.id.set2_team_a;
                    id_b = R.id.set2_team_b;
                    break;
                case 2:
                    id_a = R.id.set3_team_a;
                    id_b = R.id.set3_team_b;
                    break;
                default:
                    id_a = R.id.set1_team_a;
                    id_b = R.id.set1_team_b;
                    break;
            }
            text_a = (this.teamA.getScoresIndex(set) == 0) ? getString(R.string.empty_score) : "" + this.teamA.getScoresIndex(set);
            text_b = (this.teamB.getScoresIndex(set) == 0) ? getString(R.string.empty_score) : "" + this.teamB.getScoresIndex(set);
            final TextView scoreA = findViewById(id_a);
            scoreA.setText(text_a);
            final TextView scoreB = findViewById(id_b);
            scoreB.setText(text_b);
        }
    }
}
