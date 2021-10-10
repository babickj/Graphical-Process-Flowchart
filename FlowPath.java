import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.io.*; 
import java.util.*;
import static java.awt.geom.AffineTransform.*;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class FlowPath extends JPanel implements MouseMotionListener, MouseListener
{
    private static final int SPA_WIDTH = 2000;
    private static final int SPA_HEIGHT = SPA_WIDTH;
   
    boolean startup = true;
    
    Widget[] newWidget = new Widget[100];
    Process[] processList = new Process[100];
    Process newProcess = new Process();
    
    int corner = 0;
    int center;
    int xDim;
    int yDim;
    int xStep;
    int yStep;
    boolean isComplete = false;
    int numSteps = 1;
    int xPos;
    int yPos;

    double sumProcess;
    
    //CONSTRUCTOR
    //Used to initialize the model
    public FlowPath(int corner, int scale, int center)
    {
	//Sets the corner of the model
	this.xPos = center;
	this.yPos = corner;
	this.xStep = 200;
	this.yStep = 100;
	this.corner = corner;
	this.center = center;
	this.xDim = scale / 2;
	this.yDim = scale / 4;
	this.isComplete = false;
	this.sumProcess = 0;

	LoadModel();
	addMouseMotionListener(this);
	addMouseListener(this);
	//this.add(new JButton("TEST"),BorderLayout.NORTH);

    }//end CONSTUCTOR

    public void paint(Graphics graph)
    {
	paintComponent(graph);
    }
    
    //SUMMARY: This method controls the graphs
    //it is called 60 times / sec This method builds the model flowpath
    //INPUTS:  The Graph
    //OUTPUTS: Outputs to screen
    @Override
    public void paintComponent(Graphics graph)
    {
       
	double tempProcess = 0;
	double tempSum = 0;

	super.paintComponent(graph);
	//graph.setColor(Color.red);
	//graph.drawLine(0,0,getWidth(),getHeight());
	//Calls the method to build the flow path
	//This displays the model to the screen
	buildFlowPath(graph);

	//This method shows the Widget's Path
	for(int i = newWidget.length - 1; i >= 0; i--)
	    {
		//If process is active, display on screne
		if(newWidget[i] != null && newWidget[i].isActive == true)
		    {
			//for loop shows each active process
			for(int j = 0; j < newWidget[i].children.size(); j++)
			    {
				showFlow(graph, i, newWidget[i].children.get(j), newWidget[i].myProcess.get(j));
			    }//end for loop that show each active process

			
		    }//end if that shows process for active widgets
	    }//end for loop that displays active widgets		

	sumProcess = getCriticalPath(newWidget[0]);
	//causes the animation
	repaint();

    }//end buildFlowPath method

    @Override
    public Dimension getPreferredSize()
    {
	return new Dimension(SPA_WIDTH, SPA_HEIGHT);
    }
    
    //SUMMARY: This method builds the model flowpath
    //INPUTS:  The graph to build on
    //OUTPUTS: None
    public void buildFlowPath(Graphics graph)
    {
	int sibling = 0;
	int stringSpacing = 10;
	super.paintComponent(graph);
        //this.setBackground(Color.WHITE);

	//Sets the color for the node
	graph.setColor(Color.BLACK);
	graph.drawString("Time: "+sumProcess,50,50);
	
	//This for loop builds the model
   	for(int i = 0; i < newWidget.length; i++)
	    {
		//This if statement ensure node is not null
		if (newWidget[i] != null)
		    {
			//Draws milestone
			if(newWidget[i].isCritPath == true)
			    {
				graph.setColor(Color.RED);
				
				//draws the widget
				newWidget[i].drawWidget(graph, this.xDim, this.yDim);
				//draw(newWidget[i].r);
				graph.setColor(Color.BLACK);
			    }
			else
			    {
				//draws the widget
				newWidget[i].drawWidget(graph, this.xDim, this.yDim);
			    }

			//Draws Node Labels
			Font font = new Font("Serif", Font.PLAIN, 12);
			    
			graph.setFont(font);
			graph.drawString(newWidget[i].name, newWidget[i].xPos+stringSpacing, newWidget[i].yPos+(stringSpacing));
			graph.drawString(newWidget[i].type,newWidget[i].xPos+stringSpacing, newWidget[i].yPos+(2*stringSpacing));
			graph.drawString("Mean: "+newWidget[i].mean, newWidget[i].xPos+stringSpacing, newWidget[i].yPos + (3*stringSpacing));
			graph.drawString("Var: "+newWidget[i].var,newWidget[i].xPos+stringSpacing, newWidget[i].yPos+(4*stringSpacing));
			
			for (int j = 0; j < newWidget[i].children.size(); j++)
			    {
				int cIndex; //holds index for child / readable code
				cIndex = newWidget[i].children.get(j);

				//Ensures model is consistend
				if(newWidget[cIndex] != null)
				    {
	        
					drawArrow(graph, newWidget[i].xPos+(this.xDim)/2, newWidget[i].yPos+yDim, newWidget[cIndex].xPos+(this.xDim/2),newWidget[cIndex].yPos);
				    }
				else
				    {
					System.err.println("Model Error");
					System.err.println("Node: "+ j);
					System.err.println("Child "+cIndex);
					System.err.println("Child node non-existent");
				    }//end if else to ensure model is consistent
		        
			    }//end for loop that draws connectors.
			    
		    }//end if that ensure node is not null;
	    }//end outter for loop
		
    }//End build Flow Path


    //SUMMARY: Displays widgets moving to each node
    //INPUTS: Graphic
    //OUTPUT: Widgets moving
    public void showFlow(Graphics graph, int index, int childIndex, Process newProcess)
    {
	double tempProcess = 0;
	
	//sets process position
	if(newWidget[index] != null)
	    {
		
		//Moves process toward next milestone
		//MOVE in X-direction
		if(newProcess.xPos < newWidget[childIndex].xPos)
		    {
			newProcess.xPos += 1;
		    }//end if to move in x-direction
		else if(newProcess.xPos > newWidget[childIndex].xPos)
		    {
			newProcess.xPos -= 1;
		    }//end move processin x direction
		
		//MOVE in Y-direction
		if(newProcess.yPos < newWidget[childIndex].yPos)
		    {
			newProcess.yPos += 1;
		    }//end if to move in Y-direction

		//increment index once at next milestone
		if(newProcess.xPos >= newWidget[childIndex].xPos && newProcess.yPos >= newWidget[childIndex].yPos)
		    {
			//This code increments time in process
			double tempTime = 0;
			double tempMean = newWidget[index].mean;
			double tempVar = newWidget[index].var;
			switch(newWidget[index].type)
			    {
			    case "Norm":
				newProcess.time += newProcess.Normal(tempMean,tempVar);				
				break;
				
			    case "Exp":
				newProcess.time += newProcess.ExpDist(tempMean, tempVar);
				break;
			    default:
				System.err.println("Process: "+ newWidget[index].name + " undefined distribution- " + newWidget[index].type);
				System.err.println("Used " + newWidget[index].mean);
				newProcess.time += newWidget[index].mean;
			   
			    }//end switch that checks distribution type

			//This for loop activates the children widgets
			newWidget[index].isActive = false;
		       
	        
			for(int i = 0; i < newWidget[index].children.size();i++ )
			    {
				int tempIndex = newWidget[index].children.get(i);
				//newWidget[index].myProcess.get(i) = newProcess;
				boolean notActive = true;
				//Verifies all parents are complete
				for(int j = 0; j < newWidget[childIndex].parents.size(); j++)
				    {
					int temp2 = newWidget[childIndex].parents.get(j);
					
					if(newWidget[temp2].isActive == true)
					    {
						notActive = false;
					    }
				    }//end for loop verifying parents are done
				if(notActive)
				    {
					newWidget[tempIndex].isActive = true;
				
				    }				    
			    }//ends for loop that update childe nodes
	        
		    }//end if that increments process
		
	    }//end if statement to ensure we don't go out of bounds
	
	//draws the process on the model
	graph.setColor(Color.GREEN);
	graph.fillRect(newProcess.xPos, newProcess.yPos, 10, 10);
	    
    }//End method showing flow

    //Reads a CSV file
    public void LoadModel() {

        String csvFile = "input.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
	int index = 0;
	
        try {

            br = new BufferedReader(new FileReader(csvFile));
	    line = br.readLine(); //throws away csv header
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

		//Loads block into model
		//gets index for block
		index = Integer.parseInt(country[0]);

		//Creates new widget from file
		if(newWidget[index] == null)
		    {
			newWidget[index] = new Widget();
		    }
		newWidget[index].index = Integer.parseInt(country[0]);
		newWidget[index].name = country[1];
		newWidget[index].xPos = Integer.parseInt(country[2]);
		newWidget[index].yPos = Integer.parseInt(country[3]);
		newWidget[index].type = country[4];
		newWidget[index].mean = Double.parseDouble(country[5]);
		newWidget[index].var = Double.parseDouble(country[6]);
		
		//this for loop populates node children
		for(int i = 7; i < country.length; i++)
		    {
			//System.out.println(country[i]);
			int temp = Integer.parseInt(country[i]);
			newWidget[index].children.add(temp);
			newWidget[temp] = new Widget();
			//newWidget[temp].parents.add(index);
			newWidget[index].myProcess.add(new Process());
			int tt = newWidget[index].myProcess.size() - 1;
			newWidget[index].myProcess.get(tt).xPos = newWidget[index].xPos;
			newWidget[index].myProcess.get(tt).yPos = newWidget[index].yPos;
			
			
		    }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

	//Activate the root node
	newWidget[0].isActive = true;

	//This code sets all parents
	//outter for loops through widgets
	for (int i = 0; i < newWidget.length; i++)
	    {
		//ensure widget is not null
		if(newWidget[i] != null)
		    {
			//inner for loops through children and sets parents
			for(int j = 0; j < newWidget[i].children.size(); j++)
			    {
				int tt = newWidget[i].children.get(j);
				newWidget[tt].parents.add(i);
			    }//end inner for loop through children
		    }//end if to ensure widget isn't null
	    }//end outter for loop the loops through widgets

    }//end readCSV method

    //This method returns the critical path
    public double getCriticalPath(Widget myWidget)
    {
	double tempTime = 0;
	double tempP = 0;
	for(int i = 0; i < myWidget.children.size(); i++)
	    {
		double temp2 = 0;
		temp2 = getCriticalPath(newWidget[myWidget.children.get(i)]);
		if(temp2 <= tempTime)
		    {
			newWidget[myWidget.children.get(i)].isCritPath = false;
		    }
		tempTime = Math.max(tempTime,temp2); 
	    }
	for(int i = 0; i < myWidget.myProcess.size(); i++)
	    {
		tempP = Math.max(tempP, myWidget.myProcess.get(i).time);
	        
	    }
	myWidget.isCritPath = true;
	return (tempP + tempTime);
    }//End method to return critical path

    /*SUMMARY:  DrawArrow method draws a directed arrow
      INPUTS: 1. Graph to draw on
              2. (x1, y1) are starting coordinates
	      3. (x2, y2) ending coordinates
      OUTPUT: Draws arrows on graph */
    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
	Graphics2D g = (Graphics2D) g1.create();

	double dx = x2 - x1, dy = y2 - y1;
	double angle = Math.atan2(dy, dx);
	int len = (int) Math.sqrt(dx*dx + dy*dy);
	AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
	int ARR_SIZE = 10; //Sets arrowhead size
	at.concatenate(AffineTransform.getRotateInstance(angle));
	g.transform(at);
	
	// Draw horizontal arrow starting in (0, 0)
	g.drawLine(0, 0, len, 0);
	g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
		      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }//end drawArrow method

    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse pressed (# of clicks: "+ e.getClickCount() + ")");
	int x = e.getXOnScreen();
	int y = e.getYOnScreen();
	//System.out.println(x+","+y);
	
	//System.out.println(newWidget[0].r.getBounds2D());
	//Loops to determine if widget was pressed
	for(int i = 0; i < newWidget.length; i++)
	    {
		//ensure widget[i] != null
		if (newWidget[i] != null && newWidget[i].r.contains(e.getLocationOnScreen()))
		    {
			//System.out.println(newWidget[i].name + " selected");
			newWidget[i].isSelected = true;
			newWidget[i].xPos = x;
			newWidget[i].yPos = y-50;
		    }//end if checking if widget is null
	    }//end for loop to determine if widget was selected
    }
     
    public void mouseReleased(MouseEvent e) {
        //System.out.println("Mouse released (# of clicks: "+ e.getClickCount() + ")");
	//unselect all widgets
	for (int i = 0; i < newWidget.length; i++)
	    {
		if(newWidget[i] != null)
		    {
			if(newWidget[i].isSelected == true)
			    {
				newWidget[i].xPos = e.getXOnScreen();
				newWidget[i].yPos = e.getYOnScreen()-50;
			    }//end if
			newWidget[i].isSelected = false;
		    }//end if
	    }//end for loop
    }
     
    public void mouseEntered(MouseEvent e) {
	
	
        //System.out.println("Mouse entered");
    }

    public void mouseMoved(MouseEvent e)
    {
	//System.out.println("TEST");
	
    }
     
    public void mouseExited(MouseEvent e) {
        //System.out.println("Mouse exited");
    }
     
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Mouse clicked (# of clicks: "+ e.getClickCount() + ")");
    }

    public void mouseDragged(MouseEvent e){
	
	for (int i = 0; i < newWidget.length; i++)
	    {
		if(newWidget[i] != null && newWidget[i].isSelected == true)
		    {
			newWidget[i].xPos = e.getXOnScreen();
			newWidget[i].yPos = e.getYOnScreen()-50;
		    }//end if
	    }//end for loop
	//System.out.println(e.getLocationOnScreen());
    }

    /*PURPOSE:  This method saves the model to the user specificed .csv file
      INPUT: File Path to save model to
      OUTPUT: Writes the model to the .csv file
     */
    public void saveModel()
    {
	String fName = "temp.csv";

	  try {
            // Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(fName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);

	    bufferedWriter.write("Index,Name,xPos,yPos,dist,mean,Var,Child");
	    bufferedWriter.newLine();
	    //this for loop writes each non-null widget to a .csv file
	    for(int i = 0; i < newWidget.length; i++)
		{
		    if(newWidget[i] != null)
			{
		    bufferedWriter.write(String.valueOf(newWidget[i].index+","));
		    bufferedWriter.write(newWidget[i].name+",");
		    bufferedWriter.write(String.valueOf(newWidget[i].xPos+","));
		    bufferedWriter.write(String.valueOf(newWidget[i].yPos+","));
		    bufferedWriter.write(newWidget[i].type+",");
		    bufferedWriter.write(String.valueOf(newWidget[i].mean)+",");
		    bufferedWriter.write(String.valueOf(newWidget[i].var)+",");
		
			 //this for loop populates node children
			 int tempNumChild = newWidget[i].children.size();
			 for(int j = 0; j < tempNumChild; j++)
			{
			    //if(newWidget[i].children.get(j) > 0)
			    //	{
	      bufferedWriter.write(String.valueOf(newWidget[i].children.get(j)));
			  bufferedWriter.write(",");
			  //	}   
			}//end loop to save children

			 bufferedWriter.newLine();
			}//end if the checks if widgetis null
	
		  		}//end for loop
	    // Always close files.
            bufferedWriter.close();
	  }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + fName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }//end save Model method
}//end class flowPath
