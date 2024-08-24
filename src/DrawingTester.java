import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class DrawingTester {
    private static double totalArea = 0; 
    private static JLabel totalAreaLabel;
    public static void main(String[] args) {
        int w = 1920;
        int h = 1080;
        JFrame f = new JFrame("BubbleGum");
        DrawingPanel panel = new DrawingPanel(w, h);
        totalAreaLabel = new JLabel("Total Floor Area: 0");
        totalAreaLabel.setBounds(10, 10, 200, 30);
        panel.add(totalAreaLabel);
        CircleButton circleButton = new CircleButton("+");
		circleButton.addActionListener(new ActionListener(){
			@Override	
            public void actionPerformed(ActionEvent e){
                    String lengthString = JOptionPane.showInputDialog(f,"Enter the length of box:");
                    String breadthString = JOptionPane.showInputDialog(f,"Enter the breadth of box");
                    String roomLabel = JOptionPane.showInputDialog(f, "Enter the room label:");
                    if(lengthString!=null && breadthString!=null){
                        try{
                            int length = Integer.parseInt(lengthString);
                            int breadth = Integer.parseInt(breadthString);
                            if(length>0 && breadth>0){
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
        circleButton.setBounds(1800,525,50,50);
        panel.add(circleButton);
        f.setContentPane(panel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
    private static void updateTotalArea(int length, int breadth) {
        totalArea += length * breadth;
        totalAreaLabel.setText("Total Floor Area: " + totalArea);
    }
}

