package com.example.start;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an entry in the leaderboard.
 */
public class LeaderboardEntry {
    private final SimpleStringProperty username;
    private final SimpleIntegerProperty wins;

    /**
     * Initializes a leaderboard entry with the given username and number of wins.
     *
     * @param username the username of the player
     * @param wins     the number of wins of the player
     */
    public LeaderboardEntry(String username, int wins) {
        this.username = new SimpleStringProperty(username);
        this.wins = new SimpleIntegerProperty(wins);
    }

    /**
     * Gets the username of the player.
     *
     * @return the username of the player
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Sets the username of the player.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username.set(username);
    }

    /**
     * Gets the property representing the username.
     *
     * @return the property representing the username
     */
    public SimpleStringProperty usernameProperty() {
        return username;
    }

    /**
     * Gets the number of wins of the player.
     *
     * @return the number of wins of the player
     */
    public int getWins() {
        return wins.get();
    }

    /**
     * Sets the number of wins of the player.
     *
     * @param wins the number of wins to set
     */
    public void setWins(int wins) {
        this.wins.set(wins);
    }

    /**
     * Gets the property representing the number of wins.
     *
     * @return the property representing the number of wins
     */
    public SimpleIntegerProperty winsProperty() {
        return wins;
    }
}
