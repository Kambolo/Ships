package com.example.start;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardEntry {
    private final SimpleStringProperty username;
    private final SimpleIntegerProperty wins;

    public LeaderboardEntry(String username, int wins) {
        this.username = new SimpleStringProperty(username);
        this.wins = new SimpleIntegerProperty(wins);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public int getWins() {
        return wins.get();
    }

    public void setWins(int wins) {
        this.wins.set(wins);
    }

    public SimpleIntegerProperty winsProperty() {
        return wins;
    }
}
