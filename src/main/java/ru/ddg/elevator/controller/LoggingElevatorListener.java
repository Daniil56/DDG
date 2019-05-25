package ru.ddg.elevator.controller;

/**
 *
 */
public class LoggingElevatorListener implements ElevatorListener {
    
    @Override
    public void atFloor(int floorNumber) {
        log("Elevator at floor: " + floorNumber);
    }

    @Override
    public void doorOpening() {
        log("Door is opening...");
    }

    @Override
    public void doorClosed() {
        log("Door is closed.");
    }
    
    private static void log(String message) {
        System.out.println(message);
    }
}
