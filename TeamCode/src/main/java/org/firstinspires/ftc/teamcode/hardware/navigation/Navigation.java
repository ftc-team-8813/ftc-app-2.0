package org.firstinspires.ftc.teamcode.hardware.navigation;

public class Navigation {
    private final Odometry odometry;
    private double target_x;
    private double target_y;

    public Navigation(Odometry odometry){
        this.odometry = odometry;
    }

    public void update(){
        double[] odo_data = odometry.getOdoData();
        double curr_x = odo_data[0];
        double curr_y = odo_data[1];
    }
}
