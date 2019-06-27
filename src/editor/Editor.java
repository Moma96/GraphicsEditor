package editor;

import editor.Coordinator.Tool;
import editor.exceptions.BadFormat;
import editor.shape.Path;
import editor.shape.Shape;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;


class Editor extends JFrame {
    
    public static final String DEFAULTSAVELOADDIRECTORY = "documents";
    
    public Editor() {
        super("Graphic Editor");
        initComponents();
    }
    
    public static void main(String args[]) {
        new Editor().setVisible(true);
    }

    public void addWidth(int w){
        JMenuItem i = new JMenuItem(String.valueOf(w));
        i.addActionListener((ActionEvent e) -> {
            coor.setWidth(w);
            widthmenu.setText(String.valueOf(w));
        });
        widthmenu.add(i);
    }
    
    public void addColor(Color c, String s) {
        JButton color = new JButton(s);
        color.setBackground(c);
        color.addActionListener((ActionEvent e) -> {
            coor.setColor(c);
            currColor.setBackground(c==null?Shape.DEFAULTLNCOLOR:c);
        });
        colorbar.add(color);
    }
    
    public void addColor(Color c) {
        addColor(c, null);
    }
    
    public void disableButtons(){
        for (JButton b : buttons) b.setEnabled(false);
    }
    
    public void enableButtons(){
        for (JButton b : buttons) b.setEnabled(true);
    }

    public void dispose(){
        authorFrame.dispose();
        saveAsFrame.dispose();
        loadFrame.dispose();
        super.dispose();
    }
    
    void setCurrTool(Tool t) throws IOException {
        String path = " ";
        for (int i=0; i<tools.length; i++){
            if (tools[i].getTool() == t){
                path = tools[i].path;
                break;
            }
        }
        currTool.setIcon(new ImageIcon(ImageIO.read(new File(path))));
    }
    
    void setCurrLnColor() {
        currLnColor.setBackground(canvas.currShape.getLnColor());
    }
    
