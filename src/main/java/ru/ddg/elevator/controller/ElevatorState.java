package ru.ddg.elevator.controller;

/**
 * Elevator lifecycle state.
 */
public enum ElevatorState {
    /**
     * Elevator is waiting when a button is pressed.
     */
    IDLE,
    /**
     * Selecting which direction to move - up or down.
     */
    SELECT_DIRECTION,
    /**
     * Elevator is moving up.
     */
    MOVE_UP,
    /**
     * Elevator is moving down.
     */
    MOVE_DOWN,
}
