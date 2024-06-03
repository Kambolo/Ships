package com.example.start;

/**
 * Interface for listening to ship placement events.
 */
public interface ShipPlacementListener {

    /**
     * Called when a ship is placed.
     *
     * @param placed true if the ship is successfully placed, false otherwise
     * @param size   the size of the ship
     */
    void onShipPlaced(boolean placed, int size);
}
