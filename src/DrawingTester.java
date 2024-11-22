import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
public class DrawingTester {
    public static double totalArea = 0; 
    private static JLabel totalAreaLabel;
    public static List<DrawingPanel> floors = new ArrayList<>();
    private static JPanel sidebarPanel;
    private static JPanel mainPanel;
    private static int currentFloor = 0;

    public static void main(String[] args) {
        // Set the look and feel to system default
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        JFrame f = new JFrame("BubbleGum");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the main container panel
        mainPanel = new JPanel(new BorderLayout());
        
        // Create and setup the sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(50, 50, 50));
        sidebarPanel.setPreferredSize(new Dimension(150, 1080));
        
        // Add the "Add New Floor" button
        addNewFloorButton();
        
        // Create the first drawing panel with large dimensions
        DrawingPanel drawingPanel = new DrawingPanel(2000, 2000);
        drawingPanel.setBackground(new Color(75, 75, 75));
        floors.add(drawingPanel);
        addFloorButton("Floor 1", 0);

        // Create the scroll pane for the drawing panel
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        // Add viewport border to make the scrollable area visible
        scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Setup the total area label
        totalAreaLabel = new JLabel("Total Floor Area: 0 sq.ft");
        totalAreaLabel.setForeground(Color.WHITE);
        
        // Create a panel for the label to ensure it stays visible
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(new Color(75, 75, 75));
        labelPanel.add(totalAreaLabel);
        
        // Create the add shape button
        CircleButton circleButton = new CircleButton("+");
        circleButton.addActionListener(new ActionListener() {
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
                
                String lengthString = JOptionPane.showInputDialog(f,"Enter the length of box:");
                String breadthString = JOptionPane.showInputDialog(f,"Enter the breadth of box");
                String roomLabel = JOptionPane.showInputDialog(f, "Enter the room label:");
                
                if(lengthString != null && breadthString != null) {
                    try {
                        int length = Integer.parseInt(lengthString)*10;
                        int breadth = Integer.parseInt(breadthString)*10;
                        if(length > 0 && breadth > 0) {
                            Color color = JColorChooser.showDialog(f, "Choose a color for the shape", Color.BLACK);
                            if (color != null) {
                                DrawingPanel currentPanel = floors.get(currentFloor);
                                currentPanel.addShape(new Shape(breadth, length, 100, 100, color, roomLabel));
                                currentPanel.addedShape();
                                updateTotalArea(length, breadth);
                            }
                        } else {
                            JOptionPane.showMessageDialog(f, "Enter positive values for length and breadth.", 
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(f, "Please enter valid numeric values.", 
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // Create a panel for the button to ensure it stays visible
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(75, 75, 75));
        buttonPanel.add(circleButton);
        
        // Create a panel for the top controls
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(75, 75, 75));
        controlPanel.add(labelPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add components to the main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Set up the frame
        f.setContentPane(mainPanel);
        f.setSize(1920, 1080);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
    }

    // Method to refresh the UI
    public static void refreshUI() {
        sidebarPanel.removeAll();
        addNewFloorButton();
        for (int i = 0; i < floors.size(); i++) {
            addFloorButton("Floor " + (i + 1), i);
        }
        switchToFloor(currentFloor);
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private static void addFloorButton(String text, int floorIndex) {
        JButton floorButton = new JButton(text);
        floorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        floorButton.setMaximumSize(new Dimension(130, 40));
        floorButton.addActionListener(e -> switchToFloor(floorIndex));
        sidebarPanel.add(floorButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private static void addNewFloorButton() {
        JButton newFloorButton = new JButton("Add New Floor");
        newFloorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newFloorButton.setMaximumSize(new Dimension(130, 40));
        newFloorButton.addActionListener(e -> createNewFloor());
        sidebarPanel.add(newFloorButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private static void createNewFloor() {
        int floorNumber = floors.size() + 1;
        DrawingPanel newPanel = new DrawingPanel(2000, 2000);
        newPanel.setBackground(new Color(75, 75, 75));
        floors.add(newPanel);
        
        addFloorButton("Floor " + floorNumber, floorNumber - 1);
        switchToFloor(floorNumber - 1);
    }

    private static void switchToFloor(int floorIndex) {
        currentFloor = floorIndex;
        DrawingPanel newPanel = floors.get(floorIndex);
        
        // Create new scroll pane for the selected floor
        JScrollPane scrollPane = new JScrollPane(newPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        // Replace the center component
        Component centerPanel = mainPanel.getComponent(1);
        ((JPanel)centerPanel).remove(1);
        ((JPanel)centerPanel).add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void updateTotalArea(int length, int breadth) {
        totalArea += length * breadth / 100;
        totalAreaLabel.setText("Total Floor Area: " + totalArea + " sq.ft");
    }

    public static void updateTotalAreaLabel(int area) {
        totalArea -= area;
        totalAreaLabel.setText("Total Floor Area: " + totalArea + " sq.ft");
    }

    public static void setzeroArea() {
        totalArea = 0;
        totalAreaLabel.setText("Total Floor Area: " + totalArea + " sq.ft");
    }
}