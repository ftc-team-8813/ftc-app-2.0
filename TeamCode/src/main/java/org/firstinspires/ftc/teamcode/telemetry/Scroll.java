package org.firstinspires.ftc.teamcode.telemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Time;

import java.util.ArrayList;

public class Scroll
{
    public static final int SCROLL_STOP = 1;
    public static final int SCROLL_WRAP = 2;
    
    private ArrayList<String> lines;
    private ArrayList<Object> metadata;
    private Telemetry.Line[] visLines;
    private Telemetry.Item[] visLineData;
    private final int visibleLines;
    private int visStart = 0;
    private int selected = 0;
    private boolean invalid = true;
    public int scrollMode = SCROLL_STOP;
    public String unselectedCaption = " ";
    public String selectedCaption = ">";
    
    public Scroll(int visibleLines)
    {
        if (visibleLines <= 0)
            throw new IllegalArgumentException("Must have at least one visible line");
        this.visibleLines = visibleLines;
        this.lines = new ArrayList<>();
        this.metadata = new ArrayList<>();
    }
    
    public void setScrollMode(int scrollMode)
    {
        this.scrollMode = scrollMode;
    }
    
    public void addLine(String value, Object meta)
    {
        lines.add(value);
        metadata.add(meta);
        invalid = true;
    }
    
    public void addLine(String value) { addLine(value, null); }
    
    public String removeLine(int index)
    {
        invalid = true;
        metadata.remove(index);
        String line = lines.remove(index);
        if (getScrollPos() >= lines.size())
            updateScrollPos(getScrollPos()); // re-validate scroll position
        return line;
    }
    
    public String getLine(int index) { return lines.get(index); }
    
    public Object getLineMeta(int index) { return metadata.get(index); }
    
    public void setLine(int index, String value)
    {
        invalid = true;
        lines.set(index, value);
    }
    
    public void setLineMeta(int index, Object value)
    {
        metadata.set(index, value);
    }
    
    public void insertLine(int index, String value, Object meta)
    {
        invalid = true;
        metadata.add(index, meta);
        lines.add(index, value);
    }
    
    public void insertLine(int index, String value) { insertLine(index, value, null); }
    
    public int size()
    {
        return lines.size();
    }
    
    public int setScrollPos(int pos)
    {
        if (pos == selected) return pos;
        return updateScrollPos(pos);
    }
    
    private int updateScrollPos(int pos)
    {
        int len = lines.size();
        if (pos < 0)
        {
            if (scrollMode == SCROLL_STOP) pos = 0;
            else pos += len;
        }
        else if (pos >= len)
        {
            if (scrollMode == SCROLL_STOP) pos = len - 1;
            else pos -= len;
        }
        
        if (pos < visStart) visStart = pos;
        else if (pos >= visStart + visibleLines) visStart = pos - visibleLines + 1;
        
        selected = pos;
        invalid = true;
        
        return pos;
    }
    
    public String getSelectedLine()
    {
        return getLine(getScrollPos());
    }
    
    public Object getSelectedMeta()
    {
        return getLineMeta(getScrollPos());
    }
    
    public int getScrollPos()
    {
        return selected;
    }
    
    public void invalidate()
    {
        invalid = true;
    }
    
    public void render(Telemetry telemetry)
    {
        if (visLines == null)
        {
            visLines = new Telemetry.Line[visibleLines];
            visLineData = new Telemetry.Item[visibleLines];
            for (int i = 0; i < visLines.length; i++)
            {
                visLines[i] = telemetry.addLine();
                visLineData[i] = visLines[i].addData("", "");
            }
            invalid = true;
        }
        if (invalid)
        {
            for (int i = 0; i < visLines.length; i++)
            {
                int x = i + visStart;
                visLineData[i].setCaption((x == selected) ? selectedCaption : unselectedCaption);
                if (x < lines.size()) visLineData[i].setValue(lines.get(x));
                else visLineData[i].setValue("");
            }
            invalid = false;
        }
    }
    
    public void clear(Telemetry telemetry)
    {
        for (Telemetry.Line line : visLines)
            telemetry.removeLine(line);
        visLines = null;
    }
    
    private static final double preHoldTime = 0.5;
    private static final double holdTime = 0.05;
    
    private double holdStart = 0;
    private boolean holdTick = false;
    private int holdDir = 0;
    
    public void press(int dir)
    {
        holdStart = Time.now();
        holdDir = dir;
        holdTick = false;
        
        setScrollPos(selected + dir);
    }
    
    public boolean hold()
    {
        if (holdTick)
        {
            if (Time.now() - holdStart > holdTime)
            {
                setScrollPos(selected + holdDir);
                holdStart = Time.now();
                return true;
            }
        }
        else
        {
            if (Time.now() - holdStart > preHoldTime)
            {
                setScrollPos(selected + holdDir);
                holdStart = Time.now();
                holdTick = true;
                return true;
            }
        }
        return false;
    }
}
