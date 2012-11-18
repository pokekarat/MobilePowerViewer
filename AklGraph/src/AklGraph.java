import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.*;


public class AklGraph{

    public JPanel createContentPane(int x, int y){
       
        GraphPanel dl = new GraphPanel(x,y);
        dl.setBackground(Color.gray);
        dl.setOpaque(true);    
        return dl;  
        
    }

    private static void createAndShowGUI() {

    	Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int)tk.getScreenSize().getWidth());
        int ySize = ((int)tk.getScreenSize().getHeight()); 
        //System.out.println("Screen size = "+xSize+","+ySize);
                
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Android Power Measurer");
    
        //Create and set up the content pane.
        AklGraph demo = new AklGraph();
        frame.setContentPane(demo.createContentPane(xSize, ySize));
        //frame.setLocation(100, 100);
        //frame.pack();
        
        // The other bits and pieces that make our program a bit more stable.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
        frame.setSize(xSize, ySize);
        frame.setVisible(true);
        
        
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}