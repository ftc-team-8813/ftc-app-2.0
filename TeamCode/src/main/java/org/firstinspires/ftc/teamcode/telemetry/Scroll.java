package org.firstinspires.ftc.teamcode.telemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class Scroll
{
    public static final int SCROLL_STOP = 1;
    public static final int SCROLL_WRAP = 2;
    
    private ArrayList<String> lines;
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
        if (visibleLines <= 0) throw new IllegalArgumentException("Must have at least one visible line");
        this.visibleLines = visibleLines;
    }
    
    public void addLine(String value)
    {
        lines.add(value);
        invalid = true;
    }
    
    public String removeLine(int index)
    {
        invalid = true;
        return lines.remove(index);
    }
    
    public String getLine(int index)
    {
        return lines.get(index);
    }
    
    public void setLine(int index, String value)
    {
        invalid = true;
        lines.set(index, value);
    }
    
    public void insertLine(int index, String value)
    {
        invalid = true;
        lines.add(index, value);
    }
    
    public int getNumLines()
    {
        return lines.size();
    }
    
    public int setScrollPos(int pos)
    {
        if (pos == selected) return pos;
        
        int len = lines.size();
        if (pos < 0)
        {
            if (scrollMode == SCROLL_STOP) pos = 0;
            else pos += len;
        }
        else if (pos >= len)
        {
            if (scrollMode == SCROLL_STOP) pos = len-1;
            else pos -= len;
        }
        
        if (pos < visStart) visStart = pos;
        else if (pos >= visStart + visibleLines) visStart = pos - visibleLines + 1;
        
        selected = pos;
        invalid = true;
        
        return pos;
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
                visLineData[i].setValue(lines.get(i));
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
}
