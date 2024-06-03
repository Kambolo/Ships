package com.example.start;

/**
 * Interface for updating a label from a non-JavaFX class.
 */
public interface LabelUpdateCallback {
    /**
     * Updates the label with new text.
     *
     * @param newText the new text to be displayed in the label
     */
    void updateLabel(String newText);
}
