
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class Line {
	
	public Point p1;
    public Point p2;
    public int adjustX;
    
    public Line(){}
    
    public Line(Point p1, Point p2) 
    {
        this.p1 = p1;
        this.p2 = p2;
        adjustX = 70;
    }
    
    public void paint(Graphics g) 
    {
    
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(p1.xCor - adjustX, p1.yCor, p2.xCor - adjustX, p2.yCor);
    
    }
}
