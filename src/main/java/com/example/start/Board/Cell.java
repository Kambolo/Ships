package com.example.start.Board;

import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

/**
 * Represents a cell on the game board.
 */
public class Cell {
    private Rectangle rectangle;
    private Pair<Integer, Integer> position;
    private boolean ship;
    private boolean shooted;

    /**
     * Constructs a Cell object.
     *
     * @param rectangle the graphical representation of the cell
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     */
    Cell(Rectangle rectangle, int x, int y) {
        this.setRectangle(rectangle);
        setPosition(new Pair<>(x, y));
        setShip(false);
        this.setShooted(false);
    }

    /**
     * Gets the rectangle representing the cell.
     *
     * @return the rectangle representing the cell
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Sets the rectangle representing the cell.
     *
     * @param rectangle the new rectangle representing the cell
     */
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Gets the position of the cell.
     *
     * @return the position of the cell as a pair of coordinates (x, y)
     */
    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    /**
     * Sets the position of the cell.
     *
     * @param position the new position of the cell as a pair of coordinates (x, y)
     */
    public void setPosition(Pair<Integer, Integer> position) {
        this.position = position;
    }

    /**
     * Checks if the cell contains a ship.
     *
     * @return true if the cell contains a ship, false otherwise
     */
    public boolean isShip() {
        return ship;
    }

    /**
     * Sets whether the cell contains a ship.
     *
     * @param ship true if the cell contains a ship, false otherwise
     */
    public void setShip(boolean ship) {
        this.ship = ship;
    }

    /**
     * Checks if the cell has been shot.
     *
     * @return true if the cell has been shot, false otherwise
     */
    public boolean isShooted() {
        return shooted;
    }

    /**
     * Sets whether the cell has been shot.
     *
     * @param shooted true if the cell has been shot, false otherwise
     */
    public void setShooted(boolean shooted) {
        this.shooted = shooted;
    }
}
