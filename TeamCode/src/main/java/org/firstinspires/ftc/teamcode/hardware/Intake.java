package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Status;

public class Intake {
    private final DcMotor intake;
    private final ColorRangeSensor dist;


    public Intake(DcMotor intake, ColorRangeSensor dist){
        this.intake = intake;
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

    public double getDistance() {
        return dist.getDistance(DistanceUnit.MM);
    }
}
