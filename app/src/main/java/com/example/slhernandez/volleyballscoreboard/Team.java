package com.example.slhernandez.volleyballscoreboard;

public class Team {
    private String name;
    private int score;
    private int set;

    public Team(String name, int score, int set) {
        this.name = name;
        this.score = score;
        this.set = set;
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
}
