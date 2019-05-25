package ru.ddg.elevator.controller;

/**
 * Elevator state change listener.
 */
public interface ElevatorListener {

    /**
     * Called when elevator is at the certain floor.
     * @param floorNumber which floor elevator is at
     */
    void atFloor(int floorNumber);

    /**
     * Called when door starts to open.
     */
    void doorOpening();

    /**
     * Called when door is closed.
     */
    void doorClosed();

    /**
     * Null-object.
     */
    ElevatorListener NOOP = new ElevatorListener() {
        @Override
        public void atFloor(int floorNumber) {
        }

        @Override
        public void doorOpening() {
        }

        @Override
        public void doorClosed() {
        }
    };
}
