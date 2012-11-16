import java.awt.Graphics;


public class Line {
	
	public Point p1;
    public Point p2;
    public int adjustX;
    
    public Line(Point p1, Point p2) 
    {
        this.p1 = p1;
        this.p2 = p2;
        adjustX = 70;
    }
    
    public void paint(Graphics g) 
    {
    
    	
        g.drawLine(p1.xCor - adjustX, p1.yCor, p2.xCor - adjustX, p2.yCor);
    
    }
}
