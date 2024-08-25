import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DrawingPanel extends JPanel {
    public java.util.List<Shape> shapes = new java.util.ArrayList<>();
    private Shape draggedShape = null;
    private Point previousPoint;
    public Shape selectedShape = null;
    public int width, height;
    public DrawingPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        JPopupMenu popupMenu = new JPopupMenu();
        JPopupMenu mainPopup = new JPopupMenu();
        
        //Main Menu
        JMenuItem saveFile = new JMenuItem("Save File");
        JMenuItem loadFile  = new JMenuItem("Load File");
        saveFile.addActionListener(new SaveActionListner(shapes,this));
        loadFile.addActionListener(new LoadActionListener(shapes,this));
        mainPopup.add(saveFile);
        mainPopup.add(loadFile);
        //Shape Menu
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem cloneItem = new JMenuItem("Clone");
        JMenuItem resizeItem = new JMenuItem("Resize");
        JMenuItem editLabel = new JMenuItem("Edit Label");
        JMenuItem editColor = new JMenuItem("Edit Color");
        deleteItem.addActionListener(new DeleteActionListener(shapes, this));
        cloneItem.addActionListener(new CloneActionListener(shapes, this));
        resizeItem.addActionListener(new ResizeActionListener(this));
        editLabel.addActionListener(new EditLabelActionListener(this));
        editColor.addActionListener(new EditColorActionListener(this));
        popupMenu.add(cloneItem);
        popupMenu.add(deleteItem);
        popupMenu.add(resizeItem);
        popupMenu.add(editLabel);
        popupMenu.add(editColor);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Shape s : shapes) {
                    if (s.contains(e.getPoint())) {
                        draggedShape = s;
                        previousPoint = e.getPoint();
                        if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0 && e.getClickCount() == 1){
                            selectedShape = s;
                            popupMenu.show(DrawingPanel.this, e.getX(), e.getY());
                        }
                        break;
                    }
                    else if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0 && e.getClickCount() == 1){
                        mainPopup.show(DrawingPanel.this,e.getX(),e.getY());
                    }   

                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedShape = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedShape != null) {
                    Point currentPoint = e.getPoint();
                    int dx = (int)(currentPoint.getX() - previousPoint.getX());
                    int dy = (int)(currentPoint.getY() - previousPoint.getY());
                    
                    if (canMove(draggedShape, dx, dy)) {
                        draggedShape.translate(dx, dy);
                        previousPoint = currentPoint;
                        repaint();
                    }
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }
    
    public void addedShape(){
        repaint();
    }
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    private boolean canMove(Shape movingShape, int dx, int dy) {
        Rectangle newBounds = new Rectangle(
            movingShape.x + dx,
            movingShape.y + dy,
            movingShape.width,
            movingShape.height
        );
        
        for (Shape otherShape : shapes) {
            if (otherShape != movingShape) {
                Rectangle otherBounds = new Rectangle(
                    otherShape.x,
                    otherShape.y,
                    otherShape.width,
                    otherShape.height
                );
                if (newBounds.intersects(otherBounds)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Shape s : shapes) {
            s.draw(g2d);


        }

    }
}

class SaveActionListner implements ActionListener{
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public SaveActionListner(java.util.List<Shape> shapes, DrawingPanel drawingPanel){
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        List<Map<String, Object>> data = new ArrayList<>();
        for (Shape s : shapes){
            Map<String, Object> obj1 = new HashMap<>();
            obj1.put("x",String.valueOf(s.x));
            obj1.put("y",String.valueOf(s.y));
            obj1.put("width",String.valueOf(s.width));
            obj1.put("height",String.valueOf(s.height));
            // Save color as RGB values
            obj1.put("color", s.color.getRGB());
            obj1.put("room_label",String.valueOf(s.room_label));
            data.add(obj1);

        }
        try {
            String filename = JOptionPane.showInputDialog(drawingPanel, "Enter the filename to save the file as:");
            boolean extension = filename.endsWith(".rmap");
            if (extension){
                RMapFile.writeRMap(filename, data);
                JOptionPane.showMessageDialog(drawingPanel, "File saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);  
            }
            else{
                JOptionPane.showMessageDialog(drawingPanel, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Or handle the exception as appropriate for your application
        }
    }
}
class LoadActionListener implements  ActionListener{
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public LoadActionListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel){
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public  void actionPerformed(ActionEvent e){
        try{
            UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Button.background", new Color(102,102,102));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
            UIManager.put("OptionPane.background", new Color(50,50,50));
            UIManager.put("Panel.background", new Color(50,50,50));
            UIManager.put("InternalFrame.background", new Color(50,50,50));
        String filename = JOptionPane.showInputDialog(drawingPanel, "Enter the filename to load the file from:");
        boolean extension = filename.endsWith(".rmap");
        if (extension){
            List<Map<String, Object>> readData = RMapFile.readRMap(filename);
            shapes.clear();
        for (Map<String, Object> obj : readData){
            int x = Integer.parseInt((String) obj.get("x"));
            int y = Integer.parseInt((String) obj.get("y"));
            int width = Integer.parseInt((String) obj.get("width"));
            int height = Integer.parseInt((String) obj.get("height"));
            // Load color from RGB value
            Color color = new Color((int) obj.get("color"));
            String room_label = (String) obj.get("room_label");
            shapes.add(new Shape(width, height, x, y, color, room_label));
            }
        }
        else{
            JOptionPane.showMessageDialog(drawingPanel, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
        }
        drawingPanel.repaint();
        } catch (IOException ex){
            JOptionPane.showMessageDialog(drawingPanel, "File not found", "Error", JOptionPane.ERROR_MESSAGE);  
            ex.printStackTrace();
        }

    }
}
class DeleteActionListener implements ActionListener {
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public DeleteActionListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel) {
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        int option = JOptionPane.showConfirmDialog(drawingPanel, "Are you sure you want to delete this shape?", "Delete Shape", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            shapes.remove(drawingPanel.selectedShape);
            DrawingTester.updateTotalAreaLabel(drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
            drawingPanel.selectedShape = null;
            drawingPanel.repaint();
        }
    }
}

class CloneActionListener implements ActionListener {
    private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;

    public CloneActionListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel) {
        this.shapes = shapes;
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if ((int)drawingPanel.selectedShape.width*2+(int)drawingPanel.selectedShape.x+1< drawingPanel.width){
        shapes.add(new Shape((int)drawingPanel.selectedShape.width,(int)drawingPanel.selectedShape.height, 
        (int)drawingPanel.selectedShape.x+(int)drawingPanel.selectedShape.width+1,(int)drawingPanel.selectedShape.y, 
                            drawingPanel.selectedShape.color, drawingPanel.selectedShape.room_label));
                        }
        else{
        shapes.add(new Shape((int)drawingPanel.selectedShape.width,(int)drawingPanel.selectedShape.height, 
        (int)drawingPanel.selectedShape.x,(int)drawingPanel.selectedShape.y+drawingPanel.selectedShape.height+1, 
                            drawingPanel.selectedShape.color, drawingPanel.selectedShape.room_label));
        }
        DrawingTester.updateTotalAreaLabel(-drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.repaint();
    }
    
}

class ResizeActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public ResizeActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        
        
        String lengthString = JOptionPane.showInputDialog(drawingPanel,"Enter the length of box:","LENGTH",JOptionPane.QUESTION_MESSAGE);
        String breadthString = JOptionPane.showInputDialog(drawingPanel,"Enter the width of box","WIDTH",JOptionPane.QUESTION_MESSAGE);

        
        
        if (lengthString != null && breadthString != null){
        int length = Integer.parseInt(lengthString);
        int breadth = Integer.parseInt(breadthString);
        DrawingTester.updateTotalAreaLabel(drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.selectedShape.width = breadth;
        drawingPanel.selectedShape.height = length;
        DrawingTester.updateTotalAreaLabel(-drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.repaint();
        }
        else{
            JOptionPane.showMessageDialog(drawingPanel, "Enter valid values for length and breadth.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class EditLabelActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public EditLabelActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        
        String newLabel = JOptionPane.showInputDialog(drawingPanel,"Enter the new label:");
        if (newLabel != null){
            drawingPanel.selectedShape.room_label = newLabel;
            drawingPanel.repaint();
        }
        else{
            JOptionPane.showMessageDialog(drawingPanel, "No label entered.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class EditColorActionListener implements ActionListener{
    private DrawingPanel drawingPanel;

    public EditColorActionListener(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e){        
        Color color = JColorChooser.showDialog(drawingPanel, "Choose a color for the shape", Color.BLACK);
        drawingPanel.selectedShape.color = color;
        drawingPanel.repaint();
    }
}

class Shape {
    int width, height;
    int x, y;
    int x_label, y_label;
    public Color color;
    public String room_label;

    public Shape(int w, int h, int x, int y, Color color, String room_label) {
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
        this.color =  color;
        this.room_label = room_label;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fill(new Rectangle2D.Double(x, y, width, height));
        g2d.setColor(color.BLACK);
        g2d.draw(new Rectangle2D.Double(x, y, width, height));
        int x_label = x + (width - g2d.getFontMetrics().stringWidth(room_label)) / 2;
        int y_label = y + ((height - g2d.getFontMetrics().getHeight()) / 2) + g2d.getFontMetrics().getAscent();
        g2d.drawString(room_label, x_label, y_label);

    }

    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
}