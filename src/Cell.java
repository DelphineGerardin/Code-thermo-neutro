
public class Cell 
{
	double temperature = 1000;// approx initial temperature
	double[] section= new double[3];
	int direction;
	double[] dx = new double[3];
	double dxValue = 0;
	double sectionValue = 0;
	double pVol = 0 ;
	double dTdt;
	double rho = 4125.2417; //valeur à 700°C
	double cp = 1594; //valeur à 700°C
	double dv = 18/(3.9*16);
	double dm;
	double volume;
	Cell[] neighbours;
	double pExtract = 0;
	double hS;
	int cellNumber;
	
	//Scribes
	Scribe sTemperature;
	Scribe sPrecursorDensity;
	
	// Neutronic parameter
	double neutronDensity = 0;
	double precursorDensity = 0;
	double dpdt = 0;
	double effectiveDelayedNeutronFraction = 0;
	double generationTime = 0;
	double decayFactor = 0;
	double fissionEnergy = 0; 
	double fissionXS = 0; 
	double neutronSpeed = 0; 
	
	public Cell()
	{
		this.neighbours = new Cell[2];
	}
	
	// Definition of the cell by x,y,z
	public Cell(double[] x, int direction)
	{
		this.direction = direction;
		this.neighbours = new Cell[2];
		for (int i= 0; i<3; i++)
		{
			this.dx[i] = x[i]/2;	
		}
		this.section[0] = x[1]*x[2];
		this.section[1] = x[0]*x[2];
		this.section[2] = x[0]*x[1];
		
		this.volume = x[0]*x[1]*x[2];
		
	}
	
	// Definition of cylindric cell by radius and height
	public Cell(double length, double radius, int direction)
	{
		this.direction = direction;
		this.neighbours = new Cell[2];
		
		for (int i=0; i<3; i++)
		{
			if (direction == i)
			{
				this.dx[i] = length/2;
				this.section[i] = Math.PI*Math.pow(radius, 2);
			}
			else
			{
				this.dx[i] = radius;
				this.section[i] = 0;
			}
		}
		this.volume = length*Math.PI*Math.pow(radius, 2);
	}
	
	//Definition of a cylindric portion
	public Cell(double length, double radius, double angle, int direction)
	{
		this.direction = direction;
		this.neighbours = new Cell[2];
		
		for (int i=0; i<3; i++)
		{
			if (direction == i)
			{
				this.dx[i] = length/2;
				this.section[i] = (angle/2)*Math.pow(radius, 2);
			}
			else
			{
				this.dx[i] = radius/2;
				this.section[i] = 0;
			}
		}
		this.volume = length*(angle/2)*Math.pow(radius, 2);
	}
		
	public void compute_dTdt()
	{
		this.dm = dv*rho;
		
		this.dTdt = this.pVol/(this.rho*this.cp) 
					- (this.dm/(this.rho*this.sectionValue))*((this.temperature-this.neighbours[0].temperature)/(this.dxValue));	
	}
	
	public void compute_T(double t, double dt)
	{
		this.temperature = this.temperature + dt*this.dTdt;	
	}
	
	public void compute_dpdt()
	{
		this.dm = dv*rho;
		
		this.dpdt = (this.effectiveDelayedNeutronFraction/this.generationTime)*this.neutronDensity
				- this.decayFactor*this.precursorDensity
				- (this.dm/(this.rho*this.sectionValue))*((this.precursorDensity-this.neighbours[0].precursorDensity)/(this.dxValue)); 
		
		/*this.dpdt = (this.effectiveDelayedNeutronFraction/this.generationTime)*this.neutronDensity		
				- this.decayFactor*this.precursorDensity;*/
				
	}
	public void compute_p(double dt)
	{
		this.precursorDensity = this.precursorDensity + dt*this.dpdt;
	}
	public void compute_powerDensity()
	{
		this.pVol = this.neutronDensity * this.neutronSpeed * this.fissionXS * this.fissionEnergy;
		
	}
	public void setPVol(double p)
	{
		this.pVol = p;
	}
	
	public void setPExtract(double pExtract){}
	
	public void computePExtract(){}
	
	public void setHS(double hS){}
	
	public double getRho()
	{
		return this.rho;
	}
	
	public void addNeighbours(Cell... neighbours)
	{
		for (int i=0; i<neighbours.length; i++)
		{
			this.neighbours[i]=neighbours[i];
		}
		computeGeometricValues();
	}
	
	public void computeGeometricValues()
	{
		this.sectionValue = this.section[this.direction];
		this.dxValue = this.volume/this.sectionValue;
	}
	
	public void setCellNumber(int number)
	{
		this.cellNumber = number;
		this.sTemperature = new Scribe("temperature"+cellNumber);
		this.sPrecursorDensity = new Scribe("precursorDensity"+cellNumber);
	}
	
	public void setNeutronicParameters(Reactor reactor)
	{
		//set neutronic parameters
		this.effectiveDelayedNeutronFraction = reactor.effectiveDelayedNeutronFraction;
		this.generationTime = reactor.generationTime;
		this.fissionEnergy = reactor.fissionEnergy; 
		this.fissionXS = reactor.fissionXS; 
		this.neutronSpeed = reactor.neutronSpeed; 
		this.decayFactor = reactor.decayFactor;
	}
	public void setPrecursors()
	{
		this.precursorDensity = this.effectiveDelayedNeutronFraction*this.neutronDensity / (this.generationTime*this.decayFactor);
	}
	public void setPrecursors(double nbPrecursors)
	{
		this.precursorDensity = nbPrecursors/this.volume;
	}
	public void setNeutrons(double nbNeutrons)
	{
		this.neutronDensity = nbNeutrons/this.volume;
	}
	public double getPrecursors()
	{
		return this.precursorDensity*this.volume;
	}
}
