package editor.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Polygon extends Path implements Closed {

    public Polygon(Point2D.Double[] points, int w, Color ln, Color fill) {
        super(points, w, ln);
        fillColor = fill;
    }
    
    public Polygon(Point2D.Double[] points, int w, Color ln) {
        super(points, w, ln);
    }

    public Polygon(Point2D.Double[] points, int w) {
        super(points, w);
    }

    public Polygon(Point2D.Double[] points) {
        super(points);
    }
    
    public Polygon(Point2D.Double point, int w, Color ln, Color fill) {
        super(point, w, ln);
        fillColor = fill;
    }
    
    public Polygon(Point2D.Double point, int w, Color ln) {
        super(point, w, ln);
    }

    public Polygon(Point2D.Double point, int w) {
        super(point, w);
    }

    public Polygon(Point2D.Double point) {
        super(point);
    }

    public Polygon(double x, double y, int w, Color ln) {
        super(x, y, w, ln);
    }

    public Polygon(double x, double y, int w) {
        super(x, y, w);
    }

    public Polygon(double x, double y) {
        super(x, y);
    }
    
    public Polygon(Polygon p) {
        super(p);
        fillColor = p.getFillColor();
    }
    
    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fill) {
        fillColor = fill;
    }
    
    public Polygon finish() {
        super.finish();
        path.closePath();
        return this;
    }
    
    public void fill(Graphics2D g) {
        if (fillColor != null && finished){
            g.setColor(fillColor);
            g.fill(path);
        }
    }
    
    public void translate(double x, double y) {
        super.translate(x, y);
        finish();
    }
    
    public double getLength() {
        double sum = super.getLength();
        if (finished())
            sum += Line.getLength(points[0], points[points.length-1]);
        return sum;
    }
    
    public Polygon clone() {
        return new Polygon(this);
    }
    
    public boolean equals(Shape s){
        if (!(s instanceof Polygon)) return false;
        Polygon p = (Polygon)s;
        return ((fillColor == null && p.fillColor == null) || fillColor.equals(p.fillColor)) && super.equals(s);
    }
    
    public String toString() {
        return super.toString() + " " + fillColor;
    }
    
    public void paint(Graphics2D g) {
        super.paint(g);
        fill(g);
    }
    
    private Color fillColor;
}
