package org.firstinspires.ftc.teamcode.hardware.tracking;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Turret;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;

/**
 * Uses odometry class to update power and heading regressions
 */
public class Tracker {
    public Odometry odometry;
    public IMU imu;
    public Turret turret;
    public final int starting_pos;
    public final int color; // Tells which side of the field (-1 = Blue, 1 = Red)
    public final double TURRET_CIRCUMFERENCE = 0;
    public final double kP = 0;

    public Tracker(Turret turret, DcMotor top_left, DcMotor top_right, IMU imu, int starting_pos, int color){
        this.odometry = new Odometry(top_left, top_right, imu);
        this.turret = turret;
        this.imu = imu;
        this.starting_pos = starting_pos;
        this.color = color;
        translateCoordinates(starting_pos);
    }

    /**
     * Updates power and rotation angle based on regression equations from training data
     */
    public void updateVars(){
        this.odometry.updateDeltas();
        double rotation_distance = (this.imu.getHeading() / 360.0) * TURRET_CIRCUMFERENCE;
        double power = Math.pow(kP*(getHypo()), 2); // Assuming regression is exponential for now
        this.turret.shooter.setPower(power);
        this.turret.rotate(rotation_distance);
    }

    /**
     * Starts robot on coordinate system where (0, 0) is the center of the field
     * Ensures that all starting points can match training data
     * @param starting_pos Where the robot will start (1 = BlueLeft, 2 = BlueRight, 3 = RedLeft, 4 = RedRight)
     */
    public void translateCoordinates(int starting_pos){
        // TODO Find distance from center of the field for this.odometry.y
        switch (starting_pos){
            case 1:
                this.odometry.setStartingPos(-48, -50);
            case 2:
                this.odometry.setStartingPos(-24, -50);
            case 3:
                this.odometry.setStartingPos(24, -50);
            case 4:
                this.odometry.setStartingPos(48, -50);
        }
    }

    /**
     * Uses odometry to get hypotenuse of right triangle depending on which goal is chosen
     * @return Hypotenuse of the right triangle (always positive)
     */
    public double getHypo(){
        double x_side = odometry.getX() + 36 * this.color;
        double y_side = odometry.getY() + 72;
        return Math.sqrt(Math.pow(x_side, 2) + Math.pow(y_side, 2));
    }
}
