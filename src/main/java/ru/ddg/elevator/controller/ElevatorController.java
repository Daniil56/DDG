package ru.ddg.elevator.controller;

import ru.ddg.elevator.model.Elevator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static ru.ddg.elevator.controller.ElevatorState.IDLE;
import static ru.ddg.elevator.controller.ElevatorState.MOVE_DOWN;
import static ru.ddg.elevator.controller.ElevatorState.MOVE_UP;
import static ru.ddg.elevator.controller.ElevatorState.SELECT_DIRECTION;

/**
 * Класс реализующий логику движения лифта.
 *
 * Сначала лифт выполняет внутренние запросы:
 * - первая нажатая кнопка определяет направление
 * - при движении вверх или вниз лифт останавливается на этаже, если номер этажа нажат внутри или снаружи
 * - лифт не изменяет направление если внутренние запросы в выбранном направлении еще обрабатываются
 * Лифт начинает обработку внешних запросов только тогда, когда все внутренние запросы обработаны.
 */
public class ElevatorController implements ElevatorControls, Runnable {

    private final Elevator elevator;
    private final BlockingQueue<ButtonPressed> requests = new LinkedBlockingQueue<>(); // unbounded blocking queue
    private final Thread controllerThread;
    private final int floorsCount;
    private final int doorOpenCloseTimeMillis;
    private final int floorTravelTimeMillis;

    private Map<Integer, ButtonPressed> insidePressed = new HashMap<>();
    private Map<Integer, ButtonPressed> outsidePressed = new HashMap<>();
    private ElevatorState state = IDLE;
    private int currentFloor;

    private ElevatorListener elevatorListener = ElevatorListener.NOOP;

    public ElevatorController(Elevator elevator, ControllerProps props) {
        System.out.println(props);
        this.currentFloor = props.getCurrentFloor();
        this.elevator = elevator;
        this.floorsCount = props.getFloorsCount();
        this.doorOpenCloseTimeMillis = props.getDoorOpenCloseTimeMillis();
        this.floorTravelTimeMillis = props.getFloorTravelTimeMillis();

        controllerThread = new Thread(this, "Elevator-controller-thread");
        controllerThread.setDaemon(false);
    }

    public void setElevatorListener(ElevatorListener elevatorListener) {
        this.elevatorListener = (elevatorListener == null ? ElevatorListener.NOOP : elevatorListener);
    }


    @Override
    public void insideButtonPressed(int floorNumber) throws IllegalArgumentException {
        assertValid(floorNumber);
        requests.add(ButtonPressed.inside(floorNumber));
    }

    @Override
    public void outsideButtonPressed(int floorNumber) throws IllegalArgumentException {
        assertValid(floorNumber);
        requests.add(ButtonPressed.outside(floorNumber));
    }

    @Override
    public int getFloorsCount() {
        return floorsCount;
    }

    private void assertValid(int floorNumber) {
        if (floorNumber < 1 || floorNumber > floorsCount) {
            throw new IllegalArgumentException(
                    "The floor number should be from 1 to " + floorsCount + ", Input " + floorNumber
            );
        }
    }


    public void start() {
        controllerThread.start();
    }

    public void stop() {
        controllerThread.interrupt();
    }


