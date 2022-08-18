package org.firstinspires.ftc.teamcode.hardware.events;

import android.service.controls.Control;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.events.summerRobot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

@TeleOp(name = "!!Summer Robot TeleOp!!")
public class summerRobotTele extends OpMode {
    summerRobot Robot;
    Boolean intake;
    Boolean fallingEdge;
    Boolean fallingEdgeChecker;
    ControllerMap controllerMap;
    EventBus evBus;
    ControllerMap.AxisEntry forward;
    ControllerMap.AxisEntry strafe;
    ControllerMap.AxisEntry turn;
    ControllerMap.ButtonEntry toggle;
    ControllerMap.ButtonEntry gate;

    ControllerMap.ButtonEntry speedup;
    ControllerMap.ButtonEntry speeddown;

    Telemetry telemetry;

    double speed = 0.5;

    @Override
    public void init() {
        Robot = new summerRobot(hardwareMap);
        intake = false;
        fallingEdge = true;
        fallingEdgeChecker = false;
        Robot.gate.gate.setPosition(0.73);
        evBus = Robot.eventBus;
        telemetry = super.telemetry;
        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);
        forward = controllerMap.getAxisMap("forward", "gamepad1", "left_stick_y");
        strafe = controllerMap.getAxisMap("strafe", "gamepad1", "left_stick_x");
        turn = controllerMap.getAxisMap("turn", "gamepad1", "right_stick_x");
        toggle = controllerMap.getButtonMap("toggle", "gamepad1", "right_bumper");
        gate = controllerMap.getButtonMap("gate", "gamepad1", "left_bumper");

        speedup = controllerMap.getButtonMap("speedup", "gamepad2", "right_bumper");
        speeddown = controllerMap.getButtonMap("speeddown", "gamepad2", "left_bumper");
    }

    @Override
    public void loop() {

        if(toggle.edge() == -1){
            if(intake){
                Robot.intake.intake.setPower(1);
                Robot.shooter.shooter.setPower(speed);
            }else{
                Robot.intake.intake.setPower(0);
                Robot.shooter.shooter.setPower(0);
            }
            intake = !intake;
        }

        if (speedup.edge() == -1) speed += 0.05;
        if (speeddown.edge() == -1) speed -= 0.05;
        speed = Range.clip(speed, 0, 1);

        telemetry.addData("shooter speed: ", speed);

        if(gate.get()){
            Robot.gate.gate.setPosition(1);
        }else{
            Robot.gate.gate.setPosition(0.73);
        }

        Robot.chassis.move(forward.get()*0.5, strafe.get()*0.5, turn.get()*0.5, 0);

        controllerMap.update();
        evBus.update();

        telemetry.update();
    }
}