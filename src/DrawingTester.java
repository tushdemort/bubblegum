import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class DrawingTester {
    public static void main(String[] args) {
        int w = 1920;
        int h = 1080;
        JFrame f = new JFrame("BubbleGum");
        DrawingPanel panel = new DrawingPanel(w, h);
        CircleButton circleButton = new CircleButton("+");
		circleButton.addActionListener(new ActionListener(){
			@Override	
            public void actionPerformed(ActionEvent e){
                    panel.addShape(new Shape(100, 150, w/2, h/3, 159, 69, 219,"newroom"));
                    panel.addedShape();
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
}

