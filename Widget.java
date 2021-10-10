import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

//Widget class is used to track widgets
public class Widget
{
    int xPos;
    int yPos;
    int index;
    boolean isActive;
    String name;
    String type;
    double mean;
    double var;
    ArrayList<Integer> parents = new ArrayList<Integer>();
    ArrayList<Integer> children = new ArrayList<Integer>();
    ArrayList<Process> myProcess = new ArrayList<Process>();
    Rectangle r;
    boolean isCritPath;
    boolean isSelected;
    
    //Constructor
    public Widget()
    {
	this.isActive = false;
        this.isSelected = false;
	this.isCritPath = false;
	r = new Rectangle();
    }

    public void printWidget()
    {
	System.out.println("name:" + name);
	System.out.println("xPos: " + xPos);
	System.out.println("yPos: " + yPos);
	//System.out.println("level: " + level);
	System.out.println("child: " + children.size());
	//r = new Rectangle();
    }

    public void drawWidget(Graphics g, int xDim, int yDim)
    {
	Graphics2D g2 = (Graphics2D) g;
	r.setBounds(this.xPos, this.yPos, xDim, yDim);
	if(this.isSelected == true)
	    {
		g.setColor(Color.YELLOW);
	    }
	g2.draw(r);
	g.setColor(Color.BLACK);
    }//end drawWidget method;
}// ends Widget class
