import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class DashedBorderPanel extends JPanel {
    // A Map to store the type of modification (gap or dashed) for each side
    private Map<String, String> sideModification = new HashMap<>();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set antialiasing for smooth drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Default solid black border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2)); // Solid black border
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Check each side for gaps or dashed lines based on the map
        g2d.setColor(Color.WHITE); // Set color to white for gaps (or choose dash pattern for windows)
        g2d.setStroke(new BasicStroke(2)); // Standard stroke width

        // Check the top side
        if (sideModification.getOrDefault("Top", "").equals("Gap")) {
            g2d.drawLine(getWidth() / 4, 0, getWidth() * 3 / 4, 0); // Gap at top
        } else if (sideModification.getOrDefault("Top", "").equals("Window")) {
            // Dashed line for window at top
            float[] dashPattern = { 10, 5 }; // Dash pattern (10px dash, 5px space)
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(getWidth() / 4, 0, getWidth() * 3 / 4, 0); // Dashed line at top
        }

        // Check the bottom side
        if (sideModification.getOrDefault("Bottom", "").equals("Gap")) {
            g2d.drawLine(getWidth() / 4, getHeight() - 1, getWidth() * 3 / 4, getHeight() - 1); // Gap at bottom
        } else if (sideModification.getOrDefault("Bottom", "").equals("Window")) {
            // Dashed line for window at bottom
            float[] dashPattern = { 10, 5 }; // Dash pattern (10px dash, 5px space)
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(getWidth() / 4, getHeight() - 1, getWidth() * 3 / 4, getHeight() - 1); // Dashed line at bottom
        }

        // Check the left side
        if (sideModification.getOrDefault("Left", "").equals("Gap")) {
            g2d.drawLine(0, getHeight() / 4, 0, getHeight() * 3 / 4); // Gap on left side
        } else if (sideModification.getOrDefault("Left", "").equals("Window")) {
            // Dashed line for window on left side
            float[] dashPattern = { 10, 5 }; // Dash pattern (10px dash, 5px space)
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(0, getHeight() / 4, 0, getHeight() * 3 / 4); // Dashed line on left side
        }

        // Check the right side
        if (sideModification.getOrDefault("Right", "").equals("Gap")) {
            g2d.drawLine(getWidth() - 1, getHeight() / 4, getWidth() - 1, getHeight() * 3 / 4); // Gap on right side
        } else if (sideModification.getOrDefault("Right", "").equals("Window")) {
            // Dashed line for window on right side
            float[] dashPattern = { 10, 5 }; // Dash pattern (10px dash, 5px space)
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
            g2d.drawLine(getWidth() - 1, getHeight() / 4, getWidth() - 1, getHeight() * 3 / 4); // Dashed line on right
                                                                                                // side
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dashed Border Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        DashedBorderPanel panel = new DashedBorderPanel();
        panel.setBackground(Color.WHITE);

        // Creating the right-click (popup) menu
        JPopupMenu popupMenu = new JPopupMenu();

        // Add options to the popup menu for each side (Top, Bottom, Left, Right)
        JMenuItem topGap = new JMenuItem("Top: Door");
        JMenuItem topWindow = new JMenuItem("Top: Window");
        JMenuItem bottomGap = new JMenuItem("Bottom: Door");
        JMenuItem bottomWindow = new JMenuItem("Bottom: Window");
        JMenuItem leftGap = new JMenuItem("Left: Door");
        JMenuItem leftWindow = new JMenuItem("Left: Window");
        JMenuItem rightGap = new JMenuItem("Right: Door");
        JMenuItem rightWindow = new JMenuItem("Right: Window");
        JMenuItem resetGap = new JMenuItem("Reset Modifications");

        // Action listeners for each menu item to add the corresponding modification
        topGap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Top", "Gap"); // Set gap for top
                panel.repaint(); // Repaint panel to update
            }
        });

        topWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Top", "Window"); // Set dashed line for top
                panel.repaint(); // Repaint panel to update
            }
        });

        bottomGap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Bottom", "Gap"); // Set gap for bottom
                panel.repaint(); // Repaint panel to update
            }
        });

        bottomWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Bottom", "Window"); // Set dashed line for bottom
                panel.repaint(); // Repaint panel to update
            }
        });

        leftGap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Left", "Gap"); // Set gap for left
                panel.repaint(); // Repaint panel to update
            }
        });

        leftWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Left", "Window"); // Set dashed line for left
                panel.repaint(); // Repaint panel to update
            }
        });

        rightGap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Right", "Gap"); // Set gap for right
                panel.repaint(); // Repaint panel to update
            }
        });

        rightWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.put("Right", "Window"); // Set dashed line for right
                panel.repaint(); // Repaint panel to update
            }
        });

        resetGap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.sideModification.clear(); // Remove all modifications
                panel.repaint(); // Repaint panel to reset modifications
            }
        });

        // Add the menu items to the popup menu
        popupMenu.add(topGap);
        popupMenu.add(topWindow);
        popupMenu.add(bottomGap);
        popupMenu.add(bottomWindow);
        popupMenu.add(leftGap);
        popupMenu.add(leftWindow);
        popupMenu.add(rightGap);
        popupMenu.add(rightWindow);
        popupMenu.addSeparator();
        popupMenu.add(resetGap);

        // Set up the right-click listener for the panel
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}