import java.awt.*;
/*import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;*/
import javax.swing.JButton;


public class Point {
	
	private int x;
	private int y;
	private int offsetX;
	private int offsetY;
	private int option;
	
	public int index;
	public String[] info;
	public int xCor, yCor;
	public int adjustX;
	public JButton resultBtn;
	public int imgW, imgH;
	public int hitSize = 15;
	public Point(){}
	public String infoTmp;
	
	public Point(int x, int y, int offsetX, int offsetY, int index, String[] info, int option)
	{
		//System.out.print("Create point obj");
		this.x = x;
		this.y = y;
			
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.index = index;
		this.info  = info;
		for(int i=0; i<info.length; i++)
			this.infoTmp += info[i];
		
		xCor = this.x + this.offsetX;
		yCor = this.offsetY - (this.y);
		adjustX = 0;
		this.option = option;
	
	}
	
	public void paint( Graphics g )
	{
		int xPos = xCor - adjustX;
		
		if(this.infoTmp.length() <= 1)
		{
			//g.drawOval(xPos,yCor,2,2);
		}
		else
		{
			//resultBtn.setBounds(xPos-2, yCor-1, imgW, imgH);	
			if(option == 1 || option == 3)
			{
				g.setColor(Color.blue);
				g.fillOval(xPos - 2,yCor-2,hitSize,hitSize);
			}
			else if(option == 2)
			{
				g.setColor(Color.red);
				g.fillRect(xPos - 2,yCor-2,hitSize-3,hitSize-3);
			}
		
		}
		
	}
}

