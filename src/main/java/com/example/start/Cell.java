package com.example.start;

import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class Cell {
    private Rectangle rectangle;
    private Pair<Integer, Integer> position;
    private boolean ship;
    private boolean shooted;

    Cell(Rectangle rectangle, int x, int y) {
        this.setRectangle(rectangle);
        setPosition(new Pair<>(x, y));
        setShip(false);
        this.setShooted(false);
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

    public boolean isShooted() {
        return shooted;
    }

    public void setShooted(boolean shooted) {
        this.shooted = shooted;
    }
}