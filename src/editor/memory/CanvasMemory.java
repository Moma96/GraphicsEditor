package editor.memory;

import editor.Canvas;
import editor.shape.Shape;
import java.awt.Color;

public class CanvasMemory extends Memory {

    public CanvasMemory(Canvas c) {
        this.c = c;
    }
    
    protected Object clone(Object o) {
        if (o instanceof Shape){
            Shape s = (Shape)o;
            return new ShapeElem(s.clone(), c.getShapes().indexOf(s));
        }
        else if (o instanceof Color)
            return o;
        return null;
    }
    
    protected synchronized void replaceUndo() {
        if (mem.get(curr).aft == null){
            ShapeElem sbef = (ShapeElem)mem.get(curr).bef;
            c.getShapes().add(sbef.index, sbef.shape.clone());
        }
        else if (mem.get(curr).aft instanceof Color)
            c.setBackground((Color)mem.get(curr).bef);
        else{
            ShapeElem sbef = (ShapeElem)mem.get(curr).bef;
            ShapeElem saft = (ShapeElem)mem.get(curr).aft;
            if (sbef == null)
                c.getShapes().remove(saft.index);
            else{
                Shape s = sbef.shape.clone();
                c.getShapes().set(saft.index, s);
                c.moveTo(s, sbef.index);
            }
            
        }
    }

    protected synchronized void replaceRedo() {
        if (mem.get(curr).bef == null){
            ShapeElem saft = (ShapeElem)mem.get(curr).aft;
            c.getShapes().add(saft.index, saft.shape.clone());
        }
        else if (mem.get(curr).bef instanceof Color)
            c.setBackground((Color)mem.get(curr).aft);
        else {
            ShapeElem sbef = (ShapeElem)mem.get(curr).bef;
            ShapeElem saft = (ShapeElem)mem.get(curr).aft;
            if (saft == null)
                c.getShapes().remove(sbef.index);
            else{
                Shape s = saft.shape.clone();
                c.getShapes().set(sbef.index, s);
                c.moveTo(s, saft.index);
            }
        }
    }
    
    private class ShapeElem {
        Shape shape;
        int index;
        ShapeElem(Shape s, int i){
            shape = s;
            index = i;
        }
    }
    
    private Canvas c;
}
