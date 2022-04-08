package org.firstinspires.ftc.teamcode.util;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.util.websocket.Server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

/**
 * Simple logging utility
 */

public class Logger
{

    public static class Level
    {
        private Level()
        {
        }

        public static final int NONE = Integer.MIN_VALUE;
        public static final int FATAL = 0;
        public static final int ERROR = 1;
        public static final int WARN = 2;
        public static final int INFO = 3;
        public static final int DEBUG = 4;
        public static final int VERBOSE = 5;
        public static final int ALL = Integer.MAX_VALUE;
    }

    private static PrintWriter writer;
    private static File file;
    private static boolean open = false;
    private static long start;
    private static boolean started = false;
    private static int maxLevel = Level.ALL;
    private static ByteArrayOutputStream serverOutput;
    private static PrintWriter serverWriter;

    private String tag;

    @SuppressLint("SimpleDateFormat")
    public static String getTimestamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    /**
     * Initialize the logger to a default location ('logs/[date].log')
     *
     * @throws IOException If an I/O error occurs
     */
    public static void init() throws IOException
    {
        init(Storage.createFile("/logs/" + getTimestamp() + ".log"));
    }

    /**
     * Initialize the logger with a new file. Closes the previous log file if one is open.
     *
     * @param file The new file to write to
     * @throws IOException If an I/O error occurs
     */
    public static void init(File file) throws IOException
    {
        Storage.createDirs("/logs");
        close();
        Logger.file = file;
        started = false;
        file.getParentFile().mkdirs();
        writer = new PrintWriter(
                new OutputStreamWriter(
                        new BufferedOutputStream(
                                new FileOutputStream(file)), StandardCharsets.UTF_8));
    }

    /**
     * Close the log file. Any logging operations after this will produce a NullPointerException
     * until {@link #init(File)} is called again. Does not produce {@link IOException}s.
     */
    public static void close()
    {
        if (writer != null)
        {
            writer.close();
            writer = null;
        }
        if (serverOutput != null)
        {
            // closing a byte array output stream has no effect
            serverOutput = null;
            serverWriter = null;
        }
        if (file != null)
        {
            file = null;
        }
    }

    /**
     * Set the maximum logging level to print. The default is {@link Level#ALL ALL}.
     *
     * @param level The maximum log level
     */
    public static void setLevel(int level)
    {
        maxLevel = level;
    }

    public static void startTimer()
    {
        start = System.currentTimeMillis();
        started = true;
    }

    public static void serveLogs(Server server, int commandId)
    {
        serverOutput = new ByteArrayOutputStream();
        serverWriter = new PrintWriter(new OutputStreamWriter(serverOutput, StandardCharsets.UTF_8));
        server.registerProcessor(commandId, (cmd, payload, resp) -> {
            serverWriter.flush();
            if (serverOutput.size() > 0)
            {
                byte[] newData = serverOutput.toByteArray();
                resp.respond(ByteBuffer.wrap(newData));
                serverOutput.reset();
            }
        });
    }

    public Logger(String tag)
    {
        this.tag = tag;
    }

    public synchronized void log(int level, String fmt, Object... args)
    {
        if (writer == null)
        {
            try
            {
                init();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (level <= maxLevel)
        {
            String base = base(level);
            String line = base + String.format(fmt, args);
            if (writer != null) writer.println(line);
            if (serverWriter != null) serverWriter.println(line);
            RobotLog.dd(tag, String.format(fmt, args));
        }
    }

    public synchronized void log(int level, Throwable t)
    {
        if (writer == null)
        {
            try
            {
                init();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (level <= maxLevel)
        {
            String base = base(level);
            if (writer != null)
            {
                writer.print(base);
                t.printStackTrace(writer);
            }
            if (serverWriter != null)
            {
                serverWriter.print(base);
                t.printStackTrace(serverWriter);
            }
            RobotLog.ww(tag, t, "");
        }
    }

    public synchronized void v(String fmt, Object... args)
    {
        log(99, fmt, args);
    }

    public synchronized void d(String fmt, Object... args)
    {
        log(4, fmt, args);
    }

    public synchronized void i(String fmt, Object... args)
    {
        log(3, fmt, args);
    }

    public synchronized void w(String fmt, Object... args)
    {
        log(2, fmt, args);
    }

    public synchronized void e(String fmt, Object... args)
    {
        log(1, fmt, args);
    }

    public synchronized void f(String fmt, Object... args)
    {
        log(0, fmt, args);
    }

    public synchronized void v(Throwable t) { log(99, t); }

    public synchronized void d(Throwable t) { log(4, t); }

    public synchronized void i(Throwable t) { log(3, t); }

    public synchronized void w(Throwable t) { log(2, t); }

    public synchronized void e(Throwable t) { log(1, t); }

    public synchronized void f(Throwable t) { log(0, t); }


    private String base(int level)
    {
        String lvl;
        if (level <= 0) lvl = "FATAL";
        else if (level == 1) lvl = "ERROR";
        else if (level == 2) lvl = "WARN";
        else if (level == 3) lvl = "INFO";
        else if (level == 4) lvl = "DEBUG";
        else lvl = "VERBOSE";
        Calendar c = Calendar.getInstance();
        //                     year  mo   dy  hour min  sec tg lv
        if (started)
        {
            double secs = (System.currentTimeMillis() - start) / 1000.0;
            return String.format(Locale.US, "%04d/%02d/%02d %02d:%02d:%02d [%6.3fs] %s/%s: ",
                    c.get(YEAR), c.get(MONTH) + 1, c.get(DAY_OF_MONTH), c.get(HOUR), c.get(MINUTE),
                    c.get(SECOND), secs, tag, lvl);
        }
        return String.format(Locale.US, "%04d/%02d/%02d %02d:%02d:%02d %s/%s: ",
                c.get(YEAR), c.get(MONTH) + 1, c.get(DAY_OF_MONTH), c.get(HOUR), c.get(MINUTE),
                c.get(SECOND), tag, lvl);
    }
}
