package org.firstinspires.ftc.teamcode.util.localhost;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
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
        Spark.get("/", (request, response) -> {
            response.redirect("index.html");
            return "";
        });
    }

    public void close(){
        Spark.stop();
    }
}
