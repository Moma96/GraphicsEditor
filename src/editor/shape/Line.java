package editor.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line extends Shape {
    
    public static double getLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    public static double getLength(Point2D.Double p1, Point2D.Double p2) {
        return getLength(p1.x, p1.y, p2.x, p2.y);
    }
    
    public Line(double x1, double y1, double x2, double y2, int lnw, Color ln) {
        super(lnw, ln);
        initLine(x1, y1, x2, y2);
    }
    
    public Line(double x1, double y1, double x2, double y2, int lnw) {
        super(lnw);
        initLine(x1, y1, x2, y2);
    }
    
    public Line(double x1, double y1, double x2, double y2) {
        super();
        initLine(x1, y1, x2, y2);
    }
    
    public Line(Point2D.Double p1, Point2D.Double p2, int lnw, Color ln) {
        super(lnw, ln);
        initLine(p1, p2);
    }
    
    public Line(Point2D.Double p1, Point2D.Double p2, int lnw) {
        super(lnw);
        initLine(p1, p2);
    }
    
    public Line(Point2D.Double p1, Point2D.Double p2) {
        super();
        initLine(p1, p2);
    }
    
    public Line(Line l) {
        super(l.lnWidth, l.lnColor);
        initLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
    }
    
    public double getX1() {
        return line.getX1();
    }
    
    public double getY1() {
        return line.getY1();
    }

    public double getX2() {
        return line.getX2();
    }
    
    public double getY2() {
        return line.getY2();
    }
    
    public void setLine(double x1, double y1, double x2, double y2) {
        line.setLine(x1, y1, x2, y2);
        setBoundBox((Rectangle2D.Double)line.getBounds2D());
    }
    
    public void setLine(Point2D.Double p1, Point2D.Double p2) {
        line.setLine(p1, p2);
        setBoundBox((Rectangle2D.Double)line.getBounds2D());
    }
    
    public void translate(double x, double y) {
        line.setLine(line.getX1() + x, line.getY1() + y, line.getX2() + x, line.getY2() + y);
        super.translate(x, y);
    }
    
    public double getLength() {
        return getLength(line.x1, line.y1, line.x2, line.y2);
    }
    
    public String dimension(){
        return "length: " + String.format("%.2f", getLength());
    }
    
    public boolean equals(Shape s){
        if (!(s instanceof Line)) return false;
        Line l = (Line)s;
        return line.getX1() == l.getX1() && line.getY1() == l.getY1() &&
               line.getX2() == l.getX2() && line.getY2() == l.getY2() && super.equals(s);
    }
    
    public Line clone() {
        return new Line(this);
    }
    
    public String toString() {
        return "LINE: " + getX1() + " " + getY1() + " " + getX2() + " " + getY2()
               + " " + lnWidth + " " + lnColor;
    }
    
    public void paint(Graphics2D g) {
        g.setColor(lnColor);
        g.setStroke(new BasicStroke(lnWidth));
        g.draw(line);
    }
    
    private void initLine(double x1, double y1, double x2, double y2) {
        line = new Line2D.Double(x1, y1, x2, y2);
        setBoundBox((Rectangle2D.Double)line.getBounds2D());
    }
    
    private void initLine(Point2D.Double p1, Point2D.Double p2) {
        line = new Line2D.Double(p1, p2);
        setBoundBox((Rectangle2D.Double)line.getBounds2D());
    }
    
    private Line2D.Double line;
}
