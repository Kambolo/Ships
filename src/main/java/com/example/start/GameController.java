package com.example.start;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GameController {
    private Board board;
    private Board opponentBoard;
    private Server server;
    private Client client;
    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Label infoLabel;
    @FXML
    private VBox metricY1;
    @FXML
    private VBox metricY2;
    @FXML
    private HBox metricX1;
    @FXML
    private HBox metricX2;

    @FXML
    public void initialize(Board board, Client client, Server server) {
        this.board = board;
        this.server = server;
        this.client = client;
        this.opponentBoard = new Board();

        metricX1.setVisible(false);
        metricX2.setVisible(false);
        metricY1.setVisible(false);
        metricY2.setVisible(false);

        infoLabel.setText("Oczekiwanie na\nprzeciwnika");

        confirmGameAsync(() -> {
            infoLabel.setText("");
            drawBoard(board, pane1);
            drawBoard(opponentBoard, pane2);

            metricX1.setVisible(true);
            metricX2.setVisible(true);
            metricY1.setVisible(true);
            metricY2.setVisible(true);
        }, () -> {
            System.out.println("Something went wrong");
            infoLabel.setText("Something went wrong");
        });
    }

    private void confirmGameAsync(Runnable onSuccess, Runnable onFailure) {
        System.out.println("Confirm game");
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (client != null) {
                    client.sendMessage("ready");
                    return client.confirmGame(client.getSocket(), client.getBufferedReader());
                } else if (server != null) {
                    server.sendMessage("ready");
                    return server.confirmGame(server.getSocket(), server.getBufferedReader());
                }
                return false;
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue()) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            onFailure.run();
        });

        new Thread(task).start();
    }

    private void drawBoard(Board board, Pane pane){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
    }
}
