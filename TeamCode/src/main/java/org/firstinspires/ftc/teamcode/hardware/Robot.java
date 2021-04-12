package org.firstinspires.ftc.teamcode.hardware;

import com.google.gson.JsonObject;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.util.BuildInfo;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.Storage;
import org.firstinspires.ftc.teamcode.util.event.EventBus;

public class Robot {
    public final Drivetrain drivetrain;
    public final Intake intake;
    public final Turret turret;
    public final Wobble wobble;
    public final REVHub controlHub;
    public final REVHub expansionHub;
    
    public final JsonObject config;

    public final IMU imu;
    
    public final EventBus eventBus;
    public final Scheduler scheduler;
    
    private final Logger log = new Logger("Robot");
    
    ///////////////////////////////
    // Singleton things          //
    private static Robot instance;
    
    public static Robot initialize(HardwareMap hardwareMap, String initMessage)
    {
        instance = new Robot(hardwareMap, initMessage);
        return instance;
    }
    
    public static void close()
    {
        instance = null;
    }
    
    public static Robot instance()
    {
        return instance;
    }
    //                           //
    ///////////////////////////////
    
    private Robot(HardwareMap hardwareMap, String initMessage){
        log.i("ROBOT INIT -- %s", initMessage);
        BuildInfo buildInfo = new BuildInfo(Storage.getFile("buildinfo.json"));
        buildInfo.logInfo();
        
        config = Configuration.readJson(Storage.getFile("config.json"));
        
        eventBus = new EventBus();
        scheduler = new Scheduler(eventBus);
        /*
        log.d("Hardware Map");
        for (HardwareDevice dev : hardwareMap.getAll(HardwareDevice.class))
        {
            String name = hardwareMap.getNamesOf(dev).iterator().next();
            log.d("%s: %s -- %s", name, dev.getClass().getSimpleName(), dev.getConnectionInfo());
        }
        */
        
        controlHub = new REVHub(hardwareMap.get(LynxModule.class, "Control Hub"));
        expansionHub = new REVHub(hardwareMap.get(LynxModule.class, "Expansion Hub"));
        
        // Hardware Maps
        DcMotor top_left = hardwareMap.get(DcMotor.class, "top left");
        DcMotor bottom_left = hardwareMap.get(DcMotor.class, "bottom left");
        DcMotor top_right = hardwareMap.get(DcMotor.class, "top right");
        DcMotor bottom_right = hardwareMap.get(DcMotor.class, "bottom right");
        DcMotor l_enc = hardwareMap.get(DcMotor.class, "turret");
        DcMotor r_enc = hardwareMap.get(DcMotor.class, "ramp");
        DcMotor turret_enc = hardwareMap.get(DcMotor.class, "bottom left");
        DcMotor shooter = hardwareMap.get(DcMotor.class, "shooter");
        // DcMotor shooter2 = hardwareMap.get(DcMotor.class, "shooter2");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        DcMotor ramp = hardwareMap.get(DcMotor.class, "ramp");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");

        CRServo puller = hardwareMap.get(CRServo.class, "puller");
        Servo pusher = hardwareMap.get(Servo.class, "pusher");
        Servo pivot = hardwareMap.get(Servo.class, "pivot");
        
        Servo wobble_arm = hardwareMap.get(Servo.class, "wobble a");
        Servo wobble_claw = hardwareMap.get(Servo.class, "wobble claw");

        // Sub-Assemblies
        this.drivetrain = new Drivetrain(top_left, bottom_left, top_right, bottom_right, new Odometry(l_enc, r_enc));
        this.imu = this.drivetrain.getOdometry().getIMU();
        
        DigitalChannel turretZero = hardwareMap.digitalChannel.get("turret_switch");
        this.turret = new Turret(turret, shooter, pusher, null, turret_enc,
                                 config.getAsJsonObject("shooter"),
                                 config.getAsJsonObject("turret"), turretZero);
        this.turret.connectEventBus(eventBus);
        this.intake = new Intake(ramp, intake, puller, pivot, config.getAsJsonObject("intake"));
        
        this.wobble = new Wobble(wobble_arm, wobble_claw,
                                 config.getAsJsonObject("wobble"));
    }
    
    
}
