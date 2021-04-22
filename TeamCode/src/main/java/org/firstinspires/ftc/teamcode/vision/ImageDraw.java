package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.util.Range;

import org.opencv.core.Scalar;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ImageDraw
{
    
    private static abstract class Operation
    {
        protected abstract void write(ByteBuffer buf);
    }
    
    public static final class Color
    {
        public final int color;
        
        public Color(int argb) { this.color = argb; }
        
        public Color(int r, int g, int b, int a) { this.color = (a << 24) | (b << 16) | (g << 8) | r; }
        
        public Color(int r, int g, int b) { this(r, g, b, 0xff); }
        
        public int r() { return (color >> 16) & 0xff; }
        
        public int g() { return (color >> 8) & 0xff; }
        
        public int b() { return color & 0xff; }
        
        public int a() { return (color >> 24) & 0xff; }
        
        public void write(ByteBuffer buf)
        {
            buf.putInt(color);
        }
        
        public static Color fromRGB(int rgb) { return new Color(rgb | 0xff000000); }
        
        public static Color fromScalar(Scalar s)
        {
            if (s.val.length == 4)
                return new Color((int) s.val[0], (int) s.val[1], (int) s.val[2], (int) s.val[3]);
            else if (s.val.length == 3)
                return new Color((int) s.val[0], (int) s.val[1], (int) s.val[2]);
            else if (s.val.length == 1)
                return new Color((int) s.val[0], (int) s.val[0], (int) s.val[0]);
            else throw new IllegalArgumentException("Invalid scalar dimension: " + s.val.length);
        }
    }
    
    public static final Color RED = new Color(0xcc, 0x00, 0x00);
    public static final Color GREEN = new Color(0x00, 0xcc, 0x00);
    public static final Color BLUE = new Color(0x40, 0x40, 0xff);
    public static final Color YELLOW = new Color(0xee, 0xcc, 0x00);
    public static final Color CYAN = new Color(0x40, 0xee, 0xcc);
    public static final Color MAGENTA = new Color(0xcc, 0x40, 0xee);
    public static final Color WHITE = new Color(0xff, 0xff, 0xff);
    public static final Color GRAY = new Color(0x7f, 0x7f, 0x7f);
    public static final Color BLACK = new Color(0x00, 0x00, 0x00);
    public static final Color NOCOLOR = new Color(0x00, 0x00, 0x00, 0x00);
    
    public static final int LINE_JOIN_SHARP = 0;
    public static final int LINE_JOIN_CURVE = 1;
    
    public static final int ANCHOR_H_LEFT = 0;
    public static final int ANCHOR_H_HCENTER = 1;
    public static final int ANCHOR_H_RIGHT = 2;
    
    public static final int ANCHOR_H_ASCEND = 0;
    public static final int ANCHOR_H_TOP = 1;
    public static final int ANCHOR_H_VCENTER = 2;
    public static final int ANCHOR_H_BASELINE = 3;
    public static final int ANCHOR_H_BOTTOM = 4;
    public static final int ANCHOR_H_DESCENDER = 5;
    
    public static final int ANCHOR_V_LEFT = 0;
    public static final int ANCHOR_V_BASELINE = 1;
    public static final int ANCHOR_V_HCENTER = 2;
    public static final int ANCHOR_V_RIGHT = 3;
    
    public static final int ANCHOR_V_TOP = 0;
    public static final int ANCHOR_V_VCENTER = 1;
    public static final int ANCHOR_V_BOTTOM = 2;
    
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    public static final int DIRECTION_TTB = 2;
    
    public static final class Point
    {
        public final int x;
        public final int y;
        
        public Point(int x, int y)
        {
            x = Range.clip(x, -32768, 32767);
            y = Range.clip(y, -32768, 32767);
            this.x = x;
            this.y = y;
        }
        
        protected void write(ByteBuffer buf)
        {
            buf.putShort((short) x);
            buf.putShort((short) y);
        }
        
        public static Point fromCvPoint(org.opencv.core.Point pt)
        {
            return new Point((int) pt.x, (int) pt.y);
        }
        
        public static Point[] fromContour(org.opencv.core.Point[] contourPoints)
        {
            Point[] points = new Point[contourPoints.length];
            for (int i = 0; i < points.length; i++)
            {
                points[i] = fromCvPoint(contourPoints[i]);
            }
            return points;
        }
    }
    
    private ArrayList<Operation> ops;
    
    public ImageDraw()
    {
        ops = new ArrayList<>();
    }
    
    public void draw(Operation op)
    {
        ops.add(op);
    }
    
    public void clear()
    {
        ops.clear();
    }
    
    public void write(ByteBuffer buf)
    {
        for (Operation op : ops)
        {
            op.write(buf);
        }
    }
    
    public static final class Arc extends Operation
    {
        private final Point top_left, bottom_right;
        private final double start, end;
        private final Color stroke;
        private final int width;
        
        public Arc(Point top_left, Point bottom_right, double start, double end, Color stroke, int width)
        {
            width = Range.clip(width, 0, 255);
            this.top_left = top_left;
            this.bottom_right = bottom_right;
            this.start = start;
            this.end = end;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            this.width = width;
        }
        
        @Override
        protected void write(ByteBuffer buf)
        {
            buf.put((byte) 0x00);
            top_left.write(buf);
            bottom_right.write(buf);
            buf.putFloat((float) start);
            buf.putFloat((float) end);
            stroke.write(buf);
            buf.put((byte) width);
        }
    }
    
    public static final class Chord extends Operation
    {
        private final Point top_left, bottom_right;
        private final double start, end;
        private final Color stroke, fill;
        private final int width;
        
        public Chord(Point top_left, Point bottom_right, double start, double end, Color stroke, Color fill, int width)
        {
            width = Range.clip(width, 0, 255);
            this.top_left = top_left;
            this.bottom_right = bottom_right;
            this.start = start;
            this.end = end;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.width = width;
        }
        
        @Override
        protected void write(ByteBuffer buf)
        {
            buf.put((byte) 0x01);
            top_left.write(buf);
            bottom_right.write(buf);
            buf.putFloat((float) start);
            buf.putFloat((float) end);
            stroke.write(buf);
            fill.write(buf);
            buf.put((byte) width);
        }
    }
    
    public static final class Ellipse extends Operation
    {
        private final Point top_left, bottom_right;
        private final Color stroke, fill;
        private final int width;
        
        public Ellipse(Point top_left, Point bottom_right, Color stroke, Color fill, int width)
        {
            width = Range.clip(width, 0, 255);
            this.top_left = top_left;
            this.bottom_right = bottom_right;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.width = width;
        }
        
        @Override
        protected void write(ByteBuffer buf)
        {
            buf.put((byte) 0x02);
            top_left.write(buf);
            bottom_right.write(buf);
            stroke.write(buf);
            fill.write(buf);
            buf.put((byte) width);
        }
    }
    
    public static final class Lines extends Operation
    {
        private final Point[] points;
        public final Color fill;
        public final int width;
        public final int join;
        
        public Lines(Color fill, int width, int join, Point... points)
        {
            if (points == null || points.length < 2)
                throw new IllegalArgumentException("Must have at least 2 points for lines");
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.width = Range.clip(width, 0, 255);
            this.join = Range.clip(join, 0, 1);
            this.points = points;
        }
        
        public Lines(Color fill, int width, Point... points)
        {
            this(fill, width, LINE_JOIN_SHARP, points);
        }
        
        @Override
        public void write(ByteBuffer buf)
        {
            buf.put((byte) 0x03);
            fill.write(buf);
            buf.put((byte) width);
            buf.put((byte) join);
            int pointsToSend = Math.min(points.length - 2, 65535);
            buf.putShort((short) pointsToSend);
            for (int i = 0; i < pointsToSend + 2; i++)
            {
                points[i].write(buf);
            }
        }
    }
    
    public static final class Pieslice extends Operation
    {
        private final Point top_left, bottom_right;
        private final double start, end;
        private final Color stroke, fill;
        private final int width;
        
        public Pieslice(Point top_left, Point bottom_right, double start, double end, Color stroke, Color fill, int width)
        {
            width = Range.clip(width, 0, 255);
            this.top_left = top_left;
            this.bottom_right = bottom_right;
            this.start = start;
            this.end = end;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.width = width;
        }
        
        @Override
        protected void write(ByteBuffer buf)
        {
            buf.put((byte) 0x04);
            top_left.write(buf);
            bottom_right.write(buf);
            buf.putFloat((float) start);
            buf.putFloat((float) end);
            stroke.write(buf);
            fill.write(buf);
            buf.put((byte) width);
        }
    }
    
    public static final class Pixel extends Operation
    {
        public final Point point;
        public final Color color;
        
        public Pixel(Point p, Color color)
        {
            this.point = p;
            if (color == null) this.color = NOCOLOR;
            else this.color = color;
        }
        
        @Override
        protected void write(ByteBuffer buf)
        {
            buf.put((byte) 0x05);
            point.write(buf);
            color.write(buf);
        }
    }
    
    public static final class Polygon extends Operation
    {
        private final Point[] points;
        public final Color fill;
        public final Color stroke;
        
        public Polygon(Color stroke, Color fill, Point... points)
        {
            if (points == null || points.length < 2)
                throw new IllegalArgumentException("Must have at least 2 points for lines");
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.points = points;
        }
        
        @Override
        public void write(ByteBuffer buf)
        {
            buf.put((byte) 0x06);
            fill.write(buf);
            stroke.write(buf);
            int pointsToSend = Math.min(points.length - 2, 65535);
            buf.putShort((short) pointsToSend);
            for (int i = 0; i < pointsToSend + 2; i++)
            {
                points[i].write(buf);
            }
        }
    }
    
    public static final class RegularPolygon extends Operation
    {
        public final Point origin;
        public final int radius;
        public final int sides;
        public final float rotation;
        public final Color fill;
        public final Color stroke;
        
        public RegularPolygon(Point origin, int radius, int sides, float rotation, Color stroke, Color fill)
        {
            this.origin = origin;
            this.radius = Range.clip(radius, 0, 65535);
            this.sides = Range.clip(sides, 0, 255);
            this.rotation = rotation;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
        }
        
        @Override
        public void write(ByteBuffer buf)
        {
            buf.put((byte) 0x07);
            origin.write(buf);
            buf.putShort((short) radius);
            buf.put((byte) sides);
            buf.putFloat(rotation);
            fill.write(buf);
            stroke.write(buf);
        }
    }
    
    public static final class Rectangle extends Operation
    {
        public final Point tl;
        public final Point br;
        public final Color fill;
        public final Color stroke;
        public final int width;
        
        public Rectangle(Point tl, Point br, Color stroke, Color fill, int width)
        {
            this.tl = tl;
            this.br = br;
            if (stroke == null) this.stroke = NOCOLOR;
            else this.stroke = stroke;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            this.width = Range.clip(width, 0, 255);
        }
        
        @Override
        public void write(ByteBuffer buf)
        {
            buf.put((byte) 0x08);
            tl.write(buf);
            br.write(buf);
            fill.write(buf);
            stroke.write(buf);
            buf.put((byte) width);
        }
    }
    
    public static final class Text extends Operation
    {
        public final Point point;
        public final Color fill;
        public final int anchor;
        public final int spacing;
        public final int align;
        public final int direction;
        public final int width;
        public final Color stroke_fill;
        public final Color background;
        public final String text;
        
        public Text(String text, Point origin, Color fill, Color stroke_fill, Color bg, int width, int h_anchor, int v_anchor, int align, int direction, int spacing)
        {
            this.text = text;
            this.point = origin;
            if (stroke_fill == null) this.stroke_fill = NOCOLOR;
            else this.stroke_fill = stroke_fill;
            if (fill == null) this.fill = NOCOLOR;
            else this.fill = fill;
            if (bg == null) this.background = NOCOLOR;
            else this.background = bg;
            this.width = Range.clip(width, 0, 255);
            this.align = Range.clip(align, 0, 2);
            this.direction = Range.clip(direction, 0, 2);
            if (this.direction == 2)
            {
                h_anchor = Range.clip(h_anchor, 0, 3);
                v_anchor = Range.clip(v_anchor, 0, 2);
            }
            else
            {
                h_anchor = Range.clip(h_anchor, 0, 2);
                v_anchor = Range.clip(v_anchor, 0, 5);
            }
            this.anchor = ((h_anchor << 4) | v_anchor) & 0xFF;
            this.spacing = Range.clip(spacing, 0, 65535);
        }
        
        @Override
        public void write(ByteBuffer buf)
        {
            buf.put((byte) 0x09);
            point.write(buf);
            byte[] data = text.getBytes(StandardCharsets.UTF_8);
            buf.putInt(data.length);
            for (byte b : data)
            {
                buf.put(b);
            }
            fill.write(buf);
            buf.put((byte) anchor);
            buf.putShort((short) spacing);
            buf.put((byte) align);
            buf.put((byte) direction);
            buf.put((byte) width);
            stroke_fill.write(buf);
            background.write(buf);
        }
    }
}
