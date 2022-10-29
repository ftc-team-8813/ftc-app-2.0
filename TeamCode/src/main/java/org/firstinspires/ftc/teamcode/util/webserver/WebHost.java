package org.firstinspires.ftc.teamcode.util.webserver;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.firstinspires.ftc.teamcode.hardware.Robot;

import java.io.StringWriter;

import spark.Spark;
//import spark.template.velocity.VelocityTemplateEngine;

public class WebHost {
    Robot robot;

    /**
     * Hosts a site on robot's IP
     * All templates are stored in src/main/resources/public (for some reason, that's the default)
     */
    public WebHost() {
        Spark.staticFiles.location("/public");
//        Spark.staticFileLocation("/public");
        Spark.port(8813);
//        index();
//        Spark.init();
    }

    public WebHost(Robot robot){
        this.robot = robot;
        Spark.staticFiles.location("/public");
        Spark.port(8813);
    }


    public void index(){
        Spark.get("/", (req, res) -> {
            VelocityEngine engine = new VelocityEngine();
            engine.addProperty("resource.loader","class");
            engine.addProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            engine.init();
            
            StringWriter writer = new StringWriter();
            Template tplt = engine.getTemplate("public/test.vm");
            VelocityContext context = new VelocityContext();

            context.put("Lift_Position", 7);
            tplt.merge(context, writer);

            return writer.toString();
        });
    }

    public void close(){
        Spark.stop();
    }
}
