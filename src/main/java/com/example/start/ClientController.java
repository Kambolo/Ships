package com.example.start;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController implements UpdateCellsCallback{
    @FXML
    private Pane pane;
    private Board board;
    private Client client;
    private int portNr;

    @FXML
    public void initialize(int portNr){
        try{
            this.portNr = portNr;
            client = new Client(new Socket("localhost", this.portNr), this);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while creating client");
        }
        board = new Board();
        drawBoard();
    }

    public void drawBoard(){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
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
