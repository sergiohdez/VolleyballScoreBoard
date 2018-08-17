package com.formul.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableRow;
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
    private boolean reversePoint;
    private boolean reverseSet;

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

    private final View.OnClickListener mRadioButtonListener = new View.OnClickListener() {
        private int newLimitSets;
        @Override
        public void onClick(View v) {
            newLimitSets = Integer.parseInt(v.getTag().toString());
            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.change_sets)
                        .setMessage(R.string.really_change_set)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeSets(newLimitSets);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelChangeSets(newLimitSets);
                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface xdialog) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));
                    }
                });
                if (isPlaying && this.newLimitSets != limitSets) {
                    dialog.show();
                }
                else if (!isPlaying) {
                    changeSets(newLimitSets);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception in Dialog:", e.getMessage());
            }
        }
    };

    private final View.OnClickListener mTeamNameListener = new View.OnClickListener() {
        private String team;
        @Override
        public void onClick(View v) {
            team = v.getTag().toString();
            try {
                final TextView name = findViewById(v.getId());
                final LinearLayout layout = new LinearLayout(v.getContext());
                final EditText newName = new EditText(v.getContext());
                newName.setId(getResources().getIdentifier("new_team_name", "id", getPackageName()));
                newName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                newName.setText(name.getText());
                newName.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
                newName.setSelection(newName.getText().length());
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(25, 50, 25, 50);
                newName.setLayoutParams(params);
                layout.addView(newName);
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.change_team_name)
                        //.setMessage(R.string.really_change_set)
                        .setView(layout)
                        .setPositiveButton(android.R.string.yes, null)
                        .setNegativeButton(android.R.string.no, null);
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface xdialog) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));

                        Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final EditText editText = dialog.findViewById(R.id.new_team_name);
                                if (editText != null) {
                                    String newName = editText.getText().toString();
                                    final int otherIDName = (name.getId() == R.id.name_team_a) ? R.id.name_team_b : R.id.name_team_a;
                                    final TextView otherName = findViewById(otherIDName);
                                    Boolean valid = true;
                                    int message = 0;
                                    if (newName.isEmpty()) {
                                        valid = false;
                                        message = R.string.empty_name;
                                    }
                                    else if (newName.equals(otherName.getText().toString())) {
                                        valid = false;
                                        message = R.string.equal_name;
                                    }

                                    if (valid) {
                                        changeTeamName(team, newName);
                                        dialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                        editText.setText(name.getText());
                                        editText.setSelection(name.getText().length());
                                        dialog.show();
                                    }
                                }
                            }
                        });
                    }
                });
                if (!isPlaying) {
                    dialog.show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception in Dialog:", e.getMessage());
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.limitSets = 3;
        configInitial(this.limitSets);

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
                                    configInitial(limitSets);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null);
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface xdialog) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(getString(R.string.color_btn_dialog)));
                        }
                    });
                    if (isPlaying) {
                        dialog.show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.no_reset, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in Dialog:", e.getMessage());
                }
            }
        });

        final RadioButton radio3 = findViewById(R.id.radio_btn_3);
        radio3.setOnClickListener(mRadioButtonListener);
        radio3.setChecked(true);

        final RadioButton radio5 = findViewById(R.id.radio_btn_5);
        radio5.setOnClickListener(mRadioButtonListener);

        final TextView nameTeamA = findViewById(R.id.name_team_a);
        nameTeamA.setOnClickListener(mTeamNameListener);

        final TextView nameTeamB = findViewById(R.id.name_team_b);
        nameTeamB.setOnClickListener(mTeamNameListener);
    }

    private void changeSets(int sets) {
        this.limitSets = sets;
        configInitial(sets);
        Toast.makeText(this, "Sets: " + sets, Toast.LENGTH_SHORT).show();
    }

    private void cancelChangeSets(int sets) {
        final RadioButton radioPrev, radio;
        final int radioIDPrev, radioID;
        radioIDPrev = (sets == 3) ? R.id.radio_btn_5 : R.id.radio_btn_3;
        radioID = (sets == 3) ? R.id.radio_btn_3 : R.id.radio_btn_5;
        radioPrev = findViewById(radioIDPrev);
        radioPrev.setChecked(true);
        radio = findViewById(radioID);
        radio.setChecked(false);
    }

    private void changeTeamName(String current_team, String newName) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        team.setName(newName);
        final int idName = (current_team.equals(getString(R.string.default_name_team_a))) ? R.id.name_team_a : R.id.name_team_b;
        final TextView nameTeam = findViewById(idName);
        nameTeam.setText(newName);
        final int idTName = (current_team.equals(getString(R.string.default_name_team_a))) ? R.id.tname_team_a : R.id.tname_team_b;
        final TextView nameTTeam = findViewById(idTName);
        nameTTeam.setText(newName);
    }

    private void configInitial(int sets) {
        this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
        this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
        this.teamA.setScores(sets);
        this.teamB.setScores(sets);
        this.lastTeam = "";
        this.currentSet = 0;
        this.isPlaying = false;
        this.reversePoint = false;
        this.reverseSet = false;
        configBoard(false);
        configTable(sets);
        configScores(sets);
    }

    private void configBoard(Boolean underline) {
        SpannableString content;
        final TextView scoreA = findViewById(R.id.score_team_a);
        final TextView scoreB = findViewById(R.id.score_team_b);
        final TextView setA = findViewById(R.id.set_team_a);
        final TextView setB = findViewById(R.id.set_team_b);
        if (underline && !this.lastTeam.isEmpty()) {
            if (this.lastTeam.equals(getString(R.string.default_name_team_a))) {
                content = new SpannableString(this.teamA.getScoreText());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                scoreA.setText(content);
                scoreB.setText(this.teamB.getScoreText());
            }
            if (this.lastTeam.equals(getString(R.string.default_name_team_b))) {
                content = new SpannableString(this.teamB.getScoreText());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                scoreA.setText(this.teamA.getScoreText());
                scoreB.setText(content);
            }
        }
        else {
            scoreA.setText(this.teamA.getScoreText());
            scoreB.setText(this.teamB.getScoreText());
        }
        setA.setText(this.teamA.getSetText());
        setB.setText(this.teamB.getSetText());
    }

    private void configTable(int sets) {
        TableRow row;
        TextView column;
        int dp, child, pos;
        final int[] id = new int[2];
        id[0] = R.id.row_a;
        id[1] = R.id.row_b;
        String team;
        for (int anId : id) {
            row = findViewById(anId);
            team = (anId == R.id.row_a) ? "a" : "b";
            child = row.getChildCount();
            for (int i = 0; i < child; i++) {
                pos = row.getChildCount() - 1;
                if (!row.getChildAt(pos).getTag().toString().equals(getString(R.string.tag_team_name))) {
                    row.removeViewAt(pos);
                }
            }
            for (int i = 1; i <= sets; i++) {
                column = new TextView(getApplicationContext());
                column.setId(getResources().getIdentifier("set" + i + "_team_" + team, "id", getPackageName()));
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.weight = 1;
                params.height = TableRow.LayoutParams.WRAP_CONTENT;
                params.width = TableRow.LayoutParams.WRAP_CONTENT;
                dp = Math.round(1 * (getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160f));
                params.setMargins(dp, dp, dp, dp);
                column.setLayoutParams(params);
                column.setBackgroundColor(Color.WHITE);
                dp = Math.round(2 * (getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160f));
                column.setPadding(dp, dp, dp, dp);
                column.setGravity(Gravity.CENTER_HORIZONTAL);
                column.setText(R.string.empty_score);
                column.setTextSize(20);
                column.setTag("");
                row.addView(column);
            }
        }
    }

    private void configScores(int sets) {
        int id_a, id_b;
        String text_a, text_b;
        for (int set = 0; set < sets; set++) {
            id_a = getResources().getIdentifier("set" + (set + 1) + "_team_a", "id", getPackageName());
            id_b = getResources().getIdentifier("set" + (set + 1) + "_team_b", "id", getPackageName());
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
            configBoard(true);
            if (!this.isPlaying) {
                this.isPlaying = true;
            }
            this.reversePoint = false;
        }
        if (isSetEnd()) {
            addSet(current_team);
        }
    }

    private void subtractPoint(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getScore() > 0 && current_team.equals(this.lastTeam) && !reversePoint) {
            team.setScore(team.getScore() - 1);
            this.lastTeam = "";
            this.reversePoint = true;
            configBoard(false);
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
            //else {
                this.teamA.setScore(0);
                this.teamB.setScore(0);
            //}
            this.lastTeam = current_team;
            this.currentSet += 1;
            this.reverseSet = false;
            configBoard(false);
            configScores(this.limitSets);
        }
    }

    private void subtractSet(String current_team) {
        Team team = (current_team.equals(getString(R.string.default_name_team_a))) ? this.teamA : this.teamB;
        if (team.getSet() > 0 && current_team.equals(this.lastTeam) && !reverseSet) {
            this.currentSet -= 1;
            this.teamA.setScore(this.teamA.getScoresIndex(this.currentSet));
            this.teamB.setScore(this.teamB.getScoresIndex(this.currentSet));
            this.teamA.setScoresIndex(this.currentSet, 0);
            this.teamB.setScoresIndex(this.currentSet, 0);
            team.setSet(team.getSet() - 1);
            this.lastTeam = current_team;
            this.reverseSet = true;
            configBoard(true);
            configScores(this.limitSets);
        }
    }
}
