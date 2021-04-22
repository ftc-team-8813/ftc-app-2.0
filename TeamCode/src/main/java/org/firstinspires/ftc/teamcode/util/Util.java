package org.firstinspires.ftc.teamcode.util;

public class Util
{
    public static double sum(double[] data)
    {
        double total = 0;
        for (double d : data) total += d;
        return total;
    }
    
    public static double average(double[] data)
    {
        return sum(data) / data.length;
    }
    
    public static double max(double[] data)
    {
        double max = Double.NEGATIVE_INFINITY;
        for (double d : data) if (d > max) max = d;
        return max;
    }
    
    public static double min(double[] data)
    {
        double min = Double.POSITIVE_INFINITY;
        for (double d : data) if (d < min) min = d;
        return min;
    }
}
