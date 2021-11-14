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

            buf.putDouble(robot.lift.getCurrentLiftPos());

            buf.putDouble(robot.lift.getPower());
            double[] pid_terms = robot.lift.getPIDTerms();
            buf.putDouble(pid_terms[0]); // P Term
            buf.putDouble(pid_terms[1]); // I Term
            buf.putDouble(pid_terms[2]); // D Term

            double[] odo_enc_poses = robot.odometry.getCurrentPositions();
            buf.putDouble(odo_enc_poses[0]); // L Enc
            buf.putDouble(odo_enc_poses[1]); // R Enc
            buf.putDouble(odo_enc_poses[2]); // S Enc

            double[] odo_data = robot.odometry.getOdoData();
            buf.putDouble(odo_data[0]);
            buf.putDouble(odo_data[1]);
            buf.putDouble(odo_data[2]);

            double[] go_to_power = robot.drivetrain.getPositionDeltas();
            buf.putDouble(go_to_power[0]);
            buf.putDouble(go_to_power[1]);
            buf.putDouble(go_to_power[2]);


            buf.flip();
            resp.respond(buf);
        });

        // Odo Drawer
        server.registerProcessor(0x2, (cmd, payload, resp) -> {
            ByteBuffer buf = ByteBuffer.allocate(300);
            double[] odo_data = robot.odometry.getOdoData();
            buf.putDouble(odo_data[0]); // x
            buf.putDouble(odo_data[1]); // y
            buf.putDouble(odo_data[2]); // heading

            buf.flip();
            resp.respond(buf);
        });

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
