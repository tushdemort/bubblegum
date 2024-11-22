// Add these methods to your DrawingPanel class in DrawingPanel.java

public class DrawingPanel extends JPanel {
    // ... existing code ...
    
    private static final int ALIGNMENT_MARGIN = 1; // 1 pixel margin for alignment
    private JPopupMenu alignmentMenu = new JPopupMenu();
    
    public DrawingPanel(int width, int height) {
        // ... existing constructor code ...
        
        // Add alignment menu items
        JMenuItem alignEast = new JMenuItem("Align East");
        JMenuItem alignWest = new JMenuItem("Align West");
        JMenuItem alignNorth = new JMenuItem("Align North");
        JMenuItem alignSouth = new JMenuItem("Align South");
        JMenuItem alignCenter = new JMenuItem("Align Center");
        
        alignEast.addActionListener(e -> alignShape("EAST"));
        alignWest.addActionListener(e -> alignShape("WEST"));
        alignNorth.addActionListener(e -> alignShape("NORTH"));
        alignSouth.addActionListener(e -> alignShape("SOUTH"));
        alignCenter.addActionListener(e -> alignShape("CENTER"));
        
        alignmentMenu.add(alignEast);
        alignmentMenu.add(alignWest);
        alignmentMenu.add(alignNorth);
        alignmentMenu.add(alignSouth);
        alignmentMenu.add(alignCenter);
        
        // Modify mousePressed in MouseAdapter to include:
        if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0 && e.getClickCount() == 1) {
            for (Shape s : shapes) {
                if (s.contains(e.getPoint())) {
                    selectedShape = s;
                    if (e.isShiftDown()) { // Show alignment menu when Shift+Right-click
                        alignmentMenu.show(DrawingPanel.this, e.getX(), e.getY());
                    } else {
                        popupMenu.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                    break;
                }
            }
        }
    }

    private void alignShape(String alignment) {
        if (selectedShape == null) {
            JOptionPane.showMessageDialog(this, "Please select a reference room first", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newX = selectedShape.x;
        int newY = selectedShape.y;
        
        // Show input dialog for new room dimensions
        String lengthStr = JOptionPane.showInputDialog(this, "Enter room length:");
        String widthStr = JOptionPane.showInputDialog(this, "Enter room width:");
        String label = JOptionPane.showInputDialog(this, "Enter room label:");
        
        if (lengthStr == null || widthStr == null || label == null) return;
        
        try {
            int length = Integer.parseInt(lengthStr);
            int width = Integer.parseInt(widthStr);
            
            // Calculate new position based on alignment
            switch (alignment) {
                case "EAST":
                    newX = selectedShape.x + selectedShape.width + ALIGNMENT_MARGIN;
                    newY = selectedShape.y;
                    break;
                case "WEST":
                    newX = selectedShape.x - width - ALIGNMENT_MARGIN;
                    newY = selectedShape.y;
                    break;
                case "NORTH":
                    newX = selectedShape.x;
                    newY = selectedShape.y - length - ALIGNMENT_MARGIN;
                    break;
                case "SOUTH":
                    newX = selectedShape.x;
                    newY = selectedShape.y + selectedShape.height + ALIGNMENT_MARGIN;
                    break;
                case "CENTER":
                    newX = selectedShape.x + (selectedShape.width - width) / 2;
                    newY = selectedShape.y + (selectedShape.height - length) / 2;
                    break;
            }
            
            // Create temporary shape to check for overlap
            Shape newShape = new Shape(width, length, newX, newY, selectedShape.color, label);
            
            // Check for overlap with existing shapes
            if (checkOverlap(newShape)) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot place room here - overlap detected", 
                    "Overlap Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Add the new shape if no overlap
            shapes.add(newShape);
            DrawingTester.updateTotalAreaLabel(-width * length/100);
            repaint();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for dimensions", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkOverlap(Shape newShape) {
        Rectangle newBounds = new Rectangle(
            newShape.x, newShape.y, 
            newShape.width, newShape.height
        );
        
        for (Shape existingShape : shapes) {
            if (existingShape != newShape) {
                Rectangle existingBounds = new Rectangle(
                    existingShape.x, existingShape.y,
                    existingShape.width, existingShape.height
                );
                if (newBounds.intersects(existingBounds)) {
                    return true;
                }
            }
        }
        return false;
    }
}
