package editor.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Rectangle extends Shape implements Closed {
    
    public Rectangle(double x, double y, double w, double h, int lnw, Color ln, Color fill) {
        super(lnw, ln);
        initRectangle(x, y, w, h);
        fillColor = fill;
    }
    
    public Rectangle(double x, double y, double w, double h, int lnw, Color ln) {
        super(lnw, ln);
        initRectangle(x, y, w, h);
    }
    
    public Rectangle(double x, double y, double w, double h, int lnw) {
        super(lnw);
        initRectangle(x, y, w, h);
    }
    
    public Rectangle(double x, double y, double w, double h) {
        super();
        initRectangle(x, y, w, h);
    }
    
    public Rectangle(Point2D.Double p1, Point2D.Double p2, int lnw, Color ln, Color fill) {
        super(lnw, ln);
        initRectangle(p1, p2);
        fillColor = fill;
    }
    
    public Rectangle(Point2D.Double p1, Point2D.Double p2, int lnw, Color ln) {
        super(lnw, ln);
        initRectangle(p1, p2);
    }
    
    public Rectangle(Point2D.Double p1, Point2D.Double p2, int lnw) {
        super(lnw);
        initRectangle(p1, p2);
    }
    
    public Rectangle(Point2D.Double p1, Point2D.Double p2) {
        super();
        initRectangle(p1, p2);
    }
    
    public Rectangle(Rectangle r) {
        super(r.lnWidth, r.lnColor);
        initRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        fillColor = r.getFillColor();
    }
    
    public double getX() {
        return rectangle.getX();
    }
    
    public double getY() {
        return rectangle.getY();
    }

    public double getWidth() {
        return rectangle.getWidth();
    }

    public double getHeight() {
        return rectangle.getHeight();
    }
    
    public void setRectangle(double x, double y, double w, double h) {
        rectangle.setRect(x, y, w, h);
        setBoundBox((Rectangle2D.Double)rectangle.getBounds2D());
    }
    
    public void setRectangle(Rectangle2D.Double r) {
        rectangle.setRect(r);
        setBoundBox((Rectangle2D.Double)rectangle.getBounds2D());
    }
    
    public void setRectangle(Point2D.Double p1, Point2D.Double p2) {
        double x, y, w, h;
        x = p1.getX() < p2.getX()? p1.getX() : p2.getX();
        y = p1.getY() < p2.getY()? p1.getY() : p2.getY();
        w = p1.getX() < p2.getX()? p2.getX() - p1.getX() : p1.getX() - p2.getX();
        h = p1.getY() < p2.getY()? p2.getY() - p1.getY() : p1.getY() - p2.getY();
        setRectangle(x, y, w, h);
    }
    
    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fill) {
        fillColor = fill;
    }
    
    public void fill(Graphics2D g) {
        if (fillColor != null){
            g.setColor(fillColor);
            g.fill(rectangle);
        }
    }
    
    public void translate(double x, double y) {
        setBoundBox(new Rectangle2D.Double(getBoundBox().x + x, getBoundBox().y + y, getBoundBox().width, getBoundBox().height));
        rectangle = getBoundBox();
    }
    
    public String dimension(){
        return "<html>width: " + rectangle.width +
             "<br>height: " + rectangle.height + "</html>";
    }
    
    public Rectangle clone() {
        return new Rectangle(this);
    }
    
    public boolean equals(Shape s){
        if (!(s instanceof Rectangle)) return false;
        Rectangle r = (Rectangle)s;
        return rectangle.equals(r.rectangle) && ((fillColor == null && r.fillColor == null) || fillColor.equals(r.fillColor))
                && super.equals(s);
    }
    
    public String toString() {
        return "RECTANGLE: " + getX() + " " + getY() + " " + getWidth() + " " + getHeight()
               + " " + lnWidth + " " + lnColor + " " + fillColor;
    }
    
    public void paint(Graphics2D g) {
        g.setColor(lnColor);
        g.setStroke(new BasicStroke(lnWidth));
        g.draw(rectangle);
        fill(g);
    }
    
    private void initRectangle(double x, double y, double w, double h) {
        rectangle = new Rectangle2D.Double();
        setRectangle(x, y, w, h);
    }
    
    private void initRectangle(Point2D.Double p1, Point2D.Double p2) {
        rectangle = new Rectangle2D.Double();
        setRectangle(p1, p2);
    }
    
    private Rectangle2D.Double rectangle;
    private Color fillColor;
}
