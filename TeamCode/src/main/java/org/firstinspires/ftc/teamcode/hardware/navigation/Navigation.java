package org.firstinspires.ftc.teamcode.hardware.navigation;

public class Navigation {
    private final Odometry odometry;
    private double target_x;
    private double target_y;

    public Navigation(Odometry odometry){
        this.odometry = odometry;
    }

    public void update(){
        double curr_x = odometry.x;
        double curr_y = odometry.y;

        
    }
}
