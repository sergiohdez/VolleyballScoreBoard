package com.example.slhernandez.volleyballscoreboard;

import java.util.ArrayList;

public class Team {
    private String name;
    private int score;
    private int set;
    private ArrayList<Integer> scores;

    public Team(String name, int score, int set) {
        this.name = name;
        this.score = score;
        this.set = set;
        this.scores = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public String getScoreText() {
        if (this.getScore() < 10) {
            return "0" + this.getScore();
        }
        return "" + this.getScore();
    }

    public String getSetText() {
        return "" + this.getSet();
    }

    public void setScores(Integer sets) {
        for(int index = 0; index < sets; index++) {
            this.scores.add(0);
        }
    }

    public Integer getScoresIndex(Integer index) {
        return scores.get(index);
    }

    public void setScoresIndex(Integer index, Integer score) {
        this.scores.set(index, score);
    }
}
