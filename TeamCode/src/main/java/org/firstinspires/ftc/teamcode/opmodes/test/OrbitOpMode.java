package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/*
 * Demonstration of the dashboard's field overlay display capabilities.
 */
@Config
@Autonomous(name="OrbitOpMode")
public class OrbitOpMode extends LinearOpMode {
//    public static double ORBITAL_FREQUENCY = 0.05;
//    public static double SPIN_FREQUENCY = 0.25;
//
//    public static double ORBITAL_RADIUS = 50;
    public static double x = -30.5;
    public static double y = -62.0;
    public static double SIDE_LENGTH = 10;

    // (-30.5,-62.0)

    private static void rotatePoints(double[] xPoints, double[] yPoints, double x_cor, double y_cor) {
        for (int i = 0; i < xPoints.length; i++) {
            double x = xPoints[i];
            double y = yPoints[i];
            xPoints[i] = x + x_cor;
            yPoints[i] = y + y_cor;
//            x = xPoints[i];
//            y = yPoints[i];
//            xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle);
//            yPoints[i] = x * Math.sin(angle) + y * Math.cos(angle);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

//            double bx = ORBITAL_RADIUS * Math.cos(2 * Math.PI * ORBITAL_FREQUENCY * time);
//            double by = ORBITAL_RADIUS * Math.sin(2 * Math.PI * ORBITAL_FREQUENCY * time);

            double l = SIDE_LENGTH / 2;

            double[] bxPoints = { l, -l, -l, l };
            double[] byPoints = { l, l, -l, -l };
            rotatePoints(bxPoints, byPoints,x,y);

            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay()
                    .setStrokeWidth(1)
                    .setFill("black")
                    .fillPolygon(bxPoints, byPoints);
            dashboard.sendTelemetryPacket(packet);

            sleep(20);
        }
    }
}