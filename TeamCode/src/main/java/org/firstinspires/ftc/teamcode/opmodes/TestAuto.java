package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.hardware.IMU;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.events.IMUEvent;
import org.firstinspires.ftc.teamcode.hardware.events.AutoMoveEvent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.firstinspires.ftc.teamcode.util.event.EventBus.Subscriber;
import org.firstinspires.ftc.teamcode.util.event.EventFlow;
import org.firstinspires.ftc.teamcode.util.event.TimerEvent;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

@Autonomous(name="Test Auto")
public class TestAuto extends LoggingOpMode {
    private Robot robot;
    private EventBus bus;
    private Scheduler scheduler;
    private EventFlow flow;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        bus = new EventBus();
        IMU imu = robot.drivetrain.getOdometry().getIMU();
        scheduler = new Scheduler(bus);

        imu.setImmediateStart(true);
        imu.initialize(bus, scheduler);
        Scheduler.Timer resetTimer = scheduler.addPendingTrigger(0.5, "Reset Delay");
        bus.subscribe(IMUEvent.class, (ev, bus, sub) -> {
            if (ev.new_state == IMU.STARTED)
                resetTimer.reset();
        }, "Reset Heading -- Delay", 0);
        bus.subscribe(TimerEvent.class, (ev, bus, sub) -> {
            imu.resetHeading();
        }, "Reset Heading", resetTimer.eventChannel);

        flow = new EventFlow(bus);
        flow.start(new Subscriber<>(AutoMoveEvent.class, (ev, bus, sub) -> {
                robot.drivetrain.setTargetPos(24);
            }, "Forward 20", AutoMoveEvent.MOVED))
            .then(new Subscriber<>(AutoMoveEvent.class, (ev, bus, sub) -> {
                robot.drivetrain.setTargetTurn(90);
            }, "Turn 90", AutoMoveEvent.MOVED));

        robot.drivetrain.resetEncoders();
        robot.drivetrain.connectEventBus(bus);
    }

    @Override
    public void start(){
        bus.pushEvent(new AutoMoveEvent(AutoMoveEvent.MOVED));
    }

    @Override
    public void loop() {
        telemetry.addData("Target Pos", robot.drivetrain.l_target);
        robot.drivetrain.autoPIDUpdate();
        scheduler.loop();
        bus.update();
        telemetry.update();
    }
}
