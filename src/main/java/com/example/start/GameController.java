package com.example.start;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GameController {
    private Board board;
    private Board opponentBoard;
    private Server server;
    private Client client;
    private boolean myTurn;
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
    private Label playerTurn;

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

        playerTurn.setText("");
        infoLabel.setText("Oczekiwanie na\nprzeciwnika");

        confirmGameAsync(() -> {
            infoLabel.setText("");
            drawBoard(board, pane1);
            drawBoard(opponentBoard, pane2);

            metricX1.setVisible(true);
            metricX2.setVisible(true);
            metricY1.setVisible(true);
            metricY2.setVisible(true);

            startGame();
        }, () -> {
            System.out.println("Something went wrong");
            infoLabel.setText("Something went wrong");
        });
    }

    private void startGame(){
        System.out.println("Starting game");

        board.getBoardArr().stream().flatMap(ArrayList::stream).forEach(cell -> {
            cell.getRectangle().setOnMouseEntered(null);
            cell.getRectangle().setOnMouseExited(null);
        });

        //Server starts game
        myTurn = (server != null);

        if (myTurn) {
            playerTurn.setText("Twój\nruch");
            setupBoardInteraction(opponentBoard);
        } else {
            playerTurn.setText("Ruch\nprzeciwnika");
            waitForOpponentMove();
        }
    }

    private void setupBoardInteraction(Board board) {
        board.getBoardArr().stream().flatMap(ArrayList::stream).forEach(cell -> {
            cell.getRectangle().setOnMouseClicked(evt -> {
                if(!cell.isShooted()) {
                    var selectedCell = cell.getPosition();
                    handleCellSelection(selectedCell);
                    cell.setShooted(true);
                }
            });
        });
    }

    private void resetBoardInteraction(Board board) {
        board.getBoardArr().stream().flatMap(ArrayList::stream).forEach(cell -> {
            cell.getRectangle().setOnMouseClicked(null);
        });
    }

    private void handleCellSelection(Pair<Integer, Integer> selectedCell) {
        System.out.println("Wybrana komórka: " + selectedCell);

        if (client != null) {
            client.sendMessage("selected:" + selectedCell.getKey() + ":" + selectedCell.getValue());
        } else if (server != null) {
            server.sendMessage("selected:" + selectedCell.getKey() + ":" + selectedCell.getValue());
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return waitForOpponentResponse();
            }
        };

        task.setOnSucceeded(event -> {
            System.out.println("Opponent returns information");
            if (task.getValue()) {
                System.out.println("Opponent returned true");
                Platform.runLater(() -> {
                    opponentBoard.getCell(selectedCell.getValue(), selectedCell.getKey()).getRectangle().setFill(Color.RED);
                });
            } else {
                System.out.println("Opponent returned false");
                Platform.runLater(() -> {
                    opponentBoard.getCell(selectedCell.getValue(), selectedCell.getKey()).getRectangle().setFill(Color.GRAY);
                });
            }
            myTurn = false;
            resetBoardInteraction(opponentBoard);
            playerTurn.setText("Ruch przeciwnika");
            Platform.runLater(this::waitForOpponentMove);
        });

        new Thread(task).start();
    }

    private boolean waitForOpponentResponse(){
        System.out.println("Waiting for opponent response");
        if(server != null) return server.getResponse(server.getSocket(), server.getBufferedReader());

        if(client != null) return client.getResponse(client.getSocket(), client.getBufferedReader());

        return false;
    }

    private void waitForOpponentMove() {
        Task<Pair<Integer, Integer>> task = new Task<>() {
            @Override
            protected Pair<Integer, Integer> call() {
                if (server != null) {
                    return server.receiveMessage(server.getSocket(), server.getBufferedReader());
                } else if (client != null) {
                    return client.receiveMessage(client.getSocket(), client.getBufferedReader());
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Pair<Integer, Integer> position = task.getValue();
            System.out.println("Received opponent shoot " + position);
            if (position != null) {
                Platform.runLater(() -> {
                    var cell = board.getCell(position.getKey(), position.getValue());
                    if (cell.isShip()) {
                        cell.getRectangle().setFill(Color.RED);
                    }
                    sendFeedback(cell.isShip());
                });
                myTurn = true;
                Platform.runLater(() -> {
                    playerTurn.setText("Twój ruch");
                    setupBoardInteraction(opponentBoard);
                });
            }
        });

        new Thread(task).start();
    }

    private void sendFeedback(Boolean hit){
        System.out.println("Sending feedback: " + hit);
        if(server != null){
            if(hit) server.sendMessage("hit");
            else server.sendMessage("miss");
        }

        if(client != null){
            if(hit) client.sendMessage("hit");
            else client.sendMessage("miss");
        }
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
