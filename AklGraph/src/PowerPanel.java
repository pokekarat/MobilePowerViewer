import java.awt.*;
import java.io.IOException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.util.*;
import javax.swing.JScrollBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PowerPanel extends JPanel{
	
	int width = 0;
	int height = 0;
	FileHandle inputFile;
	
	Vector< String[] > powerData;
	Vector< String[] > cpuData;
	Vector< String[] > memData;
	Vector< String[] > reportData;
	Vector< String[] > logcatData;
	Vector<Point> _points;
	Vector<Line> _lines;
	
	int size;
	Font font;
	
	int left = 100;
	int top = 100;
	
	int xScale = 1; 
	int yScale = 40;	
	Dimension d;
	
	JScrollPane sbrText; // Scroll pane for text area
	
	JScrollBar hbar;
	JLabel  mainlb;
	
	public String outputTxt = "";
	
	public PowerPanel(int w, int h,final JTextArea ta, Vector<Point> points, Vector<Line> lines)
	{
		left = w / 20;
		top = h / 20;
		font = new Font("Verdana", Font.BOLD, 16);
		
		this.setLayout(null);
		createElements(left, top);
		_points = points;
		_lines = lines;

		//Add mouse event
		addMouseListener(new MouseAdapter() { 
	         public void mousePressed(MouseEvent me) { 
	        	 ClickButtonInGraph(me.getPoint().x, me.getPoint().y,ta);
	         } 
	     }); 
		
	}

	private void createElements(int left, int top) {
		
		
		//Scrollbar
		hbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 20, 0, 20000); //1000 change viewable horizontal
		hbar.setUnitIncrement(2);
	    hbar.setBlockIncrement(1);
	    hbar.setBounds(left,top * 9 + 22,left * 20 - 270,20);
	    hbar.addAdjustmentListener(new MyAdjustmentListener());
	        
	    mainlb = new JLabel();
	    mainlb.setText("AKL Power Viewer");
	    mainlb.setSize(100,20);
	    mainlb.setBounds(left * 8, top - 25, 110, 20);
	    mainlb.setOpaque(true);
	    mainlb.setBackground(Color.cyan);
	    left = left - 20;
	    this.add(hbar);//, BorderLayout.SOUTH);
	    this.add(mainlb);
		   
	}
	
	int[] chArr = {1,1,1};
	public void ClickButtonInGraph(int clickX, int clickY, JTextArea ta){
		
		 //System.out.println("test click");	
		 for(int i=0; i<_points.size(); i++)
		 {	 
      	   	Point p = _points.elementAt(i);
      	   	int adjx = p.adjustX;
      	   
      	   	String infoTmp = "";
      	   	for(int s=0; s<p.info.length; s++)
      	   		infoTmp += p.info[s];
      	 
      	   	//has info
      	   	if(infoTmp.length() > 1)
      	   	{   
      		   int pX = p.xCor;
      		   int pY = p.yCor - p.adjustY;
      		   
      		   //Which point is clicked
      		   if(clickX > pX - adjx && clickX < pX - adjx + p.hitSize && clickY > pY && clickY < pY + p.hitSize)
      		   {	
      			  p.isClick = true; 
      			  String text = p.info[0];

      			  if(chArr[0] == 1)
      				  text += p.info[1];
      				  
      			  if(chArr[1] == 1)
      				  text += p.info[2];
      			  
      			  if(chArr[2] == 1)
      				  text += p.info[3];
      			
      			outputTxt = text;
      			System.out.println(outputTxt);
      			ta.setText(text);
      			
      		   }
      		   else
      		   {
      			   p.isClick = false;
      		   }
      		   
      		  
      		   this.repaint();
      	    }
         }
	}
	
	int adjustX = 0;
	class MyAdjustmentListener implements AdjustmentListener {
	    public void adjustmentValueChanged(AdjustmentEvent e) {
	      adjustX = e.getValue();
	      repaint();
	      
	    }
	  }
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		d = this.getSize();
		
		
		int right = left * 20 - 200;
		int bottom = top * 10 - 30;

		//draw x axis power graph
		g.drawLine(left , top, right , top);
		g.drawLine(left , bottom , right, bottom );
		
		//draw y axis power graph
		g.drawLine(left,bottom, left, top);
		g.drawLine(right, bottom, right, top);
	
		//draw y number
		for(int i=1; i<= 6; i++)
		{
			yScale = 52;
			//offsetY = 200;
			g.drawString(Integer.toString(i*100), left-45, d.height - top - (i*yScale) + 15 );
			//g.drawLine(offsetX, d.height - offsetY - (i*yScale),1500,d.height - offsetY - (i*yScale));
		}
	
		int panelWidth = right;
		
		//draw x number
		for(int i=0; i<= 200; i++)
		{
			xScale = 1;
			int dist  =  ( left + (i*xScale * 100) ) - adjustX;
			if(dist >= left && dist <= d.width-left)
			{
				g.drawString(Integer.toString(i),dist, d.height-top+15);
			}
		
		}
		
		//Draw power signal
		if(_lines.size() > 0){		
			for(int i=0; i<_lines.size(); i++)
			{
				Line l = _lines.elementAt(i);
				l.adjustX = adjustX + 70;
				int dist = l.p1.xCor - l.adjustX;
				if(dist > left && dist < panelWidth)
					_lines.elementAt(i).paint(g);
			}
		}
		
		/* Draw clickbutton inside power signal */
		if(_points.size() > 0){
			for(int i=0; i<_points.size(); i++)
			{
				Point p = _points.elementAt(i);
				p.adjustX = adjustX + 70;
				
				int dist = p.xCor - p.adjustX;
				
				if(dist > left && dist < panelWidth )
				{
					p.paint(g);
				}	
			}
		}
		
		g.setColor(Color.black);
		//g.setFont(font);
		g.drawString("Current(mA)", left-50, 30); 
		g.drawString("Time(S)", right+10, d.height-top);
	}
}

