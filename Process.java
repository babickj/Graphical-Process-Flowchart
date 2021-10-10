import java.util.*;

/*
SUMMARY:  This class is used to track a new process through
the flow path
 */

public class Process
{
    int widgetIndex;
    int nextWidget;
    int xPos;
    int yPos;
    double time;
    double [] delays;

    //CONSTRUCTOR
    public Process()
    {
	this.xPos = 0;
	this.yPos = 0;
	this.time = 0;
	
    }//end constructor


    //NORMAL DISTRIBUTION
    public double Normal(double mean, double stdDev)
    {
	//Create random object
	Random ran = new Random();
	double nxt = (ran.nextGaussian()*stdDev + mean);
	return nxt;
    }//end method that returns normal distribution


    public double ExpDist(double mean, double lambda)
    {
	Random ran = new Random();
	//double nxt = PoissonDistribution(ran, mean, stdDev);
	return Math.log(1-ran.nextDouble()) / (-lambda);
    }//end method that returns Poisson Dist

    public void Print()
    {
	System.out.println("time "+this.time);
    }//end print method
    
}//end Process class
