package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class DroneLauncher {
    public Servo droneLauncher;
    public Servo droneHeight;

    public DroneLauncher(Servo droneServo, Servo droneHeight){
        this.droneLauncher = droneServo;
        this.droneHeight = droneHeight;
    }

    public void setLaunchPos(double setPos){
        droneLauncher.setPosition(setPos);
    }

    public double getLaunchPos(){
        return droneLauncher.getPosition();
    }

    public void setLauncherHeight(double setPos){
        droneHeight.setPosition(setPos);
    }
    public double getLauncherHeight(){return droneHeight.getPosition();
    }


}
