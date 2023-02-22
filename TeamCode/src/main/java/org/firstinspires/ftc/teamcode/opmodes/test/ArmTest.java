package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;

//@Disabled
@Config
@TeleOp(name="Arm Test")
public class ArmTest extends LoggingOpMode {

    private DcMotorEx arm;

    public static double exponent = 1;
    public static double target = 0;
    public static double kf = 0.1;
    public static double kp = 0.009;
    public static double ki = 0;
    public static double mxis = 0;


    private double coefficent;

    private final PID pid = new PID(kp, ki, 0, kf, mxis, 0);

    @Override
    public void init() {
        coefficent = Math.pow((12.5/getBatteryVoltage()),exponent);
        arm = hardwareMap.get(DcMotorEx.class,"arm");

        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        super.init();
    }

    @Override
    public void loop() {

        /*
         * -97
         * -102
         * -105
         * -110
         * -112.5
         */


        double power = Range.clip(pid.getOutPut(target, (-arm.getCurrentPosition() * 288.0 / 8192.0), Math.cos(Math.toRadians((-arm.getCurrentPosition() * 288.0 / 8192.0)))), -0.6, 0.6);

        arm.setPower((power * coefficent));

        telemetry.addData("Arm Position",-arm.getCurrentPosition() * 288.0 / 8192.0);
        telemetry.addData("Arm Power", power);
        telemetry.addData("Voltage Based Power", (power * coefficent));
        telemetry.addData("Voltage Coefficient", coefficent);
        telemetry.addData("Voltage", getBatteryVoltage());
        telemetry.addData("Exponent", exponent);
        telemetry.addData("Target",target);
        telemetry.update();
    }

    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }
}