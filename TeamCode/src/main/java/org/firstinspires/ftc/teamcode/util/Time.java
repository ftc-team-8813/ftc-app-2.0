package org.firstinspires.ftc.teamcode.util;

public class Time
{
    /**
     * Get the current time (not calendar time) in seconds. Uses System.nanoTime() for the time source
     *
     * @return the current time in seconds
     */
    public static double now()
    {
        return (double) System.nanoTime() / 1_000_000_000.0;
    }

    /**
     * Get the time elapsed since a specific point
     *
     * @param start The result of {@link #now()} at a specific point in the past
     * @return How many seconds since that first time
     */
    public static double since(double start)
    {
        return now() - start;
    }

    public static String format(double time)
    {
        StringBuilder builder = new StringBuilder();
        boolean minus = (time < 0);

        if (minus) time = -time;

        if (time < 60) // under 1 minute
        {
            double magnitude = Math.log10(time);
            String unit;
            if (magnitude < -6)
            {
                unit = "ns";
                time *= 1e9;
            }
            else if (magnitude < -3)
            {
                unit = "us";
                time *= 1e6;
            }
            else if (magnitude < 0)
            {
                unit = "ms";
                time *= 1e3;
            }
            else
            {
                unit = "s";
            }
            return String.format("%7.3f %s", time, unit);
        }
        else
        {
            if (time < 3600)
            {
                double min = Math.floor(time / 60);
                double sec = time % 60;
                return String.format("%2.0fmin %6.3fs", min, sec);
            }
            else
            {
                double hour = Math.floor(time / 3600);
                double min = Math.floor((time / 60) % 60);
                double sec = Math.floor(time % 60);
                return String.format("%2.0fh, %2.0fmin, %2.0fs", hour, min, sec);
            }
        }
    }
}
