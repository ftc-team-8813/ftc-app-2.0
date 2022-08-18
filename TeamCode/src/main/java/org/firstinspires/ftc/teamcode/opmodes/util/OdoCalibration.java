package org.firstinspires.ftc.teamcode.opmodes.util;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

@TeleOp(name = "Odo Calibration")
@Disabled
public class OdoCalibration extends LoggingOpMode {
    private Robot robot;
    BNO055IMU imu;

    @Override
    public void init() {
        super.init();
        robot = Robot.initialize(hardwareMap);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    @Override
    public void loop() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double yaw = angles.firstAngle;

        double[] odo_data = new double[]{0.0, 0.0, 0.0};
        double l_enc = odo_data[0];
        double r_enc = odo_data[1];
        double average = (l_enc - r_enc) / 2;

        if (0 <= yaw && yaw <= 180){
            robot.drivetrain.move(0, 0, -0.25, 1); // Turns clockwise
        } else {
            robot.drivetrain.move(0, 0, 0, 1);
        }

        telemetry.addData("Yaw: ", yaw);
        telemetry.addData("Turn Tick Average: ", average);
        telemetry.update();
    }
}
