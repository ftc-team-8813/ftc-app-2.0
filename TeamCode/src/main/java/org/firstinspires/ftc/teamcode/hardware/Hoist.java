package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class Hoist {
    private Servo h1;
    private Servo h2;

    public Hoist(Servo h1, Servo h2){
        this.h1 = h1;
        this.h2 = h2;
    }

    public void setHoist(double pos){
        h1.setPosition(pos);
        h2.setPosition(pos);
    }

}
