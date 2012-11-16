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
	private int[] opt;
	
	public int index;
	public String[] info;
	public int xCor, yCor;
	public int adjustX;
	public int adjustY;
	public JButton resultBtn;
	public int imgW, imgH;
	public int hitSize = 20;
	public Point(){}
	public String infoTmp;
	public boolean isClick = false;
	
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
		{
			this.infoTmp += info[i];
		}
		
		xCor = this.x + this.offsetX;
		yCor = this.offsetY - (this.y);
		adjustX = 70;
		adjustY = 10;
		this.option = option;
	
	}
	
	public Point(int x, int y, int offsetX, int offsetY, int index, String[] info, int[] option)
	{
		//System.out.print("Create point obj");
		this.x = x;
		this.y = y;
		
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.index = index;
		this.info  = info;
		
		for(int i=0; i<info.length; i++)
		{
			this.infoTmp += info[i];
		}
		
		xCor = this.x + this.offsetX;
		yCor = this.offsetY - (this.y);
		adjustX = 70;
		this.opt = option;
	
	}
	
	public void paint( Graphics g )
	{
		int xPos = xCor - adjustX;
		
		if(this.infoTmp.length() > 1)
		{
			
			if( option == 2 || option == 3 || option == 4)
			{
				if(isClick)
				{
					
					String current = "Current:"+ Math.round(this.y * (10.0f/6.0f));
					String time = "Time:"+ (this.x / 100.0f);
					
					g.setColor(Color.lightGray);
					g.fillRoundRect(xPos - 2, yCor - 50, 120, 40, 10, 10);
					
					g.setColor(Color.blue);
					g.drawChars(current.toCharArray(), 0, current.length(), xPos + 2, yCor - 35);
					g.drawChars(time.toCharArray(), 0, time.length(), xPos + 2, yCor - 15);
				
				}
				else
				{
					g.setColor(Color.blue);
				}
			
				g.fillOval(xPos - 2,yCor- adjustY,hitSize,hitSize);
				
				
			}
		}
		
	}
}

