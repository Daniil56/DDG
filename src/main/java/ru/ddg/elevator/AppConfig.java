package ru.ddg.elevator;

import ru.ddg.elevator.controller.ControllerProps;
import ru.ddg.elevator.controller.ElevatorController;
import ru.ddg.elevator.controller.ElevatorControls;
import ru.ddg.elevator.controller.LoggingElevatorListener;
import ru.ddg.elevator.model.BasicElevator;
import ru.ddg.elevator.model.Elevator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring application configuration.
 */
@Configuration
@EnableConfigurationProperties(ControllerProps.class)
public class AppConfig {
    
    @Bean
    public Elevator elevator() {
        return new BasicElevator();
    }
    
    @Bean
    public ElevatorController elevatorController(Elevator elevator, ControllerProps props) {
        final ElevatorController elevatorController = new ElevatorController(elevator, props);
        elevatorController.setElevatorListener(new LoggingElevatorListener());
        return elevatorController;
    }
    
    @Bean
    public InputConsumer inputCollector(ElevatorControls elevatorControls) {
        return new InputConsumer(elevatorControls);
    }
}
