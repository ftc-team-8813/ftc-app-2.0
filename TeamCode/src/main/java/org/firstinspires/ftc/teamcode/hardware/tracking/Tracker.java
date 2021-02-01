package org.firstinspires.ftc.teamcode.hardware.tracking;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

/**
 * Uses odometry class to update power and heading regressions
 */
public class Tracker {
    private Odometry odometry;
    private IMU imu;
    private Turret turret;
    private final int color; // Tells which side of the field (-1 = Blue, 1 = Red)
    private final double y_offset = -2.4;
    private double x_side;
    private double y_side;

    public Tracker(Turret turret, Drivetrain drivetrain, int starting_pos, int color){
        this.odometry = drivetrain.getOdometry();
        this.turret = turret;
        this.imu = odometry.getIMU();
        this.color = color;
        translateCoordinates(starting_pos);
    }

    /**
     * Updates power and rotation angle based on regression equations from training data
     */
    public void updateVars(){
        final double kP = 0;
        odometry.updateDeltas();
        updateLegs();
        double power = kP * Math.pow((getHypo()), 2); // Assuming regression is exponential for now
        double rotation_distance = (getTurretHeading() / 360.0) * (turret.getTurretHome2() - turret.getTurretHome());
        // turret.shooter.setPower(power);
        turret.rotate(turret.getTurretHome() + rotation_distance);
    }

    /**
     * Starts robot on coordinate system where (0, 0) is the center of the field
     * Ensures that all starting points can match training data
     * @param starting_pos Where the robot will start (1 = BlueLeft, 2 = BlueRight, 3 = RedLeft, 4 = RedRight)
     */
    public void translateCoordinates(int starting_pos){
        switch (starting_pos){
            case 1:
                this.odometry.setStartingPos(48);
            case 2:
                this.odometry.setStartingPos(24);
            case 3:
                this.odometry.setStartingPos(-24);
            case 4:
                this.odometry.setStartingPos(-48);
        }
    }

    public void updateLegs(){
        x_side = odometry.getX() + 72;
        y_side = (odometry.getY() + 36 + y_offset) * color;
    }

    /**
     * Uses odometry to get hypotenuse of right triangle depending on which goal is chosen
     * @return Hypotenuse of the right triangle (always positive)
     */
    public double getHypo(){
        return Math.sqrt(Math.pow(x_side, 2) + Math.pow(y_side, 2));
    }

    public double getTurretHeading(){
        double robot_heading = odometry.getIMU().getHeading();
        if (robot_heading > 180) {
            robot_heading = -360 + robot_heading;
        }
        if (robot_heading > 0){
            robot_heading = 180 + Math.abs(robot_heading);
        }
        return -Math.toDegrees(Math.atan(x_side / y_side)) - robot_heading;
    }
}
