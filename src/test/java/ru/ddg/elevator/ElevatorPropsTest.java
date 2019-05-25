package ru.ddg.elevator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ddg.elevator.controller.ControllerProps;

/**
 * Test loads production application context (except InputConsumer) 
 * and checks that ControllerProps are populated.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElevatorPropsTest {
    
    @MockBean
    private InputConsumer inputConsumer;
    
    @Autowired
    private ControllerProps config;

    @Test
    public void testConfig() {
        Assert.assertEquals(ControllerPropsBuilder.defaultProps(), config);
    }
}
