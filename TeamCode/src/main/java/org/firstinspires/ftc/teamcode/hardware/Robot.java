package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Storage;

public class Robot {
    public final Drivetrain drivetrain;
    public final Intake intake;
    public final Turret turret;
    public final SimpleLift lift;
    public final Wobble wobble;
    public final REVHub controlHub;
    
    public final JsonObject config;

    @Deprecated
    public final BNO055IMU bn;

    public final IMU imu;
    
    private Logger log = new Logger("Robot");
    
    public Robot(HardwareMap hardwareMap){
        config = Configuration.readJson(Storage.getFile("config.json"));
        log.d("Hardware Map");
        for (HardwareDevice dev : hardwareMap.getAll(HardwareDevice.class))
        {
            String name = hardwareMap.getNamesOf(dev).iterator().next();
            log.d("%s: %s -- %s", name, dev.getClass().getSimpleName(), dev.getConnectionInfo());
        }
        
        controlHub = new REVHub(hardwareMap.get(LynxModule.class, "Control Hub"));
        
        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "top left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "bottom left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "top right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "bottom right");
        DcMotor l_enc = hardwareMap.get(DcMotor.class, "turret");
        DcMotor r_enc = hardwareMap.get(DcMotor.class, "intake");
        DcMotor shooter = hardwareMap.get(DcMotor.class, "shooter");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        DcMotor ramp = hardwareMap.get(DcMotor.class, "ramp");

        CRServo puller = hardwareMap.get(CRServo.class, "puller");
        Servo pusher = hardwareMap.get(Servo.class, "pusher");
        Servo aim = null; // hardwareMap.get(Servo.class, "aim");
        
        Servo wobble_arm = hardwareMap.get(Servo.class, "wobble a");
        Servo wobble_claw = hardwareMap.get(Servo.class, "wobble claw");
        
        Servo lift_a = hardwareMap.get(Servo.class, "lift a");
        Servo lift_b = hardwareMap.get(Servo.class, "lift b");
        
        this.bn = hardwareMap.get(BNO055IMU.class, "imu");
        this.imu = new IMU(this.bn);

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right, new Odometry(l_enc, r_enc, this.imu));
        
        AnalogInput turretFeedback = hardwareMap.get(AnalogInput.class, "turret");
        this.turret = new Turret(turret, shooter, pusher, aim, turretFeedback,
                                 config.getAsJsonObject("shooter"),
                                 config.getAsJsonObject("turret_cal"),
                                 config.getAsJsonObject("turret"));
        this.intake = new Intake(intake, ramp, puller);
        
        this.lift = new SimpleLift(lift_a, lift_b,
                                   config.getAsJsonObject("lift"));
        
        this.wobble = new Wobble(wobble_arm, wobble_claw,
                                 config.getAsJsonObject("wobble"));
    }
}
