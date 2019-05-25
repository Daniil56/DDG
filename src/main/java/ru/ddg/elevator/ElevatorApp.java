package ru.ddg.elevator;

import ru.ddg.elevator.controller.ElevatorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 */
@SpringBootApplication
public class ElevatorApp implements CommandLineRunner {
    
    @Autowired
    private InputConsumer inputCollector;
    @Autowired
    private ElevatorController controller;

    public static void main(String[] args) {
        SpringApplication.run(ElevatorApp.class, args);
    }

    @Override
    public void run(String... args) {
        controller.start();
        try {
            inputCollector.consumeInput(System.in);
        } finally {
            controller.stop();
        }
    }
}
