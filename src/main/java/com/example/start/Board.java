package com.example.start;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int WIDTH = 400;
    private final int HEIGHT = 400;
    private final int CELL_SIZE = 40;
    private final Color CELL_COLOR = Color.BLUE;
    private ArrayList<ArrayList<Cell>> boardArr;
    //if false - direction is down, otherwise right
    private boolean selectDirection = false;
    private boolean placementLocked = false;
    private ShipPlacementListener listener;

    Board(){
        int rows = WIDTH/CELL_SIZE;
        int cols = HEIGHT/CELL_SIZE;

        boardArr = new ArrayList<>();
        for(int y=0; y<rows; y++){
            boardArr.add(new ArrayList<>(cols));
            for(int x=0; x<cols; x++){
                Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, CELL_COLOR);

                //setting position
                rectangle.setX(x * CELL_SIZE);
                rectangle.setY(y * CELL_SIZE);

                //setting borders
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(2);

                //Creating Cell object from this rectangle
                Cell cell = new Cell(rectangle, x, y);

                //add Cell to Array
                boardArr.get(y).add(cell);
            }
        }
    }

    public void selectCells(int numberOfCells){

        boardArr.stream().flatMap(ArrayList::stream).forEach(cell -> {

            cell.getRectangle().setOnScroll(event->{
                resetColor(cell, numberOfCells);
                selectDirection = !selectDirection;

                highlightCells(numberOfCells, cell, Color.GREEN);
            });

            cell.getRectangle().setOnMouseEntered(event->{
                highlightCells(numberOfCells, cell, Color.GREEN);
            });

            cell.getRectangle().setOnMouseExited(event->{
                resetColor(cell, numberOfCells);
                //highlightCells(numberOfCells, cell, Color.BLUE);
            });

            cell.getRectangle().setOnMouseClicked(event->{
                if(isPlacementLocked()) return;
                if (putShip(numberOfCells, cell)) {
                    if (listener != null) {
                        listener.onShipPlaced(true, numberOfCells);
                    }
                    boardArr.stream().flatMap(ArrayList::stream).forEach(finalCell -> {
                        finalCell.getRectangle().setOnMouseEntered(null);
                        finalCell.getRectangle().setOnScroll(null);
                    });
                }
            });
        });

    }

    private void highlightCells(int numberOfCells, Cell cell, Color color) {
        cell.getRectangle().setFill(Color.GREEN);
        Pair<Integer, Integer> position = cell.getPosition();

        int row = position.getValue();
        int col = position.getKey();
        int nextCellRow;
        int nextCellCol;

        for(int i = 0; i < numberOfCells; i ++){
            nextCellRow = row + (selectDirection ? 0 : i);
            nextCellCol = col + (selectDirection ? i : 0);
            if(nextCellRow < boardArr.size() && nextCellCol < boardArr.get(nextCellRow).size()){
                boardArr.get(nextCellRow).get(nextCellCol).getRectangle().setFill(color);
            }
        }
    }

    private boolean putShip(int numberOfCells, Cell cell){
        Pair<Integer, Integer> position = cell.getPosition();

        int row = position.getValue();
        int col = position.getKey();
        int nextCellRow;
        int nextCellCol;

        List<Cell> availableCells = new ArrayList<>();

        for(int i = 0; i < numberOfCells; i ++){
            nextCellRow = row + (selectDirection ? 0 : i);
            nextCellCol = col + (selectDirection ? i : 0);
            if(nextCellRow < boardArr.size() && nextCellCol < boardArr.get(nextCellRow).size()){
                var temp = boardArr.get(nextCellRow).get(nextCellCol);
                if(ifAvailable(temp)){
                    availableCells.add(temp);
                }
            }
        }
        if (availableCells.size() == numberOfCells){
            availableCells.stream().forEach(cellToUpdate -> {
                cellToUpdate.setShip(true);
                cellToUpdate.getRectangle().setFill(Color.YELLOW);
            });
            setPlacementLocked(true);
            return true;
        }
        return false;
    }

    private boolean ifAvailable(Cell cell){
        Pair<Integer, Integer> position = cell.getPosition();

        int row = position.getValue();
        int col = position.getKey();

        if(row < boardArr.size() && col < boardArr.get(row).size()){
            if(col > 0 && row > 0 && col < boardArr.get(row).size() - 1 && row < boardArr.size() - 1){
                if(boardArr.get(row - 1).get(col - 1).isShip() || boardArr.get(row).get(col - 1).isShip() || boardArr.get(row + 1).get(col - 1).isShip()) return false; // checking upper-left, left and lower-left of cell
                else if(boardArr.get(row - 1).get(col + 1).isShip() || boardArr.get(row).get(col + 1).isShip() || boardArr.get(row + 1).get(col + 1).isShip()) return false; //checking upper-right, right and lower-right of cell
                else if(boardArr.get(row - 1).get(col).isShip() || boardArr.get(row + 1).get(col).isShip()) return false; //checking up and down of cell
                return true;
            }

            if(col == 0 && row == 0){
                if(boardArr.get(row).get(col + 1).isShip() || boardArr.get(row + 1).get(col + 1).isShip()) return false; //checking right and lower-right of cell
                else if(boardArr.get(row + 1).get(col).isShip()) return false; //checking down of cell
                return true;
            }

            if(col == 0 && row == boardArr.size() - 1){
                if(boardArr.get(row - 1).get(col + 1).isShip() || boardArr.get(row).get(col + 1).isShip()) return false; //checking upper-right, right of cell
                else if(boardArr.get(row - 1).get(col).isShip()) return false; //checking up of cell
                return true;
            }

            if(col == boardArr.get(row).size() - 1 && row == 0){
                if(boardArr.get(row).get(col - 1).isShip() || boardArr.get(row + 1).get(col - 1).isShip()) return false; // checking left and lower-left of cell
                else if(boardArr.get(row + 1).get(col).isShip()) return false; //checking down of cell
                return true;
            }

            if(col == boardArr.get(row).size() - 1 && row == boardArr.size() - 1){
                if(boardArr.get(row - 1).get(col - 1).isShip() || boardArr.get(row).get(col - 1).isShip()) return false; // checking upper-left, left of cell
                else if(boardArr.get(row - 1).get(col).isShip()) return false; //checking up of cell
                return true;
            }

            if(col == 0){
                if(boardArr.get(row - 1).get(col + 1).isShip() || boardArr.get(row).get(col + 1).isShip() || boardArr.get(row + 1).get(col + 1).isShip()) return false; //checking upper-right, right and lower-right of cell
                else if(boardArr.get(row - 1).get(col).isShip() || boardArr.get(row + 1).get(col).isShip()) return false; //checking up and down of cell
                return true;
            }

            if(col == boardArr.get(row).size() - 1){
                if(boardArr.get(row - 1).get(col - 1).isShip() || boardArr.get(row).get(col - 1).isShip() || boardArr.get(row + 1).get(col - 1).isShip()) return false; // checking upper-left, left and lower-left of cell
                else if(boardArr.get(row - 1).get(col).isShip() || boardArr.get(row + 1).get(col).isShip()) return false; //checking up and down of cell
                return true;
            }

            if (row == 0){
                if(boardArr.get(row + 1).get(col - 1).isShip() || boardArr.get(row + 1).get(col).isShip() || boardArr.get(row + 1).get(col + 1).isShip()) return false; //checking lower-left, down and lower-right of cell
                return true;
            }

            if(row == boardArr.size() - 1){
                if(boardArr.get(row - 1).get(col - 1).isShip() || boardArr.get(row - 1).get(col).isShip() || boardArr.get(row - 1).get(col + 1).isShip()) return false; //checking upper-left, up and upper-right of cell
                return true;
            }
        }
        return true;
    }

    private void resetColor(Cell cell, int numberOfCells){
        Pair<Integer, Integer> position = cell.getPosition();

        int row = position.getValue();
        int col = position.getKey();
        int nextCellRow;
        int nextCellCol;

        for(int i = 0; i < numberOfCells; i++){
            nextCellRow = row + (selectDirection ? 0 : i);
            nextCellCol = col + (selectDirection ? i : 0);
            if(nextCellRow < boardArr.size() && nextCellCol < boardArr.get(nextCellRow).size()){
                Cell nextCell = boardArr.get(nextCellRow).get(nextCellCol);
                if(!nextCell.isShip()) {
                    nextCell.getRectangle().setFill(Color.BLUE);
                } else {
                    nextCell.getRectangle().setFill(Color.YELLOW);
                }
            }
        }
    }

    public ArrayList<ArrayList<Cell>> getBoardArr(){
        return boardArr;
    }
    public Cell getCell(int x, int y){
        return boardArr.get(x).get(y);
    }
    public void setShipPlacementListener(ShipPlacementListener listener) {
        this.listener = listener;
    }
    public boolean isPlacementLocked() {return placementLocked;}
    public void setPlacementLocked(boolean placementLocked) {this.placementLocked = placementLocked;}
}