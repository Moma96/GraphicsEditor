package editor;

import editor.shape.*;
import editor.memory.CanvasMemory;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class Coordinator {
    public static final Tool DEFAULTTOOL = Tool.SELECT;
    
    public enum MouseState { PRESSED, RELEASED, CLICKED, DOUBLECLICKED, DRAGGED }
    public enum Tool { SELECT, ERASE, FILL, LINE, RECTANGLE, PATH, POLYGON }
    
    public Coordinator(Canvas c, Editor e) {
        canvas = c;
        editor = e;
        mem = new CanvasMemory(canvas);
    }
    
    public void memAdd(Shape s){
        mem.saveBefore(null);
        canvas.add(s);
        mem.saveAfter(s);
    }
    
    public void memSetBackground(){
        mem.saveBefore(canvas.getBackground());
        canvas.setBackground(fillColor != null? fillColor : canvas.DEFAULTBACKGROUND);
        mem.saveAfter(canvas.getBackground());
    }
    
    public void setTool(Tool t){
        tool = t;
        if (t == Tool.ERASE){
            erase(null, MouseState.RELEASED);
            canvas.repaint();
        }else if (t == Tool.FILL)
            canvas.currShape = null;
    }

    public void setLnColor(Color ln) {
        lnColor = ln;
        if (canvas.currShape != null){
            mem.saveBefore(canvas.currShape);
            canvas.currShape.setLnColor(ln);
            mem.saveAfter(canvas.currShape);
            canvas.repaint();
            editor.setCurrLnColor();
        }
    }

    public void setFillColor(Color fill) {
        fillColor = fill;
    }
    
    public void setColor(Color c) {
        if (c == null) setDefaultColor();
        else{
            setLnColor(c);
            setFillColor(c);
        }
    }
    
    public void setDefaultColor() {
        setLnColor(Shape.DEFAULTLNCOLOR);
        setFillColor(null);
    }
    
    public void disableFillColor() {
        fillColor = null;
    }
    
    public void setWidth(int w) {
        width = w;
        if (canvas.currShape != null){
            mem.saveBefore(canvas.currShape);
            canvas.currShape.setLnWidth(w);
            mem.saveAfter(canvas.currShape);
            canvas.repaint();
        }
    }
    
    public boolean checkFinished(){
        if (canvas.currShape != null && canvas.currShape instanceof Path){
            Path p = (Path)canvas.currShape;
            return p.finished();
        }
        return true;
    }
    
    public void canvasMousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (!checkFinished()) return;
        if (tool != null) switch (tool) {
            case SELECT:
                select(e, MouseState.PRESSED);
                break;
            case ERASE:
                erase(e, MouseState.PRESSED);
                break;
            case FILL:
                fill(true, e, MouseState.PRESSED);
                break;
            case LINE:
                line(e, MouseState.PRESSED);
                break;
            case RECTANGLE:
                rectangle(e, MouseState.PRESSED);
                break;
        }
        canvas.repaint();
    }

    public void canvasMouseReleased(MouseEvent e) {
        if (tool != null) switch (tool) {
            case SELECT:
                select(e, MouseState.RELEASED);
                break;
            case ERASE:
                erase(e, MouseState.RELEASED);
                break;
            case FILL:
                fill(true, e, MouseState.RELEASED);
                break;
            case LINE:
                line(e, MouseState.RELEASED);
                break;
            case RECTANGLE:
                rectangle(e, MouseState.RELEASED);
                break;
        }
        canvas.repaint();
    }
    
    public void canvasMouseDragged(MouseEvent e) {
        if (tool != null) switch (tool) {
            case SELECT:
                select(e, MouseState.DRAGGED);
                break;
            case LINE:
                line(e, MouseState.DRAGGED);
                break;
            case RECTANGLE:
                rectangle(e, MouseState.DRAGGED);
                break;
        }
        canvas.repaint();
    }
    
    public void canvasMouseClicked(MouseEvent e) {
        if (tool == Tool.PATH || tool == Tool.POLYGON)
            pathPolygon(e, MouseState.CLICKED);
    }
    
    public void canvasMouseDoubleClicked(MouseEvent e) {
        if (tool == Tool.PATH || tool == Tool.POLYGON)
            pathPolygon(e, MouseState.DOUBLECLICKED);
    }
    
    public void select(MouseEvent e, MouseState s) {
        if (s == MouseState.PRESSED){
            canvas.currShape = canvas.getFirstShape(e.getX(), e.getY());
            if (canvas.currShape != null){
                editor.setCurrLnColor();
                mem.saveBefore(canvas.currShape);
                canvas.moveToFront(canvas.currShape);
            }
        }
        else if (s == MouseState.DRAGGED){
            if (canvas.currShape != null){
                canvas.currShape.translate(e.getX() - x, e.getY() - y);
                x = e.getX();
                y = e.getY();
            }
        }
        else if (s == MouseState.RELEASED){
            if (canvas.currShape != null)
                mem.saveAfter(canvas.currShape);
        }
    }
    
    public void erase(MouseEvent e, MouseState s) {
        if (s == MouseState.PRESSED)
            canvas.currShape = canvas.getFirstShape(e.getX(), e.getY());
        else if (s == MouseState.RELEASED){
            if (canvas.currShape != null){
                mem.saveBefore(canvas.currShape);
                canvas.remove(canvas.currShape);
                canvas.currShape = null;
                mem.saveAfter(null);
            }
        }
    }
    
    public void fill(boolean background, MouseEvent e, MouseState s) {
        if (s == MouseState.PRESSED){
            canvas.currShape = canvas.getFirstClosed(e.getX(), e.getY());
            if (canvas.currShape != null){
                mem.saveBefore(canvas.currShape);
                canvas.moveToFront(canvas.currShape);
            }
        }
        else if (s == MouseState.RELEASED){
            if (canvas.currShape == null){
                if (background)
                    memSetBackground();
            }
            else if (canvas.currShape instanceof Closed){
                Closed c = (Closed)canvas.currShape;
                c.setFillColor(fillColor);
                mem.saveAfter(canvas.currShape);
                canvas.currShape = null;
            }
        }
    }
    
    public void line(MouseEvent e, MouseState s) {
        if (s != null)switch (s) {
            case PRESSED:
                canvas.currShape = new Line(x, y, e.getX(), e.getY(), width, lnColor);
                editor.setCurrLnColor();
                break;
            case DRAGGED:
                Line l = (Line)canvas.currShape;
                l.setLine(x, y, e.getX(), e.getY());
                break;
            case RELEASED:
                memAdd(canvas.currShape);
                break;
            default:
                break;
        }
    }
    
    public void rectangle(MouseEvent e, MouseState s) {
        if (s != null)switch (s) {
            case PRESSED:
                canvas.currShape = new Rectangle(new Point2D.Double(x, y),
                                   new Point2D.Double(e.getX(), e.getY()),
                                   width, lnColor);
                editor.setCurrLnColor();
                break;
            case DRAGGED:
                Rectangle r = (Rectangle)canvas.currShape;
                r.setRectangle(new Point2D.Double(x, y),
                               new Point2D.Double(e.getX(), e.getY()));
                break;
            case RELEASED:
                memAdd(canvas.currShape);
                break;
            default:
                break;
        }
    }
    
    public void pathPolygon(MouseEvent e, MouseState s) {
        if (s == MouseState.CLICKED){
            if (checkFinished()){
                if (tool == Tool.PATH)
                    canvas.currShape = new Path(new Point2D.Double(x, y), width, lnColor);
                else canvas.currShape = new Polygon(new Point2D.Double(x, y), width, lnColor);
                editor.setCurrLnColor();
                editor.disableButtons();
            }else{
                if (canvas.currShape instanceof Path){
                    Path p = (Path)canvas.currShape;
                    p.add(e.getX(), e.getY());
                }
            }
        }
        else if (s == MouseState.DOUBLECLICKED){
            Path p = (Path)canvas.currShape;
            p.finish();
            memAdd(canvas.currShape);
            editor.enableButtons();
        }
    }
    
    CanvasMemory mem;
    private final Canvas canvas;
    private final Editor editor;
    private Tool tool = DEFAULTTOOL;
    private int width = Shape.DEFAULTLNWIDTH;
    private Color lnColor = Shape.DEFAULTLNCOLOR;
    private Color fillColor;
    private int x, y;
}
