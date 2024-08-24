import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;


public class DrawingPanel extends JPanel {
    private java.util.List<Shape> shapes = new java.util.ArrayList<>();
    private Shape draggedShape = null;
    private Point previousPoint;
    public DrawingPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Shape s : shapes) {
                    if (s.contains(e.getPoint())) {
                        draggedShape = s;
                        previousPoint = e.getPoint();
                        break;
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

class Shape {
    int width, height;
    int x, y;
    int x_label, y_label;
    private Color color;
    private String room_label;

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
