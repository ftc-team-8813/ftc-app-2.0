package org.firstinspires.ftc.teamcode.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.google.zxing.BinaryBitmap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.opencv.core.Mat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@TeleOp
public class GamepadTest extends LoggingOpMode {

    private final String VUFORIA_LICENSE_KEY = "ASVVdVH/////AAABmZpPJttNS0ACqVAEDWXiUs9b3k6GUR82zmBIkqUy3Z6SXBi61oJIkw0ZPonsur+X3jtSCfZlRfRS45XUzFSZV6lWM4mvrakzRPz/yvtIZq29fNFIXRn7n/jwj11b1/4SgzGB+5pNvyt9zXU8NXZzlNFEQeuueQouirGpZPFZt3rg8mBJZTpjWaHf4oepLioX7+ubaKBryPriMNYYckKkWvOt6ewNCy/9+st8pws82d5QRjLCY7dPelQuzxrn6ZjBGc3WqxNBQwFwwskmD1sfE1NjQEs1d3svguj6siJ8OIj8Ga4eWydl++65Zb8oIVJq/3p1UzSd+R61JiO4XKmkcC5D3Cr7CK4P0DsZdKltMYcz";

    private static void logGamepad(Telemetry telemetry, Gamepad gamepad, String prefix) {
        telemetry.addData(prefix + "Synthetic",
                gamepad.getGamepadId() == Gamepad.ID_UNASSOCIATED);
        for (Field field : gamepad.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            try {
                telemetry.addData(prefix + field.getName(), field.get(gamepad));
            } catch (IllegalAccessException e) {
                // ignore for now
            }
        }
    }



    @Override
    public void init() {
        super.init();

        msStuckDetectStop = 2500;

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        vuforiaParams.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(vuforiaParams);

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);


    }

    @Override
    public void loop() {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        logGamepad(telemetry, gamepad1, "gamepad1");
        logGamepad(telemetry, gamepad2, "gamepad2");
        telemetry.update();

    }
}
