package ru.ddg.elevator;

import ru.ddg.elevator.controller.ElevatorControls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Consumes input from a user.
 */
public class InputConsumer {

    private final List<Function<String, Boolean>> consumers = new ArrayList<>();
    private final int floorsCount;

    public InputConsumer(ElevatorControls elevatorControls) {
        this.floorsCount = elevatorControls.getFloorsCount();

        consumers.add((String input) -> {
            if (input.matches("\\d+")) {
                try {
                    elevatorControls.outsideButtonPressed(
                            Integer.parseInt(input)
                    );
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                return true;
            }
            return false;
        });

        consumers.add((String input) -> {
            if (input.matches("\\[\\d+]")) {
                try {
                    elevatorControls.insideButtonPressed(
                            Integer.parseInt(
                                    input.substring(1, input.length() - 1)
                            )
                    );
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                return true;
            }
            return false;
        });
        consumers.add((String input) -> {
            showTryAgainMessage(input);
            return true;
        });
    }

    public void consumeInput(InputStream source) {

        showWelcomeMessage();

        final Scanner scanner = new Scanner(source);

        while (true) {

            final String input = scanner.next();

            if (isExit(input)) {
                return;
            }

            consume(input);
        }
    }

    private void consume(String input) {
        for (Function<String, Boolean> consumer : consumers) {
            if (consumer.apply(input)) {
                return;
            }
        }
    }

    private static boolean isExit(String input) {
        return input.matches("(?i)exit|quit");
    }

    private void showWelcomeMessage() {
        System.out.println("Please enter the floor number from 1 to " + floorsCount );
        System.out.println("Internal call in the Elevator");
        System.out.println(" - to select a floor inside the Elevator, enter a Prime number: 1 or 2 or 3 ... ");
        System.out.println("External Elevator call");
        System.out.println(" - For an external Elevator call, enter a number in square brackets: [1] or [2] or [3] ...");
        System.out.println("Exit");
        System.out.println(" - Enter quit or exit to exit the simulator");
    }

    private static void showTryAgainMessage(String input) {
        System.out.println("Input '" + input + "' doesnt not exit , please ty again...");
    }
}