    @Override
    public void run() {
        try {
            elevatorListener.atFloor(currentFloor);
            while (!Thread.currentThread().isInterrupted()) {

                if (state == IDLE) {
                    state = waitForRequests();
                } else if (state == SELECT_DIRECTION) {
                    state = selectDirection();
                } else if (state == MOVE_DOWN) {
                    state = moveDown();
                } else if (state == MOVE_UP) {
                    state = moveUp();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("You have successfully exited the simulator...");
        }
    }

    private ElevatorState waitForRequests() throws InterruptedException {
        collect(requests.take());
        collectRequests();
        return SELECT_DIRECTION;
    }

    private void collect(ButtonPressed buttonPressed) {
        (buttonPressed.inside ? insidePressed : outsidePressed)
                .putIfAbsent(buttonPressed.floorNumber, buttonPressed);
    }

    private void collectRequests() {
        List<ButtonPressed> drainedTo = new ArrayList<>();
        requests.drainTo(drainedTo);
        drainedTo.forEach(this::collect);
    }

    private ElevatorState selectDirection() {
        if (!insidePressed.isEmpty()) {
            return selectDirection(insidePressed);
        } else if (!outsidePressed.isEmpty()) {
            return selectDirection(outsidePressed);
        } else {
            return IDLE;
        }
    }

    private ElevatorState selectDirection(Map<Integer, ButtonPressed> floorsPressed) {
        ButtonPressed floorToGo = floorsPressed.values()
                .stream()
                .min(Comparator.comparing(ButtonPressed::getTime))
                .get();

        if (floorToGo.floorNumber == currentFloor) {
            floorsPressed.remove(floorToGo.floorNumber);
            openLift();
            return SELECT_DIRECTION;
        } else {
            return (floorToGo.floorNumber < currentFloor) ? MOVE_DOWN : MOVE_UP;
        }
    }

    private ElevatorState moveDown() {
        elevator.down();
        while (currentFloor > minFloor()) {
            waitFor(floorTravelTimeMillis);
            currentFloor--;
            elevatorListener.atFloor(currentFloor);
            if (insidePressed.containsKey(currentFloor) || outsidePressed.containsKey(currentFloor)) {
                elevator.stop();
                insidePressed.remove(currentFloor);
                outsidePressed.remove(currentFloor);
                openLift();
                elevator.down();
            }
            collectRequests();
        }
        return insidePressed.isEmpty() && outsidePressed.isEmpty() ? IDLE : SELECT_DIRECTION;
    }

    private int minFloor() {
        return Math.min(
                minFloor(insidePressed.keySet()),
                minFloor(outsidePressed.keySet())
        );
    }

    private int minFloor(Set<Integer> set) {
        return set.stream()
                .min(Integer::compareTo)
                .orElse(floorsCount);
    }

    private ElevatorState moveUp() {
        elevator.up();
        while (currentFloor < maxFloor()) {
            waitFor(floorTravelTimeMillis);
            currentFloor++;
            elevatorListener.atFloor(currentFloor);
            if (insidePressed.containsKey(currentFloor) || outsidePressed.containsKey(currentFloor)) {
                elevator.stop();
                insidePressed.remove(currentFloor);
                outsidePressed.remove(currentFloor);
                openLift();
                elevator.up();
            }
            collectRequests();
        }
        return insidePressed.isEmpty() && outsidePressed.isEmpty() ? IDLE : SELECT_DIRECTION;
    }

    private int maxFloor() {
        return Math.max(
                maxFloor(insidePressed.keySet()),
                maxFloor(outsidePressed.keySet())
        );
    }

    private static int maxFloor(Set<Integer> set) {
        return set.stream()
                .max(Integer::compareTo)
                .orElse(1);
    }

    private void openLift() {
        elevatorListener.doorOpening();
        elevator.openCloseDoor();
        waitFor(doorOpenCloseTimeMillis);
        elevatorListener.doorClosed();
    }

    private static void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    static class ButtonPressed {
        private final int floorNumber;
        private final long time;
        private final boolean inside;

        static ButtonPressed inside(int floorNumber) {
            return new ButtonPressed(floorNumber, System.currentTimeMillis(), true);
        }

        static ButtonPressed outside(int floorNumber) {
            return new ButtonPressed(floorNumber, System.currentTimeMillis(), false);
        }

        private ButtonPressed(int floorNumber, long time, boolean inside) {
            this.floorNumber = floorNumber;
            this.time = time;
            this.inside = inside;
        }

        long getTime() {
            return time;
        }

    }
}
