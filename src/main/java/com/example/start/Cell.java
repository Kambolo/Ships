package com.example.start;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class Cell {
    private Rectangle rectangle;
    private boolean picked;
    private Pair<Integer, Integer> position;
    private boolean ship;

    Cell(Rectangle rectangle, int x, int y) {
        this.setRectangle(rectangle);
        setPosition(new Pair<>(x, y));
        setShip(false);
    }


    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Pair<Integer, Integer> getPosition() {return position;}
    public void setPosition(Pair<Integer, Integer> position) {this.position = position;}
    public boolean isShip() {return ship;}
    public void setShip(boolean ship) {this.ship = ship;}
}