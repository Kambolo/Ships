package com.example.start;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Board {
    private final int WIDTH = 400;
    private final int HEIGHT = 400;
    private final int CELL_SIZE = 40;
    private final Color CELL_COLOR = Color.BLUE;
    private ArrayList<ArrayList<Cell>> boardArr;

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
                Cell cell = new Cell(rectangle);

                //add Cell to Array
                boardArr.get(y).add(cell);
            }
        }
    }

    public ArrayList<ArrayList<Cell>> getBoardArr(){
        return boardArr;
    }
}