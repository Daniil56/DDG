package ru.ddg.elevator;

import org.junit.Assert;

import java.util.concurrent.TimeUnit;

/**
 */
class Stopwatch {
    private static final long CORRECTION_MILLIS = 200;
    private final long startTime = System.currentTimeMillis();

    private Stopwatch() {
    }
    
    static Stopwatch start() {
        return new Stopwatch();
    }

    void assertDurationCloseTo(long duration, TimeUnit timeUnit) {
        final long actualDurationMillis = System.currentTimeMillis() - startTime;
        final long expectedDurationMillis = timeUnit.toMillis(duration);
        
        Assert.assertTrue(
                "expected: " + expectedDurationMillis + " actual: " + actualDurationMillis,
                expectedDurationMillis - CORRECTION_MILLIS <= actualDurationMillis &&
                        actualDurationMillis <= expectedDurationMillis + CORRECTION_MILLIS
        );
    }
}
