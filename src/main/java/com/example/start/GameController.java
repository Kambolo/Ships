package com.example.start;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;

public class GameController {
    private Board board;
    private Server server;
    private Client client;
    private Pane pane;

    @FXML
    public void initialize(Board board, Client client, Server server, Pane pane) {
        this.board = board;
        this.server = server;
        this.client = client;
        this.pane = pane;

        if(confirmGame()){
            System.out.println("Congratulations! You are ready to play!");
            drawBoard();
        }
    }

    private boolean confirmGame(){
        try {
            if (client != null) {
                client.sendMessage("ready");
                return client.confirmGame(client.getSocket(), client.getBufferedReader());
            } else if (server != null) {
                server.sendMessage("ready");
                return server.confirmGame(server.getSocket(), server.getBufferedReader());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void drawBoard(){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
    }
}
