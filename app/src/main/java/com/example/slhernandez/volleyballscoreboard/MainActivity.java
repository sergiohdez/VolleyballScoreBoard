package com.example.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Team teamA;
    private Team teamB;
    private int limitSets;
    private String lastTeam;
    private int currentSet;
    private boolean isPlaying;

    private final View.OnTouchListener mScoreListener = new View.OnTouchListener() {
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

    private final View.OnTouchListener mSetListener = new View.OnTouchListener() {
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
        this.limitSets = 3;

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
                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.reset_title)
                            .setMessage(R.string.really_reset)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    configInitial();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null);
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface xdialog) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3f51b5"));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3f51b5"));
                        }
                    });
                    dialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in Dialog:", e.getMessage());
                }
            }
        });

        final RadioGroup radios = findViewById(R.id.radioGroup);
        radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(group.getContext());
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.change_sets)
                            .setMessage(R.string.really_change_set)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final RadioButton radio = findViewById(checkedId);
                                    changeSets(Integer.parseInt(radio.getTag().toString()));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null);
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface xdialog) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3f51b5"));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3f51b5"));
                        }
                    });
                    dialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in Dialog:", e.getMessage());
                }
            }
        });
    }

    private void changeSets(int sets) {
        Toast.makeText(this, "Sets: " + sets, Toast.LENGTH_SHORT).show();
    }

    private void configInitial() {
        this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
        this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
        this.teamA.setScores(limitSets);
        this.teamB.setScores(limitSets);
        this.lastTeam = "";
        this.currentSet = 0;
        this.isPlaying = false;
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

    private void configScores() {
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
            if (this.currentSet < set + 1) {
                text_a = getString(R.string.empty_score);
                text_b = getString(R.string.empty_score);
            }
            else {
                text_a = String.format(Locale.ROOT, "%02d", this.teamA.getScoresIndex(set));
                text_b = String.format(Locale.ROOT, "%02d", this.teamB.getScoresIndex(set));
            }
            final TextView scoreA = findViewById(id_a);
            scoreA.setText(text_a);
            final TextView scoreB = findViewById(id_b);
            scoreB.setText(text_b);
        }
    }

    private boolean isSetEnd() {
        int diffScores = Math.abs(this.teamA.getScore() - this.teamB.getScore());
        int sets = this.teamA.getSet() + this.teamB.getSet();
        int limitPoints = (sets < this.limitSets - 1) ? 25 : 15;
        return (this.teamA.getScore() >= limitPoints || this.teamB.getScore() >= limitPoints) && diffScores >= 2;
    }

    private boolean isGameEnd() {
        float rel_a, rel_b;
        rel_a = (float) this.teamA.getSet() / this.limitSets;
        rel_b = (float) this.teamB.getSet() / this.limitSets;
        return rel_a > 0.5 || rel_b > 0.5;
    }

    private void addPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (!isGameEnd() && !isSetEnd()) {
            team.setScore(team.getScore() + 1);
            this.lastTeam = current_team;
            configBoard();
            if (!this.isPlaying) {
                this.isPlaying = true;
            }
        }
    }

    private void subtractPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getScore() > 0 && current_team.equals(this.lastTeam)) {
            team.setScore(team.getScore() - 1);
            this.lastTeam = "";
            configBoard();
        }
    }

    private void addSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (!isGameEnd() && isSetEnd() && current_team.equals(this.lastTeam)) {
            this.teamA.setScoresIndex(this.currentSet, this.teamA.getScore());
            this.teamB.setScoresIndex(this.currentSet, this.teamB.getScore());
            team.setSet(team.getSet() + 1);
            if (isGameEnd()) {
                String winner = String.format("%s %s", getString(R.string.winner_message), team.getName());
                Toast toast = Toast.makeText(getApplicationContext(), winner, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
            else {
                this.teamA.setScore(0);
                this.teamB.setScore(0);
            }
            this.lastTeam = current_team;
            this.currentSet += 1;
            configBoard();
            configScores();
        }
    }

    private void subtractSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getSet() > 0 && current_team.equals(this.lastTeam)) {
            this.currentSet -= 1;
            this.teamA.setScore(this.teamA.getScoresIndex(this.currentSet));
            this.teamB.setScore(this.teamB.getScoresIndex(this.currentSet));
            this.teamA.setScoresIndex(this.currentSet, 0);
            this.teamB.setScoresIndex(this.currentSet, 0);
            team.setSet(team.getSet() - 1);
            this.lastTeam = current_team;
            configBoard();
            configScores();
        }
    }
}
