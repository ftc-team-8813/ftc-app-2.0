package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;

public abstract class LoggingOpMode extends OpMode
{
    @Override
    public void stop()
    {
        Logger.close();
        Robot.close();
    }
}
