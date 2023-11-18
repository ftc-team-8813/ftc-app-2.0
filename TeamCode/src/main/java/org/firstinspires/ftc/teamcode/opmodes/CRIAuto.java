package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.commands.DriveCommand;
import org.firstinspires.ftc.teamcode.hardware.CommandBasedDriveSystem;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous(name = "!!Command TeleOp!")
public class CRIAuto extends CommandOpMode {

    private Motor FL, BL, FR, BR;
    private CommandBasedDriveSystem driveSystem;
    private DriveCommand driveCommand;

    private GamepadEx controller1;



    @Override
    public void initialize(){
        FL = new Motor(hardwareMap, "FL");
        BL = new Motor(hardwareMap, "BL");
        FR = new Motor(hardwareMap, "FR");
        BR = new Motor(hardwareMap, "BR");

        controller1 = new GamepadEx(gamepad1);

        driveSystem = new CommandBasedDriveSystem(FL, BL, FR, BR);
        driveCommand = new DriveCommand(driveSystem, controller1::getLeftX, controller1::getLeftY, controller1::getRightX);

        register(driveSystem);
        driveSystem.setDefaultCommand(driveCommand);

    }


}