    private void initComponents() {
        try{
            initCanvas();
            add(canvas, BorderLayout.CENTER);
            initMenuBar();
            initToolBar();
            initStatusBar();
            addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                   dispose();
               } 
            });
            pack();
        }catch(IOException e) {
            System.out.println(e.toString());
        }
    }
    
    private void initCanvas() {
        canvas = new Canvas();
        coor = new Coordinator(canvas, this);
        buttons = new LinkedList();
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                coor.canvasMousePressed(e);
                setDimension();
            }
            public void mouseReleased(MouseEvent e) {
                coor.canvasMouseReleased(e);
            }
            public void mouseClicked(MouseEvent e) {
                clicked++;
                if(clicked == 2 && e.getX() >= dblX-1 && e.getX() <= dblX+1
                                && e.getY() >= dblY-1 && e.getY() <= dblY+1){
                    coor.canvasMouseDoubleClicked(e);
                    clicked = 0;
                }else{
                    coor.canvasMouseClicked(e);
                    if (canvas.currShape != null && canvas.currShape instanceof Path)
                        dimension.setText(canvas.currShape.dimension());
                    clicked = 1;
                }
                if (clicked == 1){
                    dblX = e.getX();
                    dblY = e.getY();
                }
            }
        });
        canvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                coor.canvasMouseDragged(e);
                setPosition(e);
                setDimension();
            }

            public void mouseMoved(MouseEvent e) {
                setPosition(e);
                
            }
        });
    }
    
    private void setPosition(MouseEvent e) {
        position.setText("(" + e.getX() + ", " + e.getY() + ")");
    }
    
    private void setDimension() {
        if (canvas.currShape != null)
            dimension.setText(canvas.currShape.dimension());
        else dimension.setText("");
    }
    
    private void initMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("file");
        JMenu help = new JMenu("help");
        menubar.add(file);
        menubar.add(help);
        JLabel authorLabel = new JLabel("Momcilo Nikolic");
        authorLabel.setHorizontalAlignment(JLabel.CENTER);
        JTextArea saveText = new JTextArea(DEFAULTSAVELOADDIRECTORY);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener((ActionEvent e) -> {
            canvas.saveAs(saveText.getText());
        });
        JTextArea loadText = new JTextArea(DEFAULTSAVELOADDIRECTORY);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener((ActionEvent e) -> {
            try{
                canvas.load(loadText.getText());
            }catch(BadFormat ex){ System.out.println(ex);}
        });
        authorFrame = new JDialog(this, "Author");
        authorFrame.setBounds(100, 100, 200, 100);
        authorFrame.add(authorLabel);
        authorFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                authorFrame.setVisible(false);
            }
        });
        saveAsFrame = new JDialog(this, "Save As");
        saveAsFrame.setBounds(100, 100, 300, 100);
        saveAsFrame.add(saveText, BorderLayout.CENTER);
        saveAsFrame.add(saveButton, BorderLayout.SOUTH);
        saveAsFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                saveAsFrame.setVisible(false);
            }
        });
        loadFrame = new JDialog(this, "Load");
        loadFrame.setBounds(100, 100, 300, 100);
        loadFrame.add(loadText, BorderLayout.CENTER);
        loadFrame.add(loadButton, BorderLayout.SOUTH);
        loadFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                loadFrame.setVisible(false);
            }
        });
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener((ActionEvent e) -> {
            if (!canvas.save())
                saveAsFrame.setVisible(true);
        });
        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.addActionListener((ActionEvent e) -> {
            saveAsFrame.setVisible(true);
        });
        JMenuItem load = new JMenuItem("Load");
        load.addActionListener((ActionEvent e) -> {
            loadFrame.setVisible(true);
        });
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener((ActionEvent e) -> {
            canvas.clear();
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener((ActionEvent e) -> {
            dispose();
        });
        JMenuItem author = new JMenuItem("Author");
        author.addActionListener((ActionEvent e) -> {
            authorFrame.setVisible(true);
        });
        file.add(save);
        file.add(saveAs);
        file.add(load);
        file.add(close);
        file.add(exit);
        help.add(author);
        setJMenuBar(menubar);
    }
    
    private class ToolButton {
        static final String PATHFOLDER = "icons";
        public ToolButton(Coordinator.Tool t, String i) throws IOException {
            tool = t;
            path = PATHFOLDER.equals("")? i: PATHFOLDER + "//" + i;
            addTool(t, path);
        }
        public Tool getTool() {
            return tool;
        }
        public String getPath() {
            return path;
        }
        private Tool tool;
        private String path;
    }
    
    private void initToolBar() throws IOException{
        toolbar = new JPanel();
        add(toolbar, BorderLayout.NORTH);
        tools = new ToolButton[]{ new ToolButton(Coordinator.Tool.SELECT, "SelectIcon.png"),
                                               new ToolButton(Coordinator.Tool.ERASE, "EraseIcon.png"),
                                               new ToolButton(Coordinator.Tool.FILL, "FillIcon.png"),
                                               new ToolButton(Coordinator.Tool.LINE, "LineIcon.png"),
                                               new ToolButton(Coordinator.Tool.RECTANGLE, "RectangleIcon.png"),
                                               new ToolButton(Coordinator.Tool.PATH, "PathIcon.png"),
                                               new ToolButton(Coordinator.Tool.POLYGON, "PolygonIcon.png")};
        initWidths();
        initColors();
    }
    
    private void addTool(Coordinator.Tool t, String icon) throws IOException {
        JButton b = new JButton(new ImageIcon(ImageIO.read(new File(icon))));
        toolbar.add(b);
        b.setBackground(Color.WHITE);
        b.addActionListener((ActionEvent e) -> {
            coor.setTool(t);
            try{
                setCurrTool(t);
            } catch(IOException ioe) {}
        });
        buttons.add(b);
    }
    
    private void initWidths() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel("WIDTH"));
        widthmenu = new JMenu(String.valueOf(Shape.DEFAULTLNWIDTH));
        JMenuBar bar = new JMenuBar();
        bar.add(widthmenu);
        panel.add(bar);
        int[] widths = new int[]{ 2, 4, 8, 16 };
        for (int i=0; i<widths.length; i++)
            addWidth(widths[i]);
        toolbar.add(panel);
    }
    
    private void initColors() {
        currColor = new JLabel();
        currColor.setOpaque(true);
        currColor.setBackground(Shape.DEFAULTLNCOLOR);
        colorbar = new JPanel();
        colorbar.setLayout(new GridLayout(2, 8));
        colorbar.add(currColor);
        toolbar.add(colorbar);
        Color[] colors = new Color[]{ Color.BLACK, Color.DARK_GRAY, Color.RED,  Color.RED.darker().darker(),
                                      Color.CYAN.darker(), Color.BLUE, Color.YELLOW, Color.ORANGE,
                                      Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.GREEN,
                                      Color.GREEN.darker().darker(), Color.PINK, Color.MAGENTA, Color.MAGENTA.darker().darker() };
        int n = colors.length;
        for (int i=0; i<(n%2 == 1?n/2+1:n/2); i++)
            addColor(colors[i]);
        addColor(null, "D");
        for (int i=(n%2 == 1?n/2+1:n/2); i<n; i++)
            addColor(colors[i]);
    }
    
    private void initStatusBar() throws IOException {
        statusbar = new JPanel();
        add(statusbar, BorderLayout.SOUTH);
        statusbar.setLayout(new GridLayout(1, 3));
        initColorTool();
        initUndoRedo();
        initPosDim();
    }
    
    private void initColorTool() throws IOException {
        currTool = new JLabel();
        setCurrTool(Coordinator.DEFAULTTOOL);
        currTool.setHorizontalAlignment(JLabel.CENTER);
        currLnColor = new JLabel();
        currLnColor.setOpaque(true);
        currLnColor.setBackground(Shape.DEFAULTLNCOLOR);
        JPanel colorTool = new JPanel();
        JPanel lnColor = new JPanel();
        lnColor.setLayout(new GridLayout(2, 2));
        lnColor.add(new JLabel("Line Color:"));
        lnColor.add(new JLabel());
        lnColor.add(currLnColor);
        lnColor.add(new JLabel());
        colorTool.setLayout(new GridLayout(1, 3));
        colorTool.add(currTool);
        colorTool.add(lnColor);
        colorTool.add(new JPanel());
        statusbar.add(colorTool);
    }
    
    private void initUndoRedo() throws IOException {
        String undoIcon = "UndoIcon.png";
        String redoIcon = "RedoIcon.png";
        JButton undo = new JButton(new ImageIcon(ImageIO.read(new File(
                ToolButton.PATHFOLDER.equals("")? undoIcon: ToolButton.PATHFOLDER + "//" + undoIcon))));
        JButton redo = new JButton(new ImageIcon(ImageIO.read(new File(
                ToolButton.PATHFOLDER.equals("")? redoIcon: ToolButton.PATHFOLDER + "//" + redoIcon))));
        undo.setBackground(Color.WHITE);
        redo.setBackground(Color.WHITE);
        buttons.add(undo);
        buttons.add(redo);
        undo.addActionListener((ActionEvent e) ->{
            coor.mem.undo();
            canvas.currShape = null;
            canvas.repaint();
        });
        redo.addActionListener((ActionEvent e) ->{
            coor.mem.redo();
            canvas.currShape = null;
            canvas.repaint();
        });
        JPanel undoRedo = new JPanel();
        undoRedo.add(undo);
        undoRedo.add(redo);
        statusbar.add(undoRedo);
    }
    
    private void initPosDim() {
        position = new JLabel();
        dimension = new JLabel();
        JPanel posDim = new JPanel();
        posDim.setLayout(new GridLayout(2, 3));
        posDim.add(new JPanel());
        posDim.add(new JPanel());
        posDim.add(position);
        posDim.add(new JPanel());
        posDim.add(new JPanel());
        posDim.add(dimension);
        statusbar.add(posDim);
    }
     
    JLabel currLnColor;
    private Canvas canvas;
    private Coordinator coor;
    private ToolButton[] tools;
    private JPanel toolbar;
    private JPanel colorbar;
    private JPanel statusbar;
    private JMenu widthmenu;
    private JLabel currColor;
    private JDialog authorFrame;
    private JDialog saveAsFrame;
    private JDialog loadFrame;
    private JLabel currTool;
    private JLabel position;
    private JLabel dimension;
    private LinkedList<JButton> buttons;
    private int clicked;
    private int dblX, dblY;
}