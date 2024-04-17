package com.example.start;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell {
    private Rectangle rectangle;
    private boolean picked;

    Cell(Rectangle rectangle) {
        this.setRectangle(rectangle);

        //setting onMouseClicked to interact with rectangle
        rectangle.setOnMouseClicked(event->{
            if(event.getButton() == MouseButton.PRIMARY){
                rectangle.setFill(Color.RED);
                picked = true;
            }
        });

    }


    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
        if(this.picked){
            rectangle.setFill(Color.RED);
        }
    }
}