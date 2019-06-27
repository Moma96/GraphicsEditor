package editor;

import editor.exceptions.BadFormat;
import editor.shape.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JPanel;

public class Canvas extends JPanel {
    public static final int DEFAULTWIDTH = 1100;
    public static final int DEFAULTHEIGHT = 600;
    public static final Color DEFAULTBACKGROUND = Color.WHITE;
    
    public Canvas(){
        shapes = new LinkedList();
        setPreferredSize(new Dimension(DEFAULTWIDTH, DEFAULTHEIGHT));
        setBackground(DEFAULTBACKGROUND);
    }
    
    public Canvas add(Shape s) {
        shapes.offerFirst(s);
        return this;
    }
    
    public Canvas remove(Shape s) {
        shapes.remove(s);
        return this;
    }
    
    public Canvas add(String s) throws BadFormat, NumberFormatException {
        String[] frags = s.split(" ");
        switch (frags[0]) {
            case "LINE:":
                return add(new Line(Double.parseDouble(frags[1]), Double.parseDouble(frags[2]),
                        Double.parseDouble(frags[3]), Double.parseDouble(frags[4]),
                        Integer.parseInt(frags[5]), stringToColor(frags[6])));
            case "RECTANGLE:":
                return add(new Rectangle(Double.parseDouble(frags[1]), Double.parseDouble(frags[2]),
                        Double.parseDouble(frags[3]), Double.parseDouble(frags[4]),
                        Integer.parseInt(frags[5]), stringToColor(frags[6]), stringToColor(frags[7])));
            case "PATH:":{
                Point2D.Double[] points = new Point2D.Double[(frags.length - 3)/2];
                for (int i = 1; i < frags.length - 2; i += 2)
                    points[i/2] = new Point2D.Double(Double.parseDouble(frags[i]),
                            Double.parseDouble(frags[i + 1]));
                return add(new Path(points, Integer.parseInt(frags[frags.length - 2]),
                        stringToColor(frags[frags.length - 1])));
            }
            case "POLYGON:":{
                Point2D.Double[] points = new Point2D.Double[(frags.length - 4)/2];
                for (int i = 1; i < frags.length - 3; i += 2)
                    points[i/2] = new Point2D.Double(Double.parseDouble(frags[i]),
                            Double.parseDouble(frags[i + 1]));
                return add(new Polygon(points, Integer.parseInt(frags[frags.length - 3]),
                        stringToColor(frags[frags.length - 2]),
                        stringToColor(frags[frags.length - 1])));
            }
            default:
                throw new BadFormat();
        }
    }
    
    public static Color stringToColor(String s) throws BadFormat{
        if (s.equals("null")) return null;
        if (!s.matches("^java.awt.Color\\[r=\\d+,g=\\d+,b=\\d+\\]$")) throw new BadFormat();
        String[] frags = s.split("[,=\\]]");
        return new Color(Integer.parseInt(frags[1]), Integer.parseInt(frags[3]), Integer.parseInt(frags[5]));
    }
    
    public LinkedList getShapes() {
        return shapes;
    }
    
    public void moveToFront(Shape s) {
        remove(s).add(s);
    }
    
    public void moveToFront(int i) {
        moveToFront(shapes.get(i));
    }
    
    public void moveTo(Shape s, int i) {
        remove(s).shapes.add(i, s);
    }
    
    public void moveTo(int old, int nw) {
        moveTo(shapes.get(old), nw);
    }
    
    public Shape getFirstShape(int x, int y) {
        for (Shape s : shapes)
            if (s.contains(x, y)){
                return s;
            }
        return null;
    }
    
    public Shape getFirstClosed(int x, int y) {
        for (Shape s : shapes)
            if (s.contains(x, y) && s instanceof Closed){
                return s;
            }
        return null;
    }
    
    public void clear() {
        currShape = null;
        shapes = new LinkedList();
        currFile = null;
        setBackground(DEFAULTBACKGROUND);
        repaint();
    }
    
    public boolean save() {
        if (currFile == null) return false;
        saveAs(currFile);
        return true;
    }
    
    public void saveAs(String f) {
        try{
            PrintWriter wr = new PrintWriter(f, "UTF-8");
            wr.println(getBackground());
            Iterator<Shape> i = shapes.descendingIterator();
            while(i.hasNext())
                wr.println(i.next());
            wr.close();
        } catch (IOException e) { System.out.println(e);}
        currFile = f;
    }
    
    public void load(String f) throws BadFormat {
        clear();
        try {
            Scanner sr = new Scanner(new FileReader(f));
            String background = sr.nextLine();
            setBackground(stringToColor(background));
            while (sr.hasNextLine()) {
                String line = sr.nextLine();
                add(line);
            }
            sr.close();
        }catch(FileNotFoundException e){ System.out.println(e);}
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Iterator<Shape> i = shapes.descendingIterator();
        while(i.hasNext())
            i.next().paint((Graphics2D)g);
        if (currShape != null){
            if (currShape.getLnWidth() > Shape.DEFAULTBOUNDBOXLINEWIDTH){
                currShape.paint((Graphics2D)g);
                currShape.paintBoundBox((Graphics2D)g);
            }else{
                currShape.paintBoundBox((Graphics2D)g);
                currShape.paint((Graphics2D)g);
            }
        }
    }
    
    Shape currShape;
    LinkedList<Shape> shapes;
    private String currFile;
}
