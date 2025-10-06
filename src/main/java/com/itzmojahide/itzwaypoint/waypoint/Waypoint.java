package com.itzmojahide.itzwaypoint.waypoint;

public class Waypoint {
    public String name;
    public double x, y, z;
    public int color;

    public Waypoint() {} // Needed for GSON

    public Waypoint(String name, double x, double y, double z, int color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }
}
