package editor.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Shape implements Cloneable{
    public static final int DEFAULTLNWIDTH = 2;
    public static final Color DEFAULTLNCOLOR = Color.BLACK;
    public static final Color DEFAULTBOUNDBOXCOLOR = Color.CYAN;
    public static final int DEFAULTBOUNDBOXLINEWIDTH = 3;

    public Shape(int w, Color ln){
        lnWidth = w;
        lnColor = ln;
    }
    
    public Shape(int w) {
        this(w, DEFAULTLNCOLOR);
    }

    public Shape() {
        this(DEFAULTLNWIDTH);
    }
    
    public int getLnWidth() {
        return lnWidth;
    }

    public void setLnWidth(int w) {
        lnWidth = w;
    }

    public Color getLnColor() {
        return lnColor;
    }

    public void setLnColor(Color ln) {
        lnColor = ln;
    }
    
    public boolean contains(Point2D.Double p) {
        if (boundBox == null) return false;
        return boundBox.contains(p);
    }
    
    public boolean contains(double x, double y) {
        return contains(new Point2D.Double(x, y));
    }
    
    public void translate(double x, double y) {
        if (boundBox == null) return;
        boundBox.setRect(boundBox.x + x, boundBox.y + y, boundBox.width, boundBox.height);
    }
    
    public void paintBoundBox(Graphics2D g) {
        if (boundBox == null) return;
        g.setColor(DEFAULTBOUNDBOXCOLOR);
        g.setStroke(new BasicStroke(DEFAULTBOUNDBOXLINEWIDTH));
        g.draw(boundBox);
    }
    
    public boolean equals(Shape s){
        return boundBox.equals(s.boundBox) && lnWidth == s.lnWidth && lnColor.equals(s.lnColor);
    }
    
    public abstract String dimension();
    
    public abstract Shape clone();
    
    public abstract void paint(Graphics2D g);
    
    protected Rectangle2D.Double getBoundBox() {
        return boundBox;
    }
    
    protected void setBoundBox(Rectangle2D.Double r) {
        boundBox = r;
        if (!(this instanceof Closed)){
            boundBox.x -= 2;
            boundBox.y -= 2;
            boundBox.width += 4;
            boundBox.height += 4;
        }
    }
    
    protected Rectangle2D.Double boundBox;
    protected int lnWidth;
    protected Color lnColor;
}
