package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

public class Intake {
    private final DcMotor intake;
    private final Servo dropper;
    private final Servo dropper_gate;
    private final ColorRangeSensor dist;


    public Intake(DcMotor intake, Servo dropper, Servo dropper_gate, ColorRangeSensor dist){
        this.intake = intake;
        this.dropper_gate = dropper_gate;
        this.dropper = dropper;
        this.dist = dist;
    }


    public void intake(){
        intake.setPower(1);
    }

    public void outtake(){
        intake.setPower(-1);
    }

    public void stop(){
        intake.setPower(0);
    }

    public void dropperClose() { dropper_gate.setPosition(Status.DEPOSIT_CLOSED); }

    public void dropperOpen() { dropper_gate.setPosition(Status.DEPOSIT_OPEN); }

    public double getDistance() {
        return dist.getDistance(DistanceUnit.MM);
    }
}
