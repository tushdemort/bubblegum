import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.awt.Font;

public class CircleButton extends JButton{

	private boolean mouseOver = false;
	private boolean mousePressed = false;
	public CircleButton(String text){
		super(text);
		setOpaque(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setFont(new Font("Arial", Font.PLAIN, 24));
		MouseAdapter mouseListener = new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent me){
				if(contains(me.getX(), me.getY())){
					mousePressed = true;
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent me){
				mousePressed = false;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent me){
				mouseOver = false;
				mousePressed = false;
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent me){
				mouseOver = contains(me.getX(), me.getY());
				repaint();
			}
		};

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);		
	}

	private int getDiameter(){
		int diameter = Math.min(getWidth(), getHeight())-10;
		return diameter;
	}

	@Override
	public Dimension getPreferredSize(){
		FontMetrics metrics = getGraphics().getFontMetrics(getFont());
		int minDiameter = 10 + Math.max(metrics.stringWidth(getText()), metrics.getHeight());
		return new Dimension(minDiameter, minDiameter);
	}

	@Override
	public boolean contains(int x, int y){
		int radius = getDiameter()/2;
		return Point2D.distance(x, y, getWidth()/2, getHeight()/2) < radius;
	}

	@Override
	public void paintComponent(Graphics g){
		int diameter = getDiameter();
		int radius = diameter/2;
		int offset = 5;
		Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient;
        if (mousePressed) {
            gradient = new GradientPaint(0, 0, new Color(141, 158, 154), 0, getHeight(), new Color(141, 160, 154));
        } else if (mouseOver) {
            gradient = new GradientPaint(0, 0, new Color(166, 182, 219), 0, getHeight(), new Color(166, 182, 219));
        } else {
            gradient = new GradientPaint(0, 0, new Color(151, 196, 182), 0, getHeight(), new Color(151, 196, 182));
        }
        g2d.setPaint(gradient);
        g2d.fillOval(getWidth() / 2 - radius, getHeight() / 2 - radius, diameter, diameter);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(getWidth() / 2 - radius, getHeight() / 2 - radius, diameter, diameter);

        if (!mousePressed) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(getWidth() / 2 - radius + 3, getHeight() / 2 - radius + 3, diameter, diameter);
        }
       	g.setFont(getFont());	
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        int stringWidth = metrics.stringWidth(getText());
        int stringHeight = metrics.getHeight();
        g2d.setColor(Color.BLACK);
        g2d.drawString(getText(), getWidth() / 2 - stringWidth / 2, getHeight() / 2 + stringHeight / 4);
	}
}