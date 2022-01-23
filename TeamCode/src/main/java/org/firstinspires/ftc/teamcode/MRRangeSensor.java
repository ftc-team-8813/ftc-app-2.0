/*
Modern Robotics Range Sensor Example
Created 9/8/2016 by Colton Mehlhoff of Modern Robotics using FTC SDK 2.x Beta
Reuse permitted with credit where credit is due

Configuration:
I2cDevice on an Interface Module named "range" at the default address of 0x28 (0x14 7-bit)

This program can be run without a battery and Power Destitution Module.

For more information, visit modernroboticsedu.com.
Support is available by emailing support@modernroboticsinc.com.
*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "MR Range Sensor", group = "MRI")
public class MRRangeSensor extends OpMode {

    I2cAddr RANGE1ADDRESS = new I2cAddr(0x14); //I2C address for MR Range (7-bit)
    public static final int RANGE1_REG_START = 0x04; //Register to start reading
    public static final int RANGE1_READ_LENGTH = 2; //Number of byte to read

    public ModernRoboticsI2cRangeSensor sensor;

    @Override
    public void init() {
        sensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "range");
        sensor.setI2cAddress(I2cAddr.create7bit(0x14 ));
    }

    @Override
    public void loop() {
        telemetry.addData("I2c Address", sensor.getI2cAddress());
        telemetry.addData("Distance", sensor.getDistance(DistanceUnit.CM));
        telemetry.addData("Ultra Sonic", sensor.cmUltrasonic());
        telemetry.addData("ODS", sensor.cmOptical());
    }

    @Override
    public void stop() {

    }

}
