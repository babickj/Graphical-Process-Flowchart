import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.event.*;

/*SUMMARY:
1. Read stochastic model (in a graph format) from
a .csv file.
2. Display model to the screen
3. Creates a new process
4. Tracks process through the flow path
5. Outputs the time to complete the process

SETTINGS:
1. Annimation:  Used to show flow path for user
2. Stochastic:  Used to run n-trials to get sample data
  
*/
public class Run_Model extends JPanel implements ActionListener{
 
    
    public static void main(String[] args) {

	FlowPath myFlowPath = new FlowPath(50,200,500);
	JScrollPane scrollpane = new JScrollPane(myFlowPath);//new FlowPath(50,200,500));
        scrollpane.getViewport().setPreferredSize(new Dimension(800, 800));
        JFrame frame = new JFrame("TTFR");
	JButton saveButton = new JButton("SAVE");
saveButton.addActionListener(new ActionListener()
{
    //This method is called then the button is pushed
  public void actionPerformed(ActionEvent e)
  {
      //Calls the save model method
      myFlowPath.saveModel();
      System.out.println(myFlowPath.newWidget[0].name);
  }
});
        frame.getContentPane().add(scrollpane,BorderLayout.CENTER);
	frame.getContentPane().add(saveButton,BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    @Override
     public void actionPerformed(ActionEvent e) {
	 //numClicks++;
	 System.out.println("Button Clicked 7  times");
        }
    
}
