package com.example.start;

import javafx.fxml.FXML;

public class GameController {
    private Board board;

    @FXML
    public void initialize(Board board) {
        this.board = board;
    }
}
