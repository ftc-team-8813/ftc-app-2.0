package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.util.websocket.InetSocketServer;
import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ServerControl extends ControlModule{
    private Server server;

    public ServerControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        try {
            server = new Server(new InetSocketServer(18888));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Plotter
        server.registerProcessor(0x1, (cmd, payload, resp) -> {
            ByteBuffer buf = ByteBuffer.allocate(500);

            buf.putDouble(robot.drivetrain.getHeading());

            buf.flip();
            resp.respond(buf);
        });

        // Odo Drawer
//        server.registerProcessor(0x2, (cmd, payload, resp) -> {
//            ByteBuffer buf = ByteBuffer.allocate(300);
//
//            double[] nav_poses = robot.navigation.getFieldPositions();
//            buf.putDouble(nav_poses[0]);
//            buf.putDouble(nav_poses[1]);
//            buf.putDouble(nav_poses[2]);
//
//            buf.flip();
//            resp.respond(buf);
//        });
        server.startServer();
    }

    @Override
    public void update(Telemetry telemetry) {
        // No update
    }

    public void stop(){
        server.close();
    }
}
