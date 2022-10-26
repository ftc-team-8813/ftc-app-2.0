package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.webserver.WebHost;

@TeleOp(name = "WebHost Test")
public class WebHostTest extends LoggingOpMode{
    private WebHost webhost;

    @Override
    public void start() {
        super.start();
        webhost = new WebHost();
        webhost.index();
    }

    @Override
    public void loop() {
        telemetry.addData("Running", "");
        telemetry.update();
    }

    @Override
    public void stop() {
        super.stop();
        webhost.close();
    }
}
