package com.example.start;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController implements UpdateCellsCallback, ShipPlacementListener{
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
    private int portNr;
    private IntegerProperty numberOf4 = new SimpleIntegerProperty();
    private IntegerProperty numberOf3 = new SimpleIntegerProperty();
    private IntegerProperty numberOf2 = new SimpleIntegerProperty();
    private IntegerProperty numberOf1 = new SimpleIntegerProperty();
    @FXML
    private Button resetButton;
    @FXML
    private Button confirmButton;

    @FXML
    public void initialize(int portNr){
        try{
            this.portNr = portNr;
            client = new Client(new Socket("localhost", this.portNr), this);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while creating client");
        }
        setNumberOfShips();

        add4.disableProperty().bind(numberOf4.lessThanOrEqualTo(0));
        add3.disableProperty().bind(numberOf3.lessThanOrEqualTo(0));
        add2.disableProperty().bind(numberOf2.lessThanOrEqualTo(0));
        add1.disableProperty().bind(numberOf1.lessThanOrEqualTo(0));

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

    @Override
    public void updateCells(String cells) {
        if(cells != null){
            ArrayList<ArrayList<Cell>> arrayOfCells = board.getBoardArr();

            String[] placement = cells.split(",");

            int row=0;
            int col =0;

            for(int i=0; i< placement.length; i++){
                row = placement[i].charAt(0) - '0';
                col = placement[i].charAt(1) - '0';
                arrayOfCells.get(row).get(col).setPicked(true);
            }
        }
    }
}
