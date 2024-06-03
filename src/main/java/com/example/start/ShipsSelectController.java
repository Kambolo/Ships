package com.example.start;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller class for the ship selection screen.
 */
public class ShipsSelectController implements ShipPlacementListener {

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    private Pane pane;
    @FXML
    private Button add4;
    @FXML
    private Button add3;
    @FXML
    private Button add2;
    @FXML
    private Button add1;
    @FXML
    private Label numberOf4Label;
    @FXML
    private Label numberOf3Label;
    @FXML
    private Label numberOf2Label;
    @FXML
    private Label numberOf1Label;

    private Board board;
    private Client client;
    private Server server;
    private int portNr;
    private IntegerProperty numberOf4 = new SimpleIntegerProperty();
    private IntegerProperty numberOf3 = new SimpleIntegerProperty();
    private IntegerProperty numberOf2 = new SimpleIntegerProperty();
    private IntegerProperty numberOf1 = new SimpleIntegerProperty();
    @FXML
    private Button resetButton;
    @FXML
    private Button confirmButton;

    /**
     * Initializes the ShipsSelectController.
     *
     * @param portNr Port number for the server
     * @param client Client instance
     * @param server Server instance
     */
    @FXML
    public void initialize(int portNr, Client client, Server server) {
        System.out.println("initialize");
        this.portNr = portNr;
        this.client = client;
        this.server = server;

        setNumberOfShips();

        add4.disableProperty().bind(numberOf4.lessThanOrEqualTo(0));
        add3.disableProperty().bind(numberOf3.lessThanOrEqualTo(0));
        add2.disableProperty().bind(numberOf2.lessThanOrEqualTo(0));
        add1.disableProperty().bind(numberOf1.lessThanOrEqualTo(0));
        confirmButton.disableProperty().bind(numberOf1.greaterThan(0).or(numberOf2.greaterThan(0))
                .or(numberOf3.greaterThan(0)).or(numberOf4.greaterThan(0)));

        System.out.println("Drawing board for " + (client == null ? "server" : "client"));
        board = new Board();
        board.setShipPlacementListener(this);
        drawBoard();
    }

    /**
     * Draws the game board on the screen.
     */
    private void drawBoard() {
        for (ArrayList<Cell> row : board.getBoardArr()) {
            for (Cell cell : row) {
                pane.getChildren().add(cell.getRectangle());
            }
        }
    }

    /**
     * Called when a ship is placed on the board.
     *
     * @param placed true if the ship is successfully placed, false otherwise
     * @param size   the size of the ship
     */
    @Override
    public void onShipPlaced(boolean placed, int size) {
        switch (size) {
            case 1: {
                numberOf1.set(numberOf1.get() - 1);
                numberOf1Label.setText("Dostepne statki: " + numberOf1.get());
                break;
            }
            case 2: {
                numberOf2.set(numberOf2.get() - 1);
                numberOf2Label.setText("Dostepne statki: " + numberOf2.get());
                break;
            }
            case 3: {
                numberOf3.set(numberOf3.get() - 1);
                numberOf3Label.setText("Dostepne statki: " + numberOf3.get());
                break;
            }
            case 4: {
                numberOf4.set(numberOf4.get() - 1);
                numberOf4Label.setText("Dostepne statki: " + numberOf4.get());
                break;
            }
        }
    }

    /**
     * Method to put a ship of size 4 on the board.
     */
    public void putShip4() {
        board.setPlacementLocked(false);
        board.selectCells(4);
    }

    /**
     * Method to put a ship of size 3 on the board.
     */
    public void putShip3() {
        board.setPlacementLocked(false);
        board.selectCells(3);
    }

    /**
     * Method to put a ship of size 2 on the board.
     */
    public void putShip2() {
        board.setPlacementLocked(false);
        board.selectCells(2);
    }

    /**
     * Method to put a ship of size 1 on the board.
     */
    public void putShip1() {
        board.setPlacementLocked(false);
        board.selectCells(1);
    }

    /**
     * Resets the board by re-initializing ship counts and drawing a new board.
     */
    public void resetBoard() {
        setNumberOfShips();
        board = new Board();
        board.setShipPlacementListener(this);
        drawBoard();
    }

    /**
     * Sets the initial ship counts for each size.
     */
    private void setNumberOfShips() {
        numberOf4.set(1);
        numberOf3.set(2);
        numberOf2.set(3);
        numberOf1.set(4);

        numberOf4Label.setText("Dostepne statki: " + numberOf4.get());
        numberOf3Label.setText("Dostepne statki: " + numberOf3.get());
        numberOf2Label.setText("Dostepne statki: " + numberOf2.get());
        numberOf1Label.setText("Dostepne statki: " + numberOf1.get());
    }

    /**
     * Starts the game when the confirm button is clicked.
     *
     * @param evt Action event
     * @throws IOException Input/output exception
     */
    @FXML
    public void startGame(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        root = loader.load();

        GameController gameController = loader.getController();
        gameController.initialize(board, client, server);

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}