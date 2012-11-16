import java.awt.*;
import java.io.IOException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

//import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.*;
import javax.swing.JScrollBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GraphPanel extends JPanel{
	
	int width = 0;
	int height = 0;
	FileHandle inputFile;
	
	Vector< String[] > powerData;
	Vector< String[] > cpuData;
	Vector< String[] > memData;
	Vector< String[] > reportData;
	Vector< String[] > logcatData;
	Vector<Point> points;
	Vector<Line> lines;
	
	int size;
	Font font;
	
	int offsetX = 100;
	int offsetY = 100;
	
	int xScale = 1; 
	int yScale = 40;	
	Dimension d;
	//JLabel label;
	JTextArea ta; // Text area
	JScrollPane sbrText; // Scroll pane for text area
	JCheckBox cb1,cb2,cb3;
	JScrollBar hbar;
	
	CheckBoxListener myListener = null;
	
	public GraphPanel(int w, int h)
	{
		offsetX = w / 10;
		offsetY = h / 2; //10;
		//System.out.println("offsetY = "+offsetY);
		
		this.setLayout(null);
		createElements(w, h);
		
		//System.out.println(width+","+height);
		font = new Font("Verdana", Font.BOLD, 14);
		
		points = new Vector<Point>();
		//vec.remove(0); //delete excel header
		
		this.processData();
		this.addDataToPoint(h);	
		
		//Add mouse event
		addMouseListener(new MouseAdapter() { 
	         public void mousePressed(MouseEvent me) { 
	        	 //System.out.println(me.getPoint().x + "," + me.getPoint().y);
	        	 TestClick(me.getPoint().x, me.getPoint().y);
	         } 
	     }); 
		
	}

	private void createElements(int w, int h) {
		
		//Scrollbar
		hbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 20, 0, 20000); //1000 change viewable horizontal
		hbar.setUnitIncrement(2);
	    hbar.setBlockIncrement(1);
	    hbar.addAdjustmentListener(new MyAdjustmentListener());
	    hbar.setBounds(offsetX-40,h/2,w-offsetX-10,20);
	    
	    //label
		/*
	    label = new JLabel();
		label.setFont(new Font("Serif", Font.BOLD, 16));
		label.setForeground(Color.black);
		label.setSize(200,200);
		label.setBounds(offsetX, offsetY + 50, w, 250);
		*/
	    
		 ta = new JTextArea("", 5, 50);
		 
		 //ta.setLineWrap(true);
		 sbrText = new JScrollPane(ta);
		 sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);       
		 sbrText.setBounds(offsetX, offsetY + 60, w, 250);		 
		 sbrText.setSize(900,250);
		 
		
		 myListener = new CheckBoxListener();
		
		//CheckBox
		cb1 = new JCheckBox();
		cb1.setText("cpu");
		cb1.setSize(80,50);
		cb1.setBounds(w - 200,offsetY + 50, cb1.getSize().width, cb1.getSize().height);
		cb1.setSelected(true);
		cb1.addItemListener(myListener);
		
		cb2 = new JCheckBox();
		cb2.setText("mem");
		cb2.setSize(80,50);
		cb2.setBounds(w - 200,offsetY + 100, cb2.getSize().width, cb2.getSize().height);
	    cb2.setSelected(true);
	    cb2.addItemListener(myListener);
	    
	    cb3 = new JCheckBox();
		cb3.setText("logcat");
		cb3.setSize(80,50);
		cb3.setBounds(w - 200,offsetY + 150, cb3.getSize().width, cb3.getSize().height);
	    cb3.setSelected(true);
	    cb3.addItemListener(myListener);
	   
	    
	    this.add(hbar);//, BorderLayout.SOUTH);fqe
	    //this.add(label);//, BorderLayout.NORTH);
	    this.add(sbrText);
	    this.add(cb1);
	    this.add(cb2);
	    this.add(cb3);
	}
	
	public void processData(){
		
		try {
		
			//power data
			inputFile = new FileHandle("data\\power.csv");
			powerData = inputFile.getData(",");
			
			//cpu usage
			inputFile = new FileHandle("data\\cpu_usage.log");
			cpuData = inputFile.getData(" ");
			
			//memory usage
			inputFile = new FileHandle("data\\memoryUsage.log");
			memData = inputFile.getData(" ");
			
			//akl report
			inputFile = new FileHandle("data\\report.log");
			reportData = inputFile.getData(" ");
			
			//dalvik
			inputFile = new FileHandle("data\\dalvik.logcat");
			logcatData = inputFile.getData(" ");
			
			
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}	
	}
	
	public void addDataToPoint(int h){
		
		String[] report = null;
		long baseTime = 0;
		long timeAdj = 37500;
		float powerAvg = 0;
		long reportSize = reportData.size();
		
		//Create base time
		if(reportSize > 0) {
			report = reportData.get(0);
			String[] timeArrs = report[1].split(":");
			baseTime = convertTime(timeArrs);
		}
		
		Hashtable<Long, String> reportHash 	= 	addHashTable(	baseTime, timeAdj, 	reportData	);
		Hashtable<Long, String> cpuHash 	= 	addHashTable(	baseTime, timeAdj,	cpuData		);
		Hashtable<Long, String> memHash 	= 	addHashTable(	baseTime, timeAdj,	memData		);
		Hashtable<Long, String> logcatHash 	= 	addHashTable2(	baseTime, timeAdj,	logcatData	);
		
		long sum = 0;
		size = powerData.size();
		for(int i=0; i<size; i++)
		{
			String[] data = powerData.get(i);
			String[] info = new String[4];//akl, cpu and mem
			
			int x =  (int)(Float.parseFloat(data[0]) * xScale * 100); // 100 HZ
			//int y =  (int)(Float.parseFloat(data[1]) * 0.6f);
			
			powerAvg += (Float.parseFloat(data[1]) * 0.6f);
			
			int option = 0;
			int[] opt = {0,0,0,0};
			
			String reportTxt = "";
			if(reportHash.containsKey(sum))
			{
				reportTxt = reportHash.get(sum).toString();
				option = 1;
				opt[0] = 1;
			}
			
			String cpuTxt = "";
			if(cpuHash.containsKey(sum))
			{
				cpuTxt = cpuHash.get(sum).toString();
				option = 2;
				opt[1] = 1;
			}
			
			String memTxt = "";
			if(memHash.containsKey(sum))
			{
				memTxt = memHash.get(sum).toString();
				option = 3;
				opt[2] = 1;
			}
			
			String logcatTxt = "";
			if(logcatHash.containsKey(sum))
			{
				logcatTxt = logcatHash.get(sum).toString();
				option = 4;
				//System.out.println(logcatTxt);
				opt[3] = 1;
			}
			
			if(reportTxt.isEmpty() && cpuTxt.isEmpty() && memTxt.isEmpty() && logcatTxt.isEmpty())
			{
				info[0] = reportTxt;
				info[1] = cpuTxt;
				info[2] = memTxt;
				info[3] = logcatTxt;
				
			}
			else
			{
				info[0] = "Report:\n" + reportTxt + "\n----------------\n";
				info[1] = "CPU:\n" + cpuTxt + "\n----------------\n";
				info[2] = "Memory:\n"+memTxt + "\n----------------\n";
				info[3] = "Logcat:\n"+logcatTxt + "\n----------------\n";
			}

			int milSec = 100;
			if(sum % milSec == 0)
			{ // create point with average every 20 ms (100/5000)
			
				/*if(option == 1)
				{
					System.out.println("test");
				}*/
				
				int y = (int)(powerAvg / milSec);
				points.add(new Point(x, y, offsetX, offsetY, i,  info, option));
				//System.out.println("x:"+x+",y:"+y);
				//points.add(new Point(x, y, offsetX, offsetY, i,  info, opt));
				powerAvg = 0;
			}
			++sum;
		}
		
		//Add points to line
	    lines = new Vector<Line>();
		for(int i=0; i<points.size()-1; i++)
		{
			Point p1 = points.elementAt(i);
			Point p2 = points.elementAt(i+1);
			Line line = new Line(p1, p2);
			lines.add(line);
		}
	}

	private Hashtable<Long, String> addHashTable(long baseTime, long timeAdj,Vector<String[]> vec) {
		
		Hashtable<Long, String> hash = new Hashtable<Long, String>();
		String info = "";
		String[] data = null;
		
		long tmp = 1;
		long size = vec.size();

		for(int i=0; i<size; i++)
		{	
			data = vec.get(i);
			String[] timeArrs = data[1].split(":");
			long count = convertTime(timeArrs) - baseTime;
			
			String infoTmp = "";
			int dataLen = data.length;
			for(int d=2; d<dataLen; d++)
			{
				infoTmp += data[d] + " ";
			}
			
			if(tmp == count)
			{
				info += infoTmp + "\n";
				tmp = count;
			}
			else
			{
				long x = ( tmp * 5000 ) + timeAdj;
				hash.put(x, info);
				i--;
				tmp = count;
				info = "";
			}
			
			if(i == size - 1){
				long x = ( tmp * 5000 ) + timeAdj;
				hash.put(x, info);
			}
		}
		
		return hash;
	
	}
	
	private Hashtable<Long, String> addHashTable2(long baseTime, long timeAdj,Vector<String[]> vec) {
		
		Hashtable<Long, String> hash = new Hashtable<Long, String>();
		String info = "";
		String[] data = null;
		
		long tmp2 = 1;
		long size = vec.size();

		for(int i=0; i<size; i++)
		{	
			data = vec.get(i);
			String[] timeArrs = data[1].split(":");
			String[] tmpTime = timeArrs[2].split("\\.");
			timeArrs[2] = tmpTime[0];
			
			long count = convertTime(timeArrs) - baseTime;
			
			if(count < 0) continue;
			
			String infoTmp = "";
			int dataLen = data.length;
			for(int d=2; d<dataLen; d++)
			{
				infoTmp += data[d] + " ";
			}
			
			if(tmp2 == count)
			{
				info += infoTmp + "\n";
				tmp2 = count;
			}
			else
			{
				long x = ( tmp2 * 5000 ) + timeAdj;
				hash.put(x, info); // 15000 is milliseconds offset 3 sec
				i--;
				tmp2 = count;
				info = "";
			}
			
			if(i == (size-1)){
				long x = ( tmp2 * 5000 ) + timeAdj;
				hash.put(x, info); // 15000 is milliseconds offset 3 sec
			}
		}
		
		return hash;
	
	}
	
	public int convertTime(String[] timeArrs){
		return (Integer.parseInt(timeArrs[0]) * 3600) + (Integer.parseInt(timeArrs[1]) * 60 ) + Integer.parseInt(timeArrs[2]);
		
	}
	
	public void TestClick(int clickX, int clickY){
		
		 //System.out.println("test click");	
		 for(int i=0; i<points.size(); i++)
		 {	 
      	   	Point p = points.elementAt(i);
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
	
	int[] chArr = {1,1,1};
	class CheckBoxListener implements ItemListener {
		
			/*
			public GraphPanel _gp;
			
			public CheckBoxListener(GraphPanel gp)
			{
				_gp = gp;
			}
			*/
	        public void itemStateChanged(ItemEvent e) 
	        {
	        
	        	Object source = e.getSource();
	            
	            if (source == cb1) {
	            	chArr[0] = 1;
	            } else if (source == cb2) {
	                chArr[1] = 1;
 	            } else if (source == cb3) {
 	            	chArr[2] = 1;
 	            }

	            if (e.getStateChange() == ItemEvent.DESELECTED){
	            	 if (source == cb1) {
	            		 chArr[0] = 0;
	 	            } else if (source == cb2) {
	 	            	chArr[1] = 0;
	 	            } else if (source == cb3) {
	 	            	chArr[2] = 0;
	 	            } 
	            	
	            }
	            
	            System.out.println(chArr[0]+","+chArr[1]+","+chArr[2]);
	            
	            //sbrText.repaint();
	            //ta.repaint();
	            //System.out.println(this);
	            //_gp.repaint();
	            
	        }
	    }
	 
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		d = this.getSize();
		
		//draw x axis power graph
		g.drawLine(offsetX , d.height-offsetY, d.width - offsetX, d.height-offsetY);
		
		//draw y axis power graph
		g.drawLine(offsetX, d.height-offsetY, offsetX, 20);
		//System.out.println("d.height = "+ d.height);
		
		//Draw x,y checkboxs
		g.drawLine(d.width - 250 , d.height-offsetY+80, d.width - 40, d.height-offsetY+80); // top
		g.drawLine(d.width - 250 , d.height-offsetY+350, d.width - 40, d.height-offsetY+350); // bottom
		g.drawLine(d.width - 250, d.height-offsetY+80,d.width - 250, d.height-offsetY+350); //left
		g.drawLine(d.width - 40,d.height-offsetY+80, d.width - 40, d.height-offsetY+350); //right
		
		//x,y message
		g.drawLine(offsetX-40 , d.height/2 + 63, d.width -300, d.height/2 + 63); // top
		g.drawLine(offsetX-40 , d.height/2 + 335, d.width -300, d.height/2 + 335); // bottom
		g.drawLine(offsetX-40 , d.height/2 + 63, offsetX-40, d.height/2 + 335); // left
		g.drawLine(d.width -300 , d.height/2 + 63, d.width -300, d.height/2 + 335);
		
		g.setFont(font);
		g.drawString("Current(mA)", offsetX-50, 20);
		g.drawString("Time (sec)", d.width-offsetX+10, d.height-offsetY);
		
		//draw y number
		for(int i=1; i<= 8; i++)
		{
			yScale = 52;
			//offsetY = 200;
			g.drawString(Integer.toString(i*100), offsetX-40, d.height - offsetY - (i*yScale) + 15 );
			//g.drawLine(offsetX, d.height - offsetY - (i*yScale),1500,d.height - offsetY - (i*yScale));
		}
	
		int panelWidth = d.width - offsetX;
		
		//draw x number
		for(int i=0; i<= 200; i++)
		{
			xScale = 1;
			int dist  =  ( offsetX + (i*xScale * 100) ) - adjustX;
			if(dist >= offsetX && dist <= d.width-offsetX)
			{
				g.drawString(Integer.toString(i),dist, d.height-offsetY+20);
				//g.drawLine(dist, d.height-offsetY+20, dist, -50);
			}
		
		}
		
		for(int i=0; i<lines.size(); i++)
		{
			Line l = lines.elementAt(i);
			l.adjustX = adjustX;
			
			int dist = l.p1.xCor - l.adjustX;
			if(dist > offsetX && dist < panelWidth)
				lines.elementAt(i).paint(g);
		}
		
		for(int i=0; i<points.size(); i++)
		{
			Point p = points.elementAt(i);
			p.adjustX = adjustX;
			
			int dist = p.xCor - p.adjustX;
			
			if(dist > offsetX && dist < panelWidth )
			{
				p.paint(g);
			}	
		}
	}
}

