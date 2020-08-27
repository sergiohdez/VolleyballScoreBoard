package com.formul.slhernandez.volleyballscoreboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Team teamA;
    private Team teamB;
    private int limitSets;
    private String lastTeam;
    private int currentSet;
    private boolean isPlaying;
    private boolean reversePoint;
    private boolean reverseSet;
    private SharedPreferences mPrefs;

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
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            newLimitSets = Integer.parseInt(v.getTag().toString());
            try {
                if (isPlaying && this.newLimitSets != limitSets) {
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
                    dialog.show();
                }
                else if (!isPlaying) {
                    changeSets(newLimitSets);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception in Dialog:", Objects.requireNonNull(e.getMessage()));
            }
        }
    };

    private final View.OnClickListener mTeamNameListener = new View.OnClickListener() {
        private String team;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                                    boolean valid = true;
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
                                        isPlaying = true;
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
                dialog.show();
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception in Dialog:", Objects.requireNonNull(e.getMessage()));
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mPrefs = getSharedPreferences("VSB_Pref", 0);

        configInitial(false);

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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    if (isPlaying) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.reset_title)
                                .setMessage(R.string.really_reset)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        configInitial(true);
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
                        dialog.show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.no_reset, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in Dialog:", Objects.requireNonNull(e.getMessage()));
                }
            }
        });

        final RadioButton radio3 = findViewById(R.id.radio_btn_3);
        radio3.setOnClickListener(mRadioButtonListener);
        //radio3.setChecked(true);

        final RadioButton radio5 = findViewById(R.id.radio_btn_5);
        radio5.setOnClickListener(mRadioButtonListener);

        final TextView nameTeamA = findViewById(R.id.name_team_a);
        nameTeamA.setOnClickListener(mTeamNameListener);

        final TextView nameTeamB = findViewById(R.id.name_team_b);
        nameTeamB.setOnClickListener(mTeamNameListener);
    }

    private void changeSets(int sets) {
        this.limitSets = sets;
        this.teamA.setScores(sets);
        this.teamB.setScores(sets);
        this.teamA.setScore(0);
        this.teamB.setScore(0);
        this.teamA.setSet(0);
        this.teamB.setSet(0);
        this.lastTeam = "";
        this.currentSet = 0;
        this.isPlaying = true;
        this.reversePoint = false;
        this.reverseSet = false;
        configBoard(false);
        configTable(sets);
        configScores(sets);
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

    private void configInitial(Boolean forceDefault) {
        if (forceDefault) {
            this.limitSets = 3;
            this.teamA = new Team(getString(R.string.default_name_team_a), 0, 0);
            this.teamB = new Team(getString(R.string.default_name_team_b), 0, 0);
            this.lastTeam = "";
            this.currentSet = 0;
            this.isPlaying = false;
            this.reversePoint = false;
            this.reverseSet = false;
        }
        else {
            Gson gson = new Gson();
            this.limitSets = this.mPrefs.getInt("limit_sets", 3);
            String defaultTeamA = "{\"name\":\"" + getString(R.string.default_name_team_a) + "\",\"score\":0,\"scores\":[0,0,0],\"set\":0}";
            String defaultTeamB = "{\"name\":\"" + getString(R.string.default_name_team_b) + "\",\"score\":0,\"scores\":[0,0,0],\"set\":0}";
            this.teamA = gson.fromJson(this.mPrefs.getString("team_a", defaultTeamA), Team.class);
            this.teamB = gson.fromJson(this.mPrefs.getString("team_b", defaultTeamB), Team.class);
            this.lastTeam = this.mPrefs.getString("last_team", "");
            this.currentSet = this.mPrefs.getInt("current_set", 0);
            this.isPlaying = this.mPrefs.getBoolean("is_playing", false);
            this.reversePoint = this.mPrefs.getBoolean("reverse_point", false);
            this.reverseSet = this.mPrefs.getBoolean("reverse_set", false);
        }

        this.teamA.setScores(this.limitSets);
        this.teamB.setScores(this.limitSets);
        configBoard(this.isPlaying);
        configTable(this.limitSets);
        configScores(this.limitSets);
        configSets(this.limitSets);
        changeTeamName(getString(R.string.default_name_team_a), this.teamA.getName());
        changeTeamName(getString(R.string.default_name_team_b), this.teamB.getName());
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
        int dp, density;
        final int[] id = new int[2];
        id[0] = R.id.row_a;
        id[1] = R.id.row_b;
        String team;
        density = getApplicationContext().getResources().getDisplayMetrics().densityDpi;
        TableRow.LayoutParams params;
        for (int anId : id) {
            row = findViewById(anId);
            team = (anId == R.id.row_a) ? "a" : "b";
            while (row.getChildCount() > 0) {
                row.removeViewAt(0);
            }
            column = new TextView(getApplicationContext());
            column.setId(getResources().getIdentifier("tname_team_" + team, "id", getPackageName()));
            params = new TableRow.LayoutParams();
            params.weight = 1;
            params.height = TableRow.LayoutParams.WRAP_CONTENT;
            params.width = TableRow.LayoutParams.WRAP_CONTENT;
            dp = getResources().getDimensionPixelSize(R.dimen.table_col_margin);
            params.setMargins(dp, dp, dp, dp);
            column.setLayoutParams(params);
            column.setBackgroundColor(Color.WHITE);
            dp = getResources().getDimensionPixelSize(R.dimen.table_col_padding);
            column.setPadding(dp, dp, dp, dp);
            //column.setGravity(Gravity.CENTER_HORIZONTAL);
            if (anId == R.id.row_a) {
                column.setText(this.teamA.getName());
            }
            else {
                column.setText(this.teamB.getName());
            }
            dp = (160 * getResources().getDimensionPixelSize(R.dimen.table_text_size)) / density;
            column.setTextSize(dp);
            column.setTag(getString(R.string.tag_team_name));
            row.addView(column);
            for (int i = 1; i <= sets; i++) {
                column = new TextView(getApplicationContext());
                column.setId(getResources().getIdentifier("set" + i + "_team_" + team, "id", getPackageName()));
                params = new TableRow.LayoutParams();
                params.weight = 1;
                params.height = TableRow.LayoutParams.WRAP_CONTENT;
                params.width = TableRow.LayoutParams.WRAP_CONTENT;
                dp = getResources().getDimensionPixelSize(R.dimen.table_col_margin);
                params.setMargins(dp, dp, dp, dp);
                column.setLayoutParams(params);
                column.setBackgroundColor(Color.WHITE);
                dp = getResources().getDimensionPixelSize(R.dimen.table_col_padding);
                column.setPadding(dp, dp, dp, dp);
                column.setGravity(Gravity.CENTER_HORIZONTAL);
                column.setText(R.string.empty_score);
                dp = (160 * getResources().getDimensionPixelSize(R.dimen.table_text_size)) / density;
                column.setTextSize(dp);
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

    private void configSets(int sets) {
        final RadioGroup radios = findViewById(R.id.radioGroup);
        if (sets == 3) {
            radios.check(R.id.radio_btn_3);
        }
        if (sets == 5) {
            radios.check(R.id.radio_btn_5);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor ed = this.mPrefs.edit();
        ed.putInt("limit_sets", this.limitSets);
        ed.putString("last_team", this.lastTeam);
        ed.putInt("current_set", this.currentSet);
        ed.putBoolean("is_playing", this.isPlaying);
        ed.putBoolean("reverse_point", this.reversePoint);
        ed.putBoolean("reverse_set", this.reverseSet);
        Gson gson = new Gson();
        String jsonTeamA = gson.toJson(this.teamA);
        String jsonTeamB = gson.toJson(this.teamB);
        ed.putString("team_a", jsonTeamA);
        ed.putString("team_b", jsonTeamB);
        ed.apply();
    }
}
