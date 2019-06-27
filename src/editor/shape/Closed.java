package editor.shape;

import java.awt.Color;
import java.awt.Graphics2D;

public interface Closed {
    
    public Color getFillColor();
    
    public void setFillColor(Color c);
    
    public void fill(Graphics2D g);
}
