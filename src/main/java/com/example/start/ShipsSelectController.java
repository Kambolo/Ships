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

public class ShipsSelectController implements ShipPlacementListener{
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
    private IntegerProperty disableConfirmButtonProperty = new SimpleIntegerProperty();
    @FXML
    private Button resetButton;
    @FXML
    private Button confirmButton;

    @FXML
    public void initialize(int portNr, Client client, Server server){
        System.out.println("initialize");
        this.portNr = portNr;
        this.client = client;
        this.server = server;
        //client.setCallback(this);

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

    private void drawBoard(){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
    }

    @Override
    public void onShipPlaced(boolean placed, int size){
        //System.out.println("Statek o rozmiarze " + size + " został " + (placed ? "umieszczony" : "nie umieszczony"));
        switch (size){
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
            default:
        }
    }

    public void putShip4(){
        board.setPlacementLocked(false);
        board.selectCells(4);
    }
    public void putShip3(){
        board.setPlacementLocked(false);
        board.selectCells(3);
    }
    public void putShip2(){
        board.setPlacementLocked(false);
        board.selectCells(2);
    }
    public void putShip1(){
        board.setPlacementLocked(false);
        board.selectCells(1);
    }

    public void resetBoard(){
        setNumberOfShips();

        board = new Board();
        board.setShipPlacementListener(this);
        drawBoard();
    }

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

    @FXML
    public void startGame(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        root = loader.load();

        GameController gameController = loader.getController();
        gameController.initialize(board);

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

//    @Override
//    public void updateCells(String cells) {
//        if(cells != null){
//            ArrayList<ArrayList<Cell>> arrayOfCells = board.getBoardArr();
//
//            String[] placement = cells.split(",");
//
//            int row=0;
//            int col =0;
//
//            for(int i=0; i< placement.length; i++){
//                row = placement[i].charAt(0) - '0';
//                col = placement[i].charAt(1) - '0';
//                arrayOfCells.get(row).get(col).setPicked(true);
//            }
//        }
//    }
}
