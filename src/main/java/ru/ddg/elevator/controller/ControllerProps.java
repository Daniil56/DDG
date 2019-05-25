package ru.ddg.elevator.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Elevator controller configuration.
 */
@ConfigurationProperties(prefix = "elevator")
@Validated //todo constraints
public class ControllerProps {
    
    private int currentFloor = 1;
    private int floorsCount;
    private int floorHeightCentimeters;
    private int liftSpeedCentimetersPerSecond;
    private int doorOpenCloseTimeMillis;

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getFloorsCount() {
        return floorsCount;
    }

    public int getFloorHeightCentimeters() {
        return floorHeightCentimeters;
    }

    public int getLiftSpeedCentimetersPerSecond() {
        return liftSpeedCentimetersPerSecond;
    }

    public int getDoorOpenCloseTimeMillis() {
        return doorOpenCloseTimeMillis;
    }

    public int getFloorTravelTimeMillis() {
        return floorHeightCentimeters / liftSpeedCentimetersPerSecond * 1000;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setFloorsCount(int floorsCount) {
        this.floorsCount = floorsCount;
    }

    public void setFloorHeightMeters(float floorHeightMeters) {
        this.floorHeightCentimeters = (int)(floorHeightMeters * 100);
    }

    public void setLiftSpeedMetersPerSecond(float liftSpeedMetersPerSecond) {
        this.liftSpeedCentimetersPerSecond = (int)(liftSpeedMetersPerSecond * 100);
    }

    public void setDoorOpenCloseTimeMillis(int doorOpenCloseTimeMillis) {
        this.doorOpenCloseTimeMillis = doorOpenCloseTimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerProps props = (ControllerProps) o;
        return currentFloor == props.currentFloor &&
                floorsCount == props.floorsCount &&
                floorHeightCentimeters == props.floorHeightCentimeters &&
                liftSpeedCentimetersPerSecond == props.liftSpeedCentimetersPerSecond &&
                doorOpenCloseTimeMillis == props.doorOpenCloseTimeMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                currentFloor, floorsCount, floorHeightCentimeters, 
                liftSpeedCentimetersPerSecond, doorOpenCloseTimeMillis
        );
    }

    @Override
    public String toString() {
        return "ControllerProps{" +
                "currentFloor=" + currentFloor +
                ", floorsCount=" + floorsCount +
                ", floorHeightCentimeters=" + floorHeightCentimeters +
                ", ElevatorSpeedCentimetersPerSecond=" + liftSpeedCentimetersPerSecond +
                ", doorOpenCloseTimeMillis=" + doorOpenCloseTimeMillis +
                '}';
    }
}
