import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingTester {
    public static double totalArea = 0; 
    private static JLabel totalAreaLabel;
    private static List<DrawingPanel> floors = new ArrayList<>();
    private static JPanel sidebarPanel;
    private static JPanel mainPanel;
    private static int currentFloor = 0;

    public static void main(String[] args) {
        int w = 1920;
        int h = 1080;
        JFrame f = new JFrame("BubbleGum");
        
        mainPanel = new JPanel(new BorderLayout());
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(50, 50, 50));
        sidebarPanel.setPreferredSize(new Dimension(150, h));

        addNewFloorButton();

        DrawingPanel panel = new DrawingPanel(w - 150, h);
        floors.add(panel);
        addFloorButton("Floor 1", 0);

        totalAreaLabel = new JLabel("Total Floor Area: 0 sq.ft");
        totalAreaLabel.setBounds(10, 10, 200, 30);
        totalAreaLabel.setForeground(Color.WHITE);
        panel.add(totalAreaLabel);
        panel.setBackground(new Color(75,75,75));

        CircleButton circleButton = new CircleButton("+");
        circleButton.addActionListener(new ActionListener(){
            @Override	
            public void actionPerformed(ActionEvent e){
                UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                UIManager.put("OptionPane.messageForeground", Color.WHITE);
                UIManager.put("Button.background", new Color(102,102,102));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("OptionPane.background", new Color(75,75,75));
                UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(102,102,102), 3));
                UIManager.put("Panel.background", new Color(75,75,75));
                UIManager.put("InternalFrame.background", new Color(75,75,75));
                String lengthString = JOptionPane.showInputDialog(f,"Enter the length of box:");
                String breadthString = JOptionPane.showInputDialog(f,"Enter the breadth of box");
                String roomLabel = JOptionPane.showInputDialog(f, "Enter the room label:");
                if(lengthString!=null && breadthString!=null){
                    try{
                        int length = Integer.parseInt(lengthString);
                        int breadth = Integer.parseInt(breadthString);
                        if(length>0 && breadth>0){
                            UIManager.put("Button.border", BorderFactory.createLineBorder(Color.BLACK,1));
                            UIManager.put("Button.foreground", Color.black);
                            UIManager.put("Button.background", Color.white);
                                UIManager.put("Panel.background", Color.white);
                                UIManager.put("OptionPane.background", Color.white);
                                Color color = JColorChooser.showDialog(f, "Choose a color for the shape", Color.BLACK);
                                 if (color != null) {
                                panel.addShape(new Shape(length, breadth, w/2, h/3,color,roomLabel));
                                panel.addedShape();
                                updateTotalArea(length, breadth);
                                 }
                            }else{
                                 JOptionPane.showMessageDialog(f, "Enter positive values for length and breadth.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            }
                        }catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(f, "Please enter valid numeric values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                    }
                    
				}
        });

        panel.setLayout(null);
        circleButton.setBounds(1700, 525, 50, 50);
        panel.add(circleButton);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(panel, BorderLayout.CENTER);

        f.setContentPane(mainPanel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(w, h);
        f.setVisible(true);
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
        DrawingPanel newPanel = new DrawingPanel(1770, 1080);
        newPanel.setBackground(new Color(75, 75, 75));
        floors.add(newPanel);
        
        addFloorButton("Floor " + floorNumber, floorNumber - 1);
        switchToFloor(floorNumber - 1);
    }

    private static void switchToFloor(int floorIndex) {
        currentFloor = floorIndex;
        mainPanel.remove(1);
        mainPanel.add(floors.get(floorIndex), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void updateTotalArea(int length, int breadth) {
        totalArea += length * breadth/100;
        totalAreaLabel.setText("Total Floor Area: " + totalArea + " sq.ft");
    }
    public static void updateTotalAreaLabel(int area) {
        totalArea -= area;
        totalAreaLabel.setText("Total Floor Area: " + (totalArea) + " sq.ft");
    }
    public static void setzeroArea(){
        totalArea=0;
        totalAreaLabel.setText("Total Floor Area: " + (totalArea) + " sq.ft");
    }
}

