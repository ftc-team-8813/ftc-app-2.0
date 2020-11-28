package org.firstinspires.ftc.teamcode.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import java.util.Arrays;

@TeleOp(name="CurrentTele")
public class CurrentTele extends OpMode {
    Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
    }

    @Override
    public void loop() {
        // Drivetrain (Normal Drive)
        robot.drivetrain.telemove(0.5*(gamepad1.left_stick_y), 0.5*(gamepad1.right_stick_x));
        robot.turret.liftGrab(gamepad2.left_stick_y, gamepad2.a);
        telemetry.addData("Potent Positions", Arrays.toString(robot.turret.getPotenPos()));
    }
}
