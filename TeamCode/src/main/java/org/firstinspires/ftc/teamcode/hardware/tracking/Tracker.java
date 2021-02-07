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
    private final double starting_pos;
    private final double y_offset = -2.4;
    private double x_side;
    private double y_side;

    public Tracker(Turret turret, Drivetrain drivetrain, int starting_pos, int manual_pos){
        this.odometry = drivetrain.getOdometry();
        this.turret = turret;
        this.imu = odometry.getIMU();
        this.starting_pos = starting_pos;
        translateCoordinates(starting_pos, manual_pos);
    }

    /**
     * Updates power and rotation angle based on regression equations from training data
     */
    public void updateVars(){
        final double kP = 0;
        odometry.updateDeltas();
        updateLegs();
        //double power = kP * Math.pow((getHypo()), 2); // Assuming regression is exponential for now
        double rotation_distance = (getTargetHeading() / 360.0) * (turret.getTurretHome());
        // turret.shooter.setPower(power);
        turret.rotate(turret.getTurretHome() + rotation_distance);
    }

    /**
     * Starts robot on coordinate system where (0, 0) is the center of the field
     * Ensures that all starting points can match training data
     * @param starting_pos Where the robot will start (1 = BlueLeft, 2 = BlueRight, 3 = RedLeft, 4 = RedRight, 5 = Manual Input)
     */
    private void translateCoordinates(int starting_pos, int manual_pos){
        switch (starting_pos){
            case 5:
                this.odometry.setStartingPos(manual_pos);
            case 1:
                this.odometry.setStartingPos(0);
            case 2:
                this.odometry.setStartingPos(0);
            case 3:
                this.odometry.setStartingPos(0);
            case 4:
                this.odometry.setStartingPos(0);
        }
    }

    private void updateLegs(){
        x_side = odometry.getX() + 10;
        if (starting_pos <= 2) y_side = (odometry.getY() + 10) * -1;
        else if (starting_pos > 2) y_side = (odometry.getY() + 10) * 1;
    }

    /**
     * Uses odometry to get hypotenuse of right triangle depending on which goal is chosen
     * @return Hypotenuse of the right triangle (always positive)
     */
    public double getHypo(){
        return Math.sqrt(Math.pow(x_side, 2) + Math.pow(y_side, 2));
    }

    public double getTargetHeading(){
        double robot_heading = odometry.getIMU().getHeading();
        double turret_heading = -Math.toDegrees(Math.atan2(y_side, x_side)) + robot_heading;
        turret_heading %= 360;
        if (turret_heading < 0) turret_heading += 360;
        return turret_heading;
    }
}
