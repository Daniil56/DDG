package ru.ddg.elevator;


import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import ru.ddg.elevator.controller.ControllerProps;
import ru.ddg.elevator.controller.ElevatorController;
import ru.ddg.elevator.controller.ElevatorListener;
import ru.ddg.elevator.controller.LoggingElevatorListener;
import ru.ddg.elevator.model.BasicElevator;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


/**
 * Elevator behavior test.
 */
public class ElevatorTest {

    private ElevatorController liftController;
    private ElevatorListener elevatorListener;

    private void setup(ControllerProps props) {
        liftController = new ElevatorController(new BasicElevator(), props);
        elevatorListener = Mockito.spy(new LoggingElevatorListener());
        liftController.setElevatorListener(elevatorListener);
        liftController.start();
    }

    @After
    public void tearDown() {
        liftController.stop();
    }

    @Test
    public void openDoorOn_1st_Floor() {
        setup(ControllerPropsBuilder.defaultProps());

        liftController.outsideButtonPressed(1);

        verify(elevatorListener, timeout(2000)).atFloor(1);
        assertDoorOpenCloseTime();
    }

    @Test
    public void from_1st_To_7th_Floor() {
        setup(ControllerPropsBuilder.defaultProps());

        liftController.insideButtonPressed(7);

        verify(elevatorListener, timeout(2000)).atFloor(1);
        
        for (int i = 2; i <= 7; i++) {
            Stopwatch timer = Stopwatch.start();
            verify(elevatorListener, timeout(10200)).atFloor(i);
            timer.assertDurationCloseTo(10, TimeUnit.SECONDS);
        }

        assertDoorOpenCloseTime();
    }

    private void assertDoorOpenCloseTime() {
        long expectedTimeMillis = TimeUnit.SECONDS.toMillis((long) 2);
        
        // door opening is being initiated
        verify(elevatorListener, timeout(2000)).doorOpening();
        Stopwatch timer = Stopwatch.start();

        // door is closed after doorOpenCloseTime
        verify(elevatorListener, timeout(expectedTimeMillis + 2000)).doorClosed();
        timer.assertDurationCloseTo((long) 2, TimeUnit.SECONDS);
    }

}
