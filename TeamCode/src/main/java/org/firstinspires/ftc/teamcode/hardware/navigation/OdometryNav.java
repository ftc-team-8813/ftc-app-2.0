package org.firstinspires.ftc.teamcode.hardware.navigation;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.Motor.Encoder;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.Serializable;

public class OdometryNav {

    private final Servo center_odo;
    private final Servo left_odo;
    private final Servo right_odo;

    public OdometryNav(Servo center_odo,Servo left_odo,Servo right_odo) {
        this.center_odo = center_odo;
        this.left_odo = left_odo;
        this.right_odo = right_odo;
    }

    public void setOdometry(double pos1, double pos2,double pos3) {
        center_odo.setPosition(pos1);
        left_odo.setPosition(pos2);
        right_odo.setPosition(pos3);
    }

    public void allOdoPosUp(){
        center_odo.setPosition(0);
        right_odo.setPosition(1);
        left_odo.setPosition(0.137);
    }

}
