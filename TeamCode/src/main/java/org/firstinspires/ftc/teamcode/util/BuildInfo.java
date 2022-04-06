package org.firstinspires.ftc.teamcode.util;

import com.google.gson.JsonObject;

import java.io.File;

public class BuildInfo
{
    public final String buildDate;
    public final String buildBranch;
    public final String buildCommit;
    public final String buildOrigin;
    
    public BuildInfo(File f)
    {
        JsonObject obj = Configuration.readJson(f);
        buildDate = obj.get("buildDate").getAsString();
        buildBranch = obj.get("branch").getAsString();
        buildCommit = obj.get("commit").getAsString();
        buildOrigin = obj.get("origin").getAsString();
    }
    
    public void logInfo()
    {
        Logger log = new Logger("Build Info");
        log.i("---------------------------");
        log.i("Project built on %s", buildDate);
        log.i("Branch %s, commit %s", buildBranch, buildCommit);
        log.i("on remote %s", buildOrigin);
        log.i("---------------------------");
    }
}
