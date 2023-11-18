package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.hardware.CommandBasedDriveSystem;

import java.util.function.DoubleSupplier;

public class DriveCommand extends CommandBase {
    private CommandBasedDriveSystem driveSystem;
    private DoubleSupplier strafe, forward, turn;


    public DriveCommand(CommandBasedDriveSystem driveSystem, DoubleSupplier strafe, DoubleSupplier forward, DoubleSupplier turn){
        this.driveSystem = driveSystem;
        this.strafe = strafe;
        this.forward = forward;
        this.turn = turn;

        addRequirements(driveSystem);
    }

    @Override
    public void execute(){
        driveSystem.driveRobotCentric(strafe.getAsDouble(), forward.getAsDouble(), turn.getAsDouble());
    }

}
