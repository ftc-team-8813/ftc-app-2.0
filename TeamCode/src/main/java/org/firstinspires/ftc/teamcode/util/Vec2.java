package org.firstinspires.ftc.teamcode.util;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

public class Vec2
{
    public double x;
    public double y;
    
    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vec2(Vec2 other)
    {
        this.x = other.x;
        this.y = other.y;
    }
    
    public Vec2()
    {
        this.x = 0;
        this.y = 0;
    }
    
    public Vec2 translate(double x, double y)
    {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vec2 rotate(double theta)
    {
        double newX = this.x * cos(theta) - this.y * sin(theta);
        double newY = this.x * sin(theta) + this.y * cos(theta);
        this.x = newX;
        this.y = newY;
        return this;
    }
    
    public Vec2 scale(double factor)
    {
        return scale(factor, factor);
    }
    
    public Vec2 scale(double x, double y)
    {
        this.x *= x;
        this.y *= y;
        return this;
    }
    
    public double angle()
    {
        return atan2(this.y, this.x);
    }
    
    public double magnitude()
    {
        return hypot(this.x, this.y);
    }
    
    public Vec2 normalize()
    {
        double mag = magnitude();
        this.x /= mag;
        this.y /= mag;
        return this;
    }
    
    public Vec2 add(Vec2 other)
    {
        translate(other.x, other.y);
        return this;
    }
    
    public Vec2 sub(Vec2 other)
    {
        translate(-other.x, -other.y);
        return this;
    }
    
    public double dot(Vec2 other)
    {
        return this.x * other.x + this.y * other.y;
    }
    
    public static Vec2 fromPolar(double mag, double theta)
    {
        return new Vec2(mag * cos(theta), mag * sin(theta));
    }
}
