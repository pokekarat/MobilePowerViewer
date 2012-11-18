import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

//import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JViewport;

import java.io.IOException;
import java.util.*;

import javax.swing.JScrollBar;


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
	JTextArea ta; // Text area
	JScrollPane sbrText; // Scroll pane for text area
	JCheckBox cb1,cb2,cb3;
	JScrollBar hbar;
	JLabel label, reportlb, mainlb;
	PowerPanel powerPanel;
	
	CheckBoxListener myListener = null;
	
	public GraphPanel(int w, int h)
	{
		offsetX = w / 10;
		offsetY = h / 2; //10;
		font = new Font("Verdana", Font.BOLD, 16);
		points = new Vector<Point>();
		this.processData();
		this.addDataToPoint(h);	
		this.setLayout(null);
		createElements(w, h);
	
	}

	private void createElements(int w, int h) {
		
		
	    //Text area
		ta = new JTextArea("", 5, 50);
		
		//ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sbrText.setBounds(offsetX, offsetY + 60, w, 250);		 
		sbrText.setSize(900,250);
		sbrText.getVerticalScrollBar().setValue(0);
		
		//CheckBox 
		int xPos = 220;
		int yPos = 60;
		myListener = new CheckBoxListener();
		cb1 = new JCheckBox();
		cb1.setText("cpu");
		cb1.setSize(80,50);
		cb1.setBounds(w - xPos,offsetY + (yPos * 1), cb1.getSize().width, cb1.getSize().height);
		cb1.setSelected(true);
		cb1.addItemListener(myListener);
		
		cb2 = new JCheckBox();
		cb2.setText("mem");
		cb2.setSize(80,50);
		cb2.setBounds(w - xPos,offsetY + (yPos * 2), cb2.getSize().width, cb2.getSize().height);
	    cb2.setSelected(true);
	    cb2.addItemListener(myListener);
	    
	    cb3 = new JCheckBox();
		cb3.setText("logcat");
		cb3.setSize(80,50);
		cb3.setBounds(w - xPos,offsetY + (yPos * 3), cb3.getSize().width, cb3.getSize().height);
	    cb3.setSelected(true);
	    cb3.addItemListener(myListener);
	    
	    Color c = Color.cyan;
	    //Label
	    label = new JLabel();
	    label.setText("System components");
	    label.setSize(100,20);
	    label.setBounds(w - xPos - 40, offsetY + 27, 120, 20);
	    label.setOpaque(true);
	    label.setBackground(c);
	   
	    //Report Label
	    reportlb = new JLabel();
	    reportlb.setText("Reports");
	    reportlb.setSize(100,20);
	    reportlb.setBounds(offsetX - 40, offsetY + 26, 50, 20);
	    reportlb.setOpaque(true);
	    reportlb.setBackground(c);
	    
	    powerPanel = new PowerPanel(w ,h,ta,points,lines);
	    powerPanel.setOpaque(true);
	    powerPanel.setBackground(Color.white);
	    powerPanel.setBounds(98,10,w - 145,h/2+2);
	    	    
	    //this.add(hbar);//, BorderLayout.SOUTH);
	  	this.add(powerPanel);
	    this.add(sbrText);
	    this.add(cb1);
	    this.add(cb2);
	    this.add(cb3);
	    this.add(reportlb);
	    this.add(label);
	  
	}
	
	int adjustX = 0;
	class MyAdjustmentListener implements AdjustmentListener {
	    public void adjustmentValueChanged(AdjustmentEvent e) {
	      adjustX = e.getValue();
	      repaint();
	    }
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
				String lineZone = "\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n";
				info[0] = ""; //"Report:\n" + reportTxt + "\n----------------\n";
				info[1] = "CPU:\n" + cpuTxt + lineZone;
				info[2] = "Memory:\n"+memTxt + lineZone;
				info[3] = "Logcat:\n"+logcatTxt + lineZone;
			}

			int milSec = 100;
			if(sum % milSec == 0)
			{ 
				int y = (int)(powerAvg / milSec);
				points.add(new Point(x, y, offsetX, offsetY, i,  info, option));
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
	
	int[] chArr = {1,1,1};
	public String[] info = new String[4];
	class CheckBoxListener implements ItemListener {
		
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
	            
	            powerPanel._chArr[0] = chArr[0];
	            powerPanel._chArr[1] = chArr[1];
	            powerPanel._chArr[2] = chArr[2];
	            
	         
    			info[0] = powerPanel.info[1];
    			
    			if(chArr[0] == 1)
    				  info[0] = powerPanel.info[1];
    			else
   				  info[0] = "";
    			  
    			if(chArr[1] == 1)
  				  info[1] = powerPanel.info[2];
    			else
 				  info[1] = "";
	            
    			if(chArr[2] == 1)
  				  info[2] = powerPanel.info[3];
    			else
 				  info[2] = "";
    			
	            ta.setText(info[0]+info[1]+info[2]+info[3]);
	            ta.setSelectionStart(0);
      			ta.setSelectionEnd(0);
	        }
	    }
	 
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		d = this.getSize();
		
		//Draw checkboxs frame
		g.drawLine(d.width - 250 , d.height-offsetY+80  , d.width - 40  , d.height-offsetY+80); // top
		g.drawLine(d.width - 250 , d.height-offsetY+350 , d.width - 40  , d.height-offsetY+350); // bottom
		g.drawLine(d.width - 250 , d.height-offsetY+80  , d.width - 250 , d.height-offsetY+350); //left
		g.drawLine(d.width - 40  , d.height-offsetY+80  , d.width - 40  , d.height-offsetY+350); //right
		
		//Draw message frame
		g.drawLine(offsetX-40 , d.height/2 + 63, d.width -300, d.height/2 + 63); // top
		g.drawLine(offsetX-40 , d.height/2 + 335, d.width -300, d.height/2 + 335); // bottom
		g.drawLine(offsetX-40 , d.height/2 + 63, offsetX-40, d.height/2 + 335); // left
		g.drawLine(d.width -300 , d.height/2 + 63, d.width -300, d.height/2 + 335);
		
		
	}
}

