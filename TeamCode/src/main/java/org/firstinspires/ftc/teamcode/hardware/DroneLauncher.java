package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class DroneLauncher {
    public Servo droneServo;

    public DroneLauncher(Servo droneServo){
        this.droneServo = droneServo;
    }

    public void setLaunchPos(double setPos){
        droneServo.setPosition(setPos);
    }

    public double getLaunchPos(){
        return droneServo.getPosition();
    }



}
