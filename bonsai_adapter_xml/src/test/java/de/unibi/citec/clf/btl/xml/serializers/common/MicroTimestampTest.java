package de.unibi.citec.clf.btl.xml.serializers.common;


import de.unibi.citec.clf.btl.data.common.MicroTimestamp;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Test for the {@link MicroTimestamp}.
 *
 * @author dklotz
 */
public class MicroTimestampTest {

    /**
     * A general test for basic assumptions about the class.
     */
    @Test
    public void smokeTest() {
        long seconds = 242554;
        long microSeconds = 53675;

        MicroTimestamp mts = new MicroTimestamp(seconds, microSeconds);

        assertEquals(seconds, mts.getSeconds());
        assertEquals(microSeconds, mts.getMicroSeconds());

        // equals / hashCode:

        assertEquals(mts, mts);
        assertEquals(mts.hashCode(), mts.hashCode());

        MicroTimestamp mts1 = new MicroTimestamp(seconds, microSeconds);
        assertEquals(mts, mts1);
        assertEquals(mts1, mts);
        assertEquals(mts.hashCode(), mts1.hashCode());

        MicroTimestamp mts2 = new MicroTimestamp(seconds + 5, microSeconds - 23);
        assertFalse(mts.equals(mts2));
        assertFalse(mts2.equals(mts));
        assertFalse(mts2.equals(mts1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorChecking1() {
        new MicroTimestamp(0, 1000001);
        // An exception should be thrown.
        fail("Just to be sure...");
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorChecking2() {
        MicroTimestamp mts = new MicroTimestamp();
        mts.setMicroSeconds(Long.MAX_VALUE);
        // An exception should be thrown.
        fail("Just to be sure...");
    }

    /**
     * A test for the unit-based conversion methods.
     */
    @Test
    public void timeConversion() {
        long currentTime = System.currentTimeMillis();

        MicroTimestamp mts = new MicroTimestamp(currentTime, TimeUnit.MILLISECONDS);

        assertEquals(currentTime, mts.getInUnit(TimeUnit.MILLISECONDS));

        // 1 second
        mts.setInUnit(1000000, TimeUnit.MICROSECONDS);
        assertEquals(1, mts.getSeconds());
        assertEquals(0, mts.getMicroSeconds());

        // 1 second + 1 microsecond
        mts.setInUnit(1000001, TimeUnit.MICROSECONDS);
        assertEquals(1, mts.getSeconds());
        assertEquals(1, mts.getMicroSeconds());

        // 1 second + 123 microseconds
        mts.setInUnit(1000123, TimeUnit.MICROSECONDS);
        assertEquals(1, mts.getSeconds());
        assertEquals(123, mts.getMicroSeconds());

        // 1 second - 1 microsecond
        mts.setInUnit(1000000 - 1, TimeUnit.MICROSECONDS);
        assertEquals(0, mts.getSeconds());
        assertEquals(1000000 - 1, mts.getMicroSeconds());

        // other possibilities...
        mts.setSeconds(23);
        mts.setMicroSeconds(0);
        assertEquals(23, mts.getInUnit(TimeUnit.SECONDS));

        mts.setInUnit(42, TimeUnit.SECONDS);
        assertEquals(42, mts.getSeconds());
        assertEquals(0, mts.getMicroSeconds());

        mts.setInUnit(42, TimeUnit.MICROSECONDS);
        assertEquals(0, mts.getSeconds());
        assertEquals(42, mts.getMicroSeconds());

        mts.setInUnit(10, TimeUnit.DAYS);
        assertEquals(10, mts.getInUnit(TimeUnit.DAYS));

        mts.setInUnit(23, TimeUnit.HOURS);
        assertEquals(23, mts.getInUnit(TimeUnit.HOURS));

        mts.setInUnit(1, TimeUnit.HOURS);
        assertEquals(60, mts.getInUnit(TimeUnit.MINUTES));

        mts.setInUnit(1, TimeUnit.MINUTES);
        assertEquals(60 * 1000L * 1000L, mts.getInUnit(TimeUnit.MICROSECONDS));

        mts.setInUnit(123456789L, TimeUnit.MILLISECONDS);
        assertEquals(123456L, mts.getSeconds());
        assertEquals(1000L * 789L, mts.getMicroSeconds());
        assertEquals(123456789L, mts.getInUnit(TimeUnit.MILLISECONDS));

        // the MicroTimestamp only has microseconds precision,
        // so we are loosing precision in these two cases:
        mts.setInUnit(435, TimeUnit.NANOSECONDS);
        assertEquals(0, mts.getSeconds());
        assertEquals(0, mts.getMicroSeconds());

        mts.setInUnit(3435, TimeUnit.NANOSECONDS);
        assertEquals(0, mts.getSeconds());
        assertEquals(3, mts.getMicroSeconds());
    }

    @Test
    public void selfCompatibility() {

    }
}
