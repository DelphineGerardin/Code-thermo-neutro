
public class CellHX extends Cell
{

	double intermediateTemperature = 820;
	
	public CellHX()
	{
		super();
	}
	
	// Definition of the cell by x,y,z
	public CellHX(double[] x, int direction)
	{
		super(x, direction);
	}
		
	// Definition of cylindric cell by radius and height
	public CellHX(double x, double radius, int direction)
	{
		super(x, radius, direction);
	}
	
	public void setPExtract(double pExtract)
	{
		this.pExtract = pExtract;
	}
	
	public void computePExtract()
	{
		 this.pExtract = this.hS*(this.temperature-intermediateTemperature)/this.volume;
	}
	
	public void compute_dTdt()
	{
		this.dm = dv*rho;
		
		this.dTdt = (this.pVol-pExtract)/(this.rho*this.cp) 
					- (this.dm/(this.rho*this.sectionValue))*((this.temperature-this.neighbours[0].temperature)/(this.dxValue));
	
	}
	
	public void setHS(double hS)
	{
		this.hS = hS;
	}

}