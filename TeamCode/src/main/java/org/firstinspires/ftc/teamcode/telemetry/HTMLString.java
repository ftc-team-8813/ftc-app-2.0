package org.firstinspires.ftc.teamcode.telemetry;

import java.util.ArrayList;

public class HTMLString
{
    public String tag;
    public String attributes;
    private ArrayList<Object> elements;
    
    public HTMLString(String tag)
    {
        this.tag = tag;
        elements = new ArrayList<>();
    }
    
    public HTMLString(String tag, String attributes, String value)
    {
        this(tag);
        this.attributes = attributes;
        addElement(value);
    }
    
    public HTMLString addElement(String value)
    {
        elements.add(value);
        return this;
    }
    
    public HTMLString addElement(HTMLString tag)
    {
        elements.add(tag);
        return this;
    }
    
    public HTMLString setAttributes(String attrs)
    {
        this.attributes = attrs;
        return this;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        if (tag != null)
        {
            builder.append("<");
            builder.append(tag);
            if (attributes != null)
            {
                builder.append(" ");
                builder.append(attributes);
            }
            builder.append(">");
        }
        for (Object element : elements)
        {
            builder.append(element); // uses toString to get string representations of tags
        }
        if (tag != null)
        {
            builder.append("</");
            builder.append(tag);
            builder.append(">");
        }
        return builder.toString();
    }
}
