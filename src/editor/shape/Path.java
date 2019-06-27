package editor.shape;

import static java.util.Arrays.deepEquals;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Path extends Shape {
    
    public Path(Point2D.Double[] points, int w, Color ln) {
        super(w, ln);
        initPath(points);
    }
    
    public Path(Point2D.Double[] points, int w) {
        super(w);
        initPath(points);
    }
    
    public Path(Point2D.Double[] points) {
        super();
        initPath(points);
    }
    
    public Path(Point2D.Double point, int w, Color ln) {
        super(w, ln);
        initPath(point);
    }
    
    public Path(Point2D.Double point, int w) {
        super(w);
        initPath(point);
    }
    
    public Path(Point2D.Double point) {
        super();
        initPath(point);
    }
    
    public Path(double x, double y, int w, Color ln) {
        super(w, ln);
        initPath(x, y);
    }
    
    public Path(double x, double y, int w) {
        super(w);
        initPath(x, y);
    }
    
    public Path(double x, double y) {
        super();
        initPath(x, y);
    }
    
    public Path(Path p) {
        super(p.lnWidth, p.lnColor);
        Point2D.Double[] npoints = new Point2D.Double[p.points.length];
        for (int i = 0; i < p.points.length; i++)
            npoints[i] = new Point2D.Double(p.points[i].x, p.points[i].y);
        initPath(npoints);
    }
    
    public Path add(Point2D.Double p) {
        if (!finished){
            path.lineTo(p.x, p.y);
            Point2D.Double[] npoints = new Point2D.Double[points.length + 1];
            for (int i = 0; i < points.length; i++) npoints[i] = points[i];
            npoints[points.length] = p;
            points = npoints;
            setBoundBox((Rectangle2D.Double)path.getBounds2D());
        }
        return this;
    }
    
    public void add(double x, double y) {
        add(new Point2D.Double(x, y));
    }
    
    public Path finish(){
        finished = true;
        return this;
    }
    
    public boolean finished(){
        return finished;
    }
    
    public void translate(double x, double y) {
        Point2D.Double[] npoints = points;
        for (Point2D.Double p : npoints){
            p.setLocation(p.getX() + x, p.getY() + y);
        }
        initPath(points);
    }
    
    public double getLength() {
        double sum = 0;
        for (int i = 1; i < points.length; i++)
            sum += Line.getLength(points[i - 1], points[i]);
        return sum;
    }
    
    public String dimension(){
        return "length: " + String.format("%.2f", getLength());
    }
    
    public Path clone() {
        return new Path(this);
    }
    
    public boolean equals(Shape s){
        if (!(s instanceof Path)) return false;
        Path p = (Path)s;
        if (!deepEquals(points, p.points)) return false;
        return super.equals(s);
    }
    
    public String toString() {
        StringBuilder s;
        if (this instanceof Polygon)
            s = new StringBuilder("POLYGON:");
        else s = new StringBuilder("PATH:");
        for (Point2D.Double p : points){
            s.append(" ").append(p.getX());
            s.append(" ").append(p.getY());
        }
        s.append(" ").append(lnWidth).append(" ").append(lnColor);
        return s.toString();
    }
    
    public void paint(Graphics2D g) {
        g.setColor(lnColor);
        g.setStroke(new BasicStroke(lnWidth));
        if (points.length == 1)
            g.draw(new Rectangle2D.Double(points[0].x - 0.5, points[0].y - 0.5, 1, 1));
        else
            g.draw(path);
    }
    
    private void initPath(Point2D.Double[] p) {
        path = new Path2D.Double();
        points = p;
        path.moveTo(p[0].getX(), p[0].getY());
        for (int i = 1; i < p.length; i++)
            path.lineTo(p[i].getX(), p[i].getY());
        setBoundBox((Rectangle2D.Double)path.getBounds2D());
        finish();
    }
    
    private void initPath(Point2D.Double p) {
        path = new Path2D.Double();
        points = new Point2D.Double[]{ p };
        path.moveTo(p.getX(), p.getY());
    }
    
    private void initPath(double x, double y) {
        initPath(new Point2D.Double(x, y));
    }
    
    protected Path2D.Double path;
    protected Point2D.Double[] points;
    protected boolean finished = false;
}
