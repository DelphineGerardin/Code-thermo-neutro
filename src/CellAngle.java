
public class CellAngle extends Cell
{
	public CellAngle()
	{
		
	}
	
	public CellAngle(double[] x, int direction)
	{
		super(x, direction);
	}
	
	// Definition of cylindric cell by radius and height
	public CellAngle(double x, double radius, int direction)
	{
		super(x, radius, direction);
	}
	
	public CellAngle(double x, double radius, double angle, int direction)
	{
		super(x, radius, angle, direction);
	}
	
	public void computeGeometricValues()
	{
		//this.dxValue = 2*this.dx[this.neighbours[0].direction];
		this.sectionValue = this.neighbours[0].section[this.neighbours[0].direction];
		//this.dxValue = this.dx[this.direction]+this.dx[this.neighbours[0].direction];
		//this.sectionValue = this.section[this.direction];
		
		this.dxValue = this.volume/this.sectionValue;
	}

}
