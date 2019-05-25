package ru.ddg.elevator.controller;

/**
 * Elevator controls exposed to a user.
 */
public interface ElevatorControls {

    /**
     * Handles of pressing button which is inside the elevator.
     * @param floorNumber which button is pressed
     * @throws IllegalArgumentException invalid floor number
     */
    void insideButtonPressed(int floorNumber) throws IllegalArgumentException;

    /**
     * Handles of pressing button which is outside the elevator.
     * @param floorNumber which button is pressed
     * @throws IllegalArgumentException invalid floor number
     */
    void outsideButtonPressed(int floorNumber) throws IllegalArgumentException;

    /**
     * Number of floors elevator can travel to.
     * @return total number of floors
     */
    int getFloorsCount();
    
}
