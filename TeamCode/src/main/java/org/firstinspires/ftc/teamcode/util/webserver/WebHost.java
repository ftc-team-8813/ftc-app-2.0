package org.firstinspires.ftc.teamcode.util.webserver;

import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Spark;
//import spark.template.velocity.VelocityTemplateEngine;

public class WebHost {
    Robot robot;

    /**
     * Hosts a site on robot's IP
     * All templates are stored in src/main/resources/public (for some reason, that's the default)
     */
    public WebHost(Robot robot){
        this.robot = robot;
        Spark.staticFiles.location("/public");
        Spark.port(8813);
    }

    public void index(){
        Spark.get("/", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("lift_pos", robot.lift.getLiftCurrentPos());
            return new ModelAndView(attributes, "index.vm");
        }, new VelocityTemplateEngine());
    }

    public void close(){
        Spark.stop();
    }
}
