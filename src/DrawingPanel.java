import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class DrawingPanel extends JPanel {
    public java.util.List<Shape> shapes = new java.util.ArrayList<>();
    public java.util.List<ImageShape> fixtures = new java.util.ArrayList<>();
    private Shape draggedShape = null;
    private Point previousPoint;
    public Shape selectedShape = null;
    public int width, height;
    public DrawingPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
            setLayout(null);

        JPopupMenu popupMenu = new JPopupMenu();
        JPopupMenu mainPopup = new JPopupMenu();
        
        //Main Menu
        JMenuItem saveFile = new JMenuItem("Save File");
        JMenuItem loadFile  = new JMenuItem("Load File");
        JMenuItem clearAllShapes = new JMenuItem("Clear All Shapes");
        saveFile.addActionListener(new SaveActionListner(shapes,this));
        loadFile.addActionListener(new LoadActionListener(this));
        // clearAllShapes.addActionListener(new clearallListener(shapes,this));
        clearAllShapes.addActionListener(new ActionListener() {
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
                int option = JOptionPane.showConfirmDialog(DrawingPanel.this, "Are you sure you want to clear all shapes?", "Clear All Shapes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    shapes.clear();
                    DrawingTester.setzeroArea();
                    repaint();
                }
            }
        });
        mainPopup.add(saveFile);
        mainPopup.add(loadFile);
        mainPopup.add(clearAllShapes);

        // Shape Menu
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem cloneItem = new JMenuItem("Clone");
        JMenuItem resizeItem = new JMenuItem("Resize");
        JMenuItem editLabel = new JMenuItem("Edit Label");
        JMenuItem editColor = new JMenuItem("Edit Color");
        
        JMenu addFixture = new JMenu("Add fixture");
        JMenuItem bed = new JMenuItem("Bed");
        JMenuItem table = new JMenuItem("Table");
        JMenuItem sofa = new JMenuItem("Sofa");
        JMenuItem dining = new JMenuItem("Dining Set");
        JMenuItem commode = new JMenuItem("Commode");
        JMenuItem basin = new JMenuItem("Basin");
        JMenuItem shower = new JMenuItem("Bathtub");
        JMenuItem ksink = new JMenuItem("Kitchen Sink");
        JMenuItem stove = new JMenuItem("Stove");
        bed.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/bed.png",30,20));
        table.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/table.png",30,20));
        sofa.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/sofa.png",30,20));
        dining.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/dining.png",30,20));
        commode.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/commode.png",30,20));
        basin.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/basin.png",30,20));
        shower.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/bathtub.png",30,20));
        ksink.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/kink.png",60,60));
        stove.addActionListener(new FixtureAddActionListner(fixtures,this,"assets/stove.png",30,20));
        addFixture.add(bed);
        addFixture.add(table);
        addFixture.add(sofa);
        addFixture.add(dining);
        addFixture.add(commode);
        addFixture.add(basin);
        addFixture.add(shower);
        addFixture.add(ksink);
        addFixture.add(stove);


        JMenu addRoomMenu = new JMenu("Add Room");
        JMenuItem addRoomNorth = new JMenuItem("North");
        JMenuItem addRoomSouth = new JMenuItem("South");
        JMenuItem addRoomEast = new JMenuItem("East");
        JMenuItem addRoomWest = new JMenuItem("West");

        deleteItem.addActionListener(new DeleteActionListener(shapes, this));
        cloneItem.addActionListener(new CloneActionListener(shapes, this));
        resizeItem.addActionListener(new ResizeActionListener(this));
        editLabel.addActionListener(new EditLabelActionListener(this));
        editColor.addActionListener(new EditColorActionListener(this));
        
        addRoomNorth.addActionListener(e -> addRoomRelativeToSelected("North"));
        addRoomSouth.addActionListener(e -> addRoomRelativeToSelected("South"));
        addRoomEast.addActionListener(e -> addRoomRelativeToSelected("East"));
        addRoomWest.addActionListener(e -> addRoomRelativeToSelected("West"));

        addRoomMenu.add(addRoomNorth);
        addRoomMenu.add(addRoomSouth);
        addRoomMenu.add(addRoomEast);
        addRoomMenu.add(addRoomWest);
        
        
        popupMenu.add(addFixture);
        popupMenu.add(cloneItem);
        popupMenu.add(deleteItem);
        popupMenu.add(resizeItem);
        popupMenu.add(editLabel);
        popupMenu.add(editColor);
        popupMenu.add(addRoomMenu);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean shapeSelected = false;
        
                // Handle right-click for showing context menus
                if (SwingUtilities.isRightMouseButton(e)) {
                    for (Shape s : shapes) {
                        if (s.contains(e.getPoint())) {
                            selectedShape = s;
                            popupMenu.show(DrawingPanel.this, e.getX(), e.getY());
                            shapeSelected = true;
                            break;
                        }
                    }
                    if (!shapeSelected) {
                        mainPopup.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // Handle left-click for selecting and dragging shapes
                    for (Shape s : shapes) {
                        if (s.contains(e.getPoint())) {
                            draggedShape = s;
                            previousPoint = e.getPoint();
                            shapeSelected = true;
                            break;
                        }
                    }
                    // If no shape is selected, clear the selectedShape to avoid unwanted movement
                    if (!shapeSelected) {
                        selectedShape = null;
                        draggedShape = null;
                    }
                }
            }
        
            @Override
            public void mouseReleased(MouseEvent e) {
                draggedShape = null;
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedShape != null && SwingUtilities.isLeftMouseButton(e)) {
                    Point currentPoint = e.getPoint();
                    int dx = (int) (currentPoint.getX() - previousPoint.getX());
                    int dy = (int) (currentPoint.getY() - previousPoint.getY());
        
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
    private void addRoomRelativeToSelected(String direction) {
        if (selectedShape == null) {
            JOptionPane.showMessageDialog(null, "No room selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(102,102,102));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("InternalFrame.background", new Color(50,50,50));
        int padding = 5;
        int newX = selectedShape.x;
        int newY = selectedShape.y;

        switch (direction) {
            case "North":
                newY = selectedShape.y - selectedShape.height - padding;
                break;
            case "South":
                newY = selectedShape.y + selectedShape.height + padding;
                break;
            case "East":
                newX = selectedShape.x + selectedShape.width + padding;
                break;
            case "West":
                newX = selectedShape.x - selectedShape.width - padding;
                break;
        }
        Shape newRoom = new Shape(selectedShape.width, selectedShape.height, newX, newY, selectedShape.color, "New Room");
        String lengthString = JOptionPane.showInputDialog(this,"Enter the length of box:","LENGTH",JOptionPane.QUESTION_MESSAGE);
        String breadthString = JOptionPane.showInputDialog(this,"Enter the width of box","WIDTH",JOptionPane.QUESTION_MESSAGE);
        String newLabel = JOptionPane.showInputDialog(this,"Enter the new label:");
        if(lengthString != null && breadthString != null && newLabel != null){
            Color color = JColorChooser.showDialog(this, "Choose a color for the shape", Color.BLACK);
            int length = Integer.parseInt(lengthString)*10;
            int breadth = Integer.parseInt(breadthString)*10;

            newRoom = new Shape(breadth, length, newX, newY, color, newLabel);
        }
        if (!isOverlapping(newRoom)) {
            if (newX > width || newX<0) {
                System.out.println(width);
                System.out.println(newX);
                JOptionPane.showMessageDialog(null, "The new room is going out of bounds", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                shapes.add(newRoom);
                repaint();
            }
            
        } else {
            
            JOptionPane.showMessageDialog(null, "The new room overlaps with an existing room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean isOverlapping(Shape newShape) {
        Rectangle newBounds = new Rectangle(newShape.x, newShape.y, newShape.width, newShape.height);
        for (Shape existingShape : shapes) {
            Rectangle existingBounds = new Rectangle(existingShape.x, existingShape.y, existingShape.width, existingShape.height);
            if (newBounds.intersects(existingBounds)) {
                return true;
            }
        }
        return false;
    }

    
    public void addedShape(){
        repaint();
    }
    public void addShape(Shape shape) {
        // Determine placement for row-major order
        int padding = 10; // Padding between shapes
        int currentX = padding;
        int currentY = padding;
        int maxHeightInRow = 0;
        
        for (Shape s : shapes) {
            // Update currentX to the next position in the row
            if (currentX + s.width + padding > width) {
                currentX = padding;
                currentY += maxHeightInRow + padding;
                maxHeightInRow = 0;
            }
            currentX = s.x + s.width + padding;
            maxHeightInRow = Math.max(maxHeightInRow, s.height);
        }
    
        // If the new shape exceeds the panel width, move to the next row
        if (currentX + shape.width > width) {
            currentX = padding;
            currentY += maxHeightInRow + padding;
        }
    
        // Set the new shape's position
        shape.x = currentX;
        shape.y = currentY;
        shapes.add(shape);
        repaint();
    }

    private boolean canMove(Shape movingShape, int dx, int dy) {
    Rectangle newBounds = new Rectangle(movingShape.x + dx, movingShape.y + dy, movingShape.width, movingShape.height);

    for (Shape otherShape : shapes) {
        if (otherShape != movingShape) {
            Rectangle otherBounds = new Rectangle(otherShape.x, otherShape.y, otherShape.width, otherShape.height);
            if (newBounds.intersects(otherBounds)) {
                return false;
            }
        }
    }
    return true;
    }
    // return true; 
    /*private boolean canMove(Shape movingShape, int dx, int dy) {
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
    }*/
// }

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    // Draw all shapes
    for (Shape s : shapes) {
        s.draw(g2d);
    }

    // Fixtures are managed by Swing components (JLabel) and do not need to be redrawn here
}

    public void addImageShape(ImageShape imageShape) {
        if (!fixtures.contains(imageShape)) {
            fixtures.add(imageShape);
            this.add(imageShape.getImageLabel());
        }
        repaint();
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
        System.out.println(DrawingTester.floors.size());
        List<List<Map<String, Object>>> savefile = new ArrayList<>();
        for (DrawingPanel p : DrawingTester.floors){    
        // System.out.println(p.shapes);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Shape s : p.shapes){
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
            savefile.add(data);
        }
        try {
            String filename = JOptionPane.showInputDialog(drawingPanel, "Enter the filename to save the file as:");
            boolean extension = filename.endsWith(".rmap");
            if (extension){
                RMapFile.writeRMap(filename, savefile);
                JOptionPane.showMessageDialog(null, "File saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);  
            }
            else{
                JOptionPane.showMessageDialog(null, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Or handle the exception as appropriate for your application
        }
    }
}

// class clearallListener implements ActionListener{
//     private java.util.List<Shape> shapes;
//     private DrawingPanel drawingPanel;

//     public clearallListener(java.util.List<Shape> shapes, DrawingPanel drawingPanel){
//         this.shapes = shapes;
//         this.drawingPanel = drawingPanel;
//     }
//     @Override
//     public void actionPerformed(ActionEvent e)
// }
class LoadActionListener implements  ActionListener{
    // private java.util.List<Shape> shapes;
    private DrawingPanel drawingPanel;
    // private DrawingTester drawingTester;


    public LoadActionListener(DrawingPanel drawingPanel) {
        // this.shapes = shapes;
        this.drawingPanel = drawingPanel;
        // this.drawingTester = drawingTester;

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
            List<List<Map<String, Object>>> readData = RMapFile.readRMap(filename);
            DrawingTester.floors.clear();
            System.out.println(readData);
            for(List<Map<String,Object>> inner : readData){
                DrawingPanel drawp = new DrawingPanel(2000, 2000);
                drawp.setBackground(new Color(75, 75, 75));
                drawp.shapes = new ArrayList<>();            
                        for (Map<String, Object> obj : inner){
                            int x = Integer.parseInt((String) obj.get("x"));
                            int y = Integer.parseInt((String) obj.get("y"));
                            int width = Integer.parseInt((String) obj.get("width"));
                            int height = Integer.parseInt((String) obj.get("height"));
                            // Load color from RGB value
                            Color color = new Color((int) obj.get("color"));
                            String room_label = (String) obj.get("room_label");
                            drawp.shapes.add(new Shape(width, height, x, y, color, room_label));
                            }
                DrawingTester.floors.add(drawp);
                // drawp.repaint();
            }
            // drawingPanel.repaint();
            DrawingTester.refreshUI();
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid file extension", "Error", JOptionPane.ERROR_MESSAGE);  
        }
        drawingPanel.repaint();
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);  
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
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this shape?", "Delete Shape", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
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
    // Modify CloneActionListener to add cloned shape in row-major order
    Shape originalShape = drawingPanel.selectedShape;
    if (originalShape != null) {
        Shape clonedShape = new Shape(
            originalShape.width,
            originalShape.height,
            0, // Temporary x, will be set by addShape
            0, // Temporary y, will be set by addShape
            originalShape.color,
            originalShape.room_label
        );

        // Check for overlap before adding the cloned shape
        boolean overlap = false;
        for (Shape s : drawingPanel.shapes) {
            if (s != originalShape && s.contains(new Point(clonedShape.x, clonedShape.y))) {
                overlap = true;
                break;
            }
        }

        if (!overlap) {
            drawingPanel.addShape(clonedShape);
            DrawingTester.updateTotalAreaLabel(-originalShape.width * originalShape.height / 100);
            drawingPanel.repaint();
        }
    }
    
}
    
}


class FixtureAddActionListner implements ActionListener {
    private java.util.List<ImageShape> fixtures;
    private DrawingPanel drawingPanel;
    private String path;
    int w;
    int h;
    public FixtureAddActionListner(java.util.List<ImageShape> fixtures, DrawingPanel drawingPanel, String path,int w,int h) {
        this.fixtures = fixtures;
        this.drawingPanel = drawingPanel;
        this.path = path;
        this.w =w;
        this.h =h;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Provide the correct path to the image
        // String imagePath = "src/resources/sofa.png";
        Shape originalShape = drawingPanel.selectedShape;
        ImageShape imageShape = new ImageShape(originalShape.x-1, originalShape.y+1, w, h, path);

        if (imageShape.getImage() == null) {
            System.out.println("Image not found: " + path);
        } else {
            System.out.println("Image loaded successfully: " + path);
            drawingPanel.addImageShape(imageShape);
        }
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
        int length = Integer.parseInt(lengthString)*10;
        int breadth = Integer.parseInt(breadthString)*10;
        DrawingTester.updateTotalAreaLabel(drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.selectedShape.width = length;
        drawingPanel.selectedShape.height = breadth;
        DrawingTester.updateTotalAreaLabel(-drawingPanel.selectedShape.width * drawingPanel.selectedShape.height/100);
        drawingPanel.repaint();
        }
        else{
            JOptionPane.showMessageDialog(null, "Enter valid values for length and breadth.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "No label entered.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
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
class ImageShape {
    private int x, y;
    private int width, height;
    private JLabel imageLabel;
    private Point initialClick;
    private Image originalImage;

    public ImageShape(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Load and resize the image to match the specified width and height
        ImageIcon originalIcon = new ImageIcon(imagePath);
        originalImage = originalIcon.getImage(); // Set the original image
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create a JLabel with the resized image
        imageLabel = new JLabel(scaledIcon);
        imageLabel.setBounds(x, y, width, height);

        // Add mouse listeners for dragging and rotating the image
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    initialClick = e.getPoint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Show a context menu instead of changing position
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    JMenuItem rotateLeftItem = new JMenuItem("Rotate Left");
                    JMenuItem rotateRightItem = new JMenuItem("Rotate Right");

                    deleteItem.addActionListener(event -> {
                        Container parent = imageLabel.getParent();
                        parent.remove(imageLabel);
                        parent.repaint();
                    });

                    rotateLeftItem.addActionListener(event -> rotateImage(-90));
                    rotateRightItem.addActionListener(event -> rotateImage(90));

                    popupMenu.add(deleteItem);
                    popupMenu.add(rotateLeftItem);
                    popupMenu.add(rotateRightItem);
                    popupMenu.show(imageLabel, e.getX(), e.getY());
                    e.consume(); // Prevent unintended side effects
                }
            }
        });

        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Get the current location of the label
                    int thisX = imageLabel.getX();
                    int thisY = imageLabel.getY();

                    // Determine how much the mouse moved since the initial click
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    // Move the label to the new location
                    int nextX = thisX + xMoved;
                    int nextY = thisY + yMoved;

                    // Set the label to the new position
                    imageLabel.setLocation(nextX, nextY);
                    imageLabel.getParent().repaint();
                }
            }
        });
    }

    private void rotateImage(int angle) {
        // Calculate the size of the rotated image to ensure full visibility
        double radians = Math.toRadians(Math.abs(angle));
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) (width * cos + height * sin);
        int newHeight = (int) (width * sin + height * cos);
    
        // Create a rotated version of the original image
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        
        // Set rendering hints for smoother rotation
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        // Center the rotation
        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(Math.toRadians(angle));
        transform.translate(-width / 2.0, -height / 2.0);
    
        // Draw the rotated image
        g2d.drawImage(originalImage, transform, null);
        g2d.dispose();
    
        // Update the image label with the rotated image
        ImageIcon rotatedIcon = new ImageIcon(rotatedImage);
        imageLabel.setIcon(rotatedIcon);
    
        // Update the internal width and height
        width = newWidth;
        height = newHeight;
    
        // Update the bounds of the label to accommodate the new image size
        imageLabel.setBounds(x, y, width, height);
        imageLabel.getParent().revalidate();
        imageLabel.getParent().repaint();
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public Image getImage() {
        return new ImageIcon(imageLabel.getIcon().toString()).getImage();
    }
}
