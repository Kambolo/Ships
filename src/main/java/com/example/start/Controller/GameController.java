package com.example.start.Controller;

import com.example.start.Board.Board;
import com.example.start.Board.Cell;
import com.example.start.Client.Client;
import com.example.start.DB.DatabaseOperations;
import com.example.start.Main;
import com.example.start.Server.Server;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller class for the game view.
 */
public class GameController {
    private Parent root;
    private Stage stage;
    private Scene scene;
    private Board board;
    private Board opponentBoard;
    private Server server;
    private Client client;
    private boolean myTurn;
    private boolean end;
    private int shipsRemaining;
    private int opponentShipsRemaining;
    private int timeSeconds;
    private String clientUsername;
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
    private Button backToMenu;
    @FXML
    private Label timerLabel;
    @FXML
    private Label timeLabel;

    /**
     * Initializes the game view.
     *
     * @param board  The player's board
     * @param client The client instance
     * @param server The server instance
     */
    @FXML
    public void initialize(Board board, Client client, Server server) {
        this.board = board;
        this.server = server;
        this.client = client;
        this.opponentBoard = new Board();
        this.shipsRemaining = 20;
        this.opponentShipsRemaining = 20;
        this.backToMenu.setVisible(false);
        this.backToMenu.setDisable(true);
        this.timerLabel.setText("");
        timeSeconds = 30;
        this.timeLabel.setText("");
        this.end = false;

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

    /**
     * Starts the game by initializing necessary components and determining the player's turn.
     * If a client is connected, it sends the username to the server.
     * If a server is active, it retrieves the client's username.
     * Sets up board interaction and starts the timer countdown.
     */
    private void startGame(){
        System.out.println("Starting game");

        //server gets client username
        if(client != null){
            client.sendMessage("username:"+ Main.getUsername());
        }
        if(server != null){
            clientUsername = server.getUsername(server.getSocket(), server.getBufferedReader());
            System.out.println("client username "+clientUsername);
        }

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

        timerCountDownThread();
    }

    /**
     * Sets up interaction with the board for shooting cells.
     *
     * @param board The board to set up interaction with
     */
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

    /**
     * Resets board interaction by removing mouse click listeners.
     *
     * @param board The board to reset interaction for
     */
    private void resetBoardInteraction(Board board) {
        board.getBoardArr().stream().flatMap(ArrayList::stream).forEach(cell -> {
            cell.getRectangle().setOnMouseClicked(null);
        });
    }

    /**
     * Handles the selection of a cell and sends the selected cell to the opponent.
     *
     * @param selectedCell The selected cell's position
     */
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
            timeSeconds = 30;
            if (task.getValue()) {
                System.out.println("Opponent returned true");
                myTurn = true;
                opponentShipsRemaining--;
                if(opponentShipsRemaining == 0){
                    resetBoardInteraction(opponentBoard);
                    end = true;
                    Platform.runLater(()->{
                        playerTurn.setText("Wygrana!");
                        timerLabel.setText("");
                        this.backToMenu.setVisible(true);
                        this.backToMenu.setDisable(false);
                        if(server != null){
                            DatabaseOperations.increaseScoreInDb("ships", "leaderboard", Main.getUsername());
                            server.closeEverything();
                        }
                        if(client != null){
                            client.closeEverything();
                        }
                    });
                }
                Platform.runLater(() -> {
                    opponentBoard.getCell(selectedCell.getValue(), selectedCell.getKey()).getRectangle().setFill(Color.RED);
                });
            } else {
                System.out.println("Opponent returned false");
                myTurn = false;
                Platform.runLater(() -> {
                    opponentBoard.getCell(selectedCell.getValue(), selectedCell.getKey()).getRectangle().setFill(Color.GRAY);
                });
                Platform.runLater(this::waitForOpponentMove);
                resetBoardInteraction(opponentBoard);
                playerTurn.setText("Ruch przeciwnika");
            }
        });
        new Thread(task).start();
    }

    /**
     * Waits for the opponent's response.
     *
     * @return True if the opponent's response indicates a hit, false otherwise
     */
    private boolean waitForOpponentResponse(){
        System.out.println("Waiting for opponent response");
        if(server != null) return server.getResponse(server.getSocket(), server.getBufferedReader());

        if(client != null) return client.getResponse(client.getSocket(), client.getBufferedReader());

        return false;
    }

    /**
     * Waits for the opponent's move and handles it accordingly.
     */
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
                Pair<Integer, Integer> finalPosition = position;
                timeSeconds = 30;
                Platform.runLater(() -> {
                    var cell = board.getCell(finalPosition.getKey(), finalPosition.getValue());
                    if (cell.isShip()) {
                        shipsRemaining--;
                        cell.getRectangle().setFill(Color.RED);
                        sendFeedback(cell.isShip());
                        if(shipsRemaining == 0){
                            resetBoardInteraction(opponentBoard);
                            end = true;
                            Platform.runLater(()->{
                               timerLabel.setText("");
                                playerTurn.setText("Przegrana!");
                                this.backToMenu.setVisible(true);
                                this.backToMenu.setDisable(false);
                                if(server != null){
                                    DatabaseOperations.increaseScoreInDb("ships", "leaderboard", clientUsername);
                                    server.closeEverything();
                                }
                                if(client != null){
                                    client.closeEverything();
                                }
                            });
                        }
                        waitForOpponentMove();
                    }
                    else{
                        myTurn = true;
                        Platform.runLater(() -> {
                            playerTurn.setText("Twój ruch");
                            setupBoardInteraction(opponentBoard);
                        });
                        sendFeedback(cell.isShip());
                    }

                });

            }
        });

        new Thread(task).start();
    }

    /**
     * Sends feedback to the opponent about the result of the last move.
     *
     * @param hit True if the last move resulted in a hit, false otherwise
     */
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

    /**
     * Confirms the game asynchronously by sending a ready message and awaiting confirmation from the opponent.
     *
     * @param onSuccess The action to perform on successful confirmation
     * @param onFailure The action to perform on failed confirmation
     */
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

    /**
     * Draws the board on the specified pane.
     *
     * @param board The board to draw
     * @param pane  The pane to draw the board on
     */
    private void drawBoard(Board board, Pane pane){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
    }

    /**
     * Redirects the user back to the main menu.
     *
     * @param evt The action event triggering the method call
     * @throws IOException If an I/O error occurs during loading of the main menu
     */
    public void backToMenu(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createOrJoin.fxml"));
        root = loader.load();

        CreateOrJoinController createOrJoinController = loader.getController();
        createOrJoinController.initialize("");

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes and starts the countdown timer.
     */
    private void timerCountDownThread() {
        timerLabel.setText("30");  // Initial text
        timeLabel.setText("Czas");
        System.out.println("start pomiaru");

        // Create a Task to run the countdown
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (timeSeconds > 0 && !end) {
                    timeSeconds--;
                    Platform.runLater(() -> timerLabel.setText(String.valueOf(timeSeconds)));
                    Thread.sleep(1000);
                }
                if (!end) {
                    Platform.runLater(() -> {
                        timerLabel.setText("koniec czasu");
                        if (myTurn) {
                            playerTurn.setText("Przegrana");
                            resetBoardInteraction(opponentBoard);
                            if (server != null) {
                                DatabaseOperations.increaseScoreInDb("ships", "leaderboard", clientUsername);
                                server.closeEverything();
                            }
                            if (client != null) {
                                client.closeEverything();
                            }
                        } else {
                            playerTurn.setText("Wygrana");
                            if (server != null) {
                                DatabaseOperations.increaseScoreInDb("ships", "leaderboard", Main.getUsername());
                                server.closeEverything();
                            }
                            if (client != null) {
                                client.closeEverything();
                            }
                        }
                        backToMenu.setVisible(true);
                        backToMenu.setDisable(false);
                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
