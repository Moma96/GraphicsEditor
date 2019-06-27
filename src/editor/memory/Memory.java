package editor.memory;

import java.util.LinkedList;

public abstract class Memory {
    public static final int DEFAULTCAPACITY = 10;
    
    public Memory() {
        mem = new LinkedList();
    }
     
    public void saveBefore(Object o) {
        newElem = new Elem();
        if (o != null)
            newElem.bef = clone(o);
    }
    
    public void saveAfter(Object o) {
        if (curr < mem.size() - 1){
            while(curr + 1 < mem.size())
                mem.remove(curr + 1);
        }
        if (o != null)
            newElem.aft = clone(o);
        mem.add(newElem);
        curr++;
        if (mem.size() > DEFAULTCAPACITY){
            mem.removeFirst();
            curr--;
        }
    }
    
    public synchronized void undo() {
        if (curr < 0)
            return;
        else
            replaceUndo();
        curr--;
    }
    
    public synchronized void redo() {
        curr++;
        if (curr >= mem.size()){
            curr--;
            return;
        }
        else
            replaceRedo();
    }
    
    protected abstract Object clone(Object o);
    
    protected abstract void replaceUndo();
    
    protected abstract void replaceRedo();
    
    protected class Elem{
        Object aft; // state before
        Object bef; // state after
    }
    
    protected LinkedList<Elem> mem;
    protected int curr = -1;
    protected Elem newElem;
}
