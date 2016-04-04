import java.util.ArrayList;


public class Reactor 
{
	double power = 0;
	double pExtract = 0;
	double criticalTemperature = 1000;
	double fuelTemperature = 1000;
	double fuelTemperatureOld;
	double fuelTemperatureFlux;
	double fuelEnergy;
	double intermediateTemperature = 820;
	Geometry geometry;
	Zone heatExchanger;
	Zone core;
	Zone superiorHorizontalPipe;
	Zone superiorVerticalPipe;
	Zone inferiorHorizontalPipe;
	Zone inferiorVerticalPipe;
	
	ArrayList<Cell> cellList;
	
	//neutronic parameters
	double reactivity0 = 0;
	double reactivity = 0;
	double neutrons = 0;
	double precursorsInCore = 0;
	double dndt = 0;
	double decayFactor = 0.3;
	// from Serpent critical core calculation
	// double fissionEnergy = 199*10E16*1.6E-19; calcul? //J
	double fissionEnergy = 199*1E6*1.6E-19; //J
	double fissionXS = 2.7E-3; //cm-1
	double neutronSpeed = 1/7E-9; //cm/s
	double generationTime = 1.98E-6;
	//from Axel thesis
	double effectiveDelayedNeutronFraction = 154E-5;
	double dkdT = -8E-5;
	
	
	public Reactor(ArrayList<Cell> cellList)
	{
		this.cellList = cellList;
		
		heatExchanger = new Zone(2);
		core = new Zone(2);
		superiorHorizontalPipe = new Zone(0);
		superiorVerticalPipe = new Zone(2);
		inferiorHorizontalPipe = new Zone(0);
		inferiorVerticalPipe = new Zone(2);
		
		this.geometry = new Geometry(heatExchanger, 
						inferiorVerticalPipe, 
						inferiorHorizontalPipe, 
						core, 
						superiorHorizontalPipe, 
						superiorVerticalPipe);
	}
	
	public void computeGlobalParameters()
	{
		this.fuelTemperatureOld = this.fuelTemperature;
		
		this.fuelTemperature = 0;
		this.fuelEnergy = 0;
		
		double energyPerKelvin = 0;
		
		this.power = 0;
		this.pExtract =0;
		
		for (int i = 0; i<cellList.size(); i++)
		{
			// for temperature computation
			energyPerKelvin += cellList.get(i).rho*cellList.get(i).cp*cellList.get(i).volume;
			fuelEnergy += cellList.get(i).rho*cellList.get(i).cp*cellList.get(i).volume* cellList.get(i).temperature;
		
			// for power computation
			this.power += cellList.get(i).pVol*cellList.get(i).volume;
			this.pExtract += cellList.get(i).pExtract*cellList.get(i).volume;
			//System.out.println(cellList.get(i).pVol);
		}
		this.fuelTemperature = fuelEnergy/energyPerKelvin;
		
		//For temperature multiplied by flux factor: faux
		this.fuelTemperatureFlux = 0;
		double h = geometry.coreHeight;
		double z = 0;
		double dz = 0;
		double weightingFactor = 0;
		for (int i = 0; i<core.cellList.size(); i++)
		{
			dz = 2*core.cellList.get(i).dx[core.cellList.get(i).direction];
			weightingFactor = (Math.sin((Math.PI/h)*(z+dz-h/2))-Math.sin((Math.PI/h)*(z-h/2)))/2;
			
			this.fuelTemperatureFlux += core.cellList.get(i).temperature* weightingFactor;
			z += dz;
		}
		
	}
	
	public void setPowerCos(double power)
	{
		double h = geometry.coreHeight;
		double z = 0;
		double dz = 0;
		double weightingFactor = 0;
		double volume = 0;
		for (int i = 0; i<core.cellList.size(); i++)
		{
			volume = core.cellList.get(i).volume;
			dz = 2*core.cellList.get(i).dx[core.cellList.get(i).direction];
			weightingFactor = (Math.sin((Math.PI/h)*(z+dz-h/2))-Math.sin((Math.PI/h)*(z-h/2)))/2;
			core.cellList.get(i).pVol = power * weightingFactor / volume;
			
			z += dz;
		}
	}
	public void setPowerHomo(double power)
	{
		for (int i = 0; i<cellList.size(); i++)
		{
			cellList.get(i).pVol = power / this.geometry.volume;
		}
	}
	public void setPowerNeutronic(double power)
	{
		this.power = power;
		this.neutrons = this.power/(this.fissionEnergy*this.neutronSpeed*this.fissionXS);
		this.setNeutronCos();
		
	}
	
	public void setNeutronCos()
	{
		double h = geometry.coreHeight;
		double z = 0;
		double dz = 0;
		double weightingFactor = 0;
		for (int i = 0; i<core.cellList.size(); i++)
		{
			dz = 2*core.cellList.get(i).dx[core.cellList.get(i).direction];
			weightingFactor = (Math.sin((Math.PI/h)*(z+dz-h/2))-Math.sin((Math.PI/h)*(z-h/2)))/2;
			core.cellList.get(i).setNeutrons(this.neutrons*weightingFactor);
			
			z += dz;
		}
	}
	
	public void compute_dndt()
	{
		dndt = ((this.reactivity-this.effectiveDelayedNeutronFraction)/this.generationTime)*neutrons;
		double wi = 1;
		
		/*dndt += core.cellList.get(0).getPrecursors()*decayFactor*wi
				-(core.cellList.get(0).dm/(core.cellList.get(0).rho*core.cellList.get(0).sectionValue))*((core.cellList.get(0).precursorDensity-core.cellList.get(0).neighbours[0].precursorDensity)/(core.cellList.get(0).dxValue));
		
		for (int i=1; i<core.cellList.size()-1; i++)
		{
			dndt += core.cellList.get(i).getPrecursors()*decayFactor*wi;
		}
		dndt += core.cellList.get(core.cellList.size()-1).getPrecursors()*decayFactor*wi
				+(core.cellList.get(core.cellList.size()-1).dm/(core.cellList.get(core.cellList.size()-1).rho*core.cellList.get(core.cellList.size()-1).sectionValue))*((core.cellList.get(core.cellList.size()-1).precursorDensity-core.cellList.get(core.cellList.size()-1).neighbours[0].precursorDensity)/(core.cellList.get(core.cellList.size()-1).dxValue));
		*/
		
		for (int i=0; i<core.cellList.size(); i++)
		{
			dndt += core.cellList.get(i).getPrecursors()*decayFactor*wi;
		}
		
		
	}
	
	public void compute_n(double dt)
	{
		this.neutrons = this.neutrons + dt*this.dndt;
		this.setNeutronCos();
	}
	public void computeReactivity0()
	{
		this.precursorsInCore = 0;
		for(int i=0; i<core.cellList.size(); i++)
		{
			this.precursorsInCore += core.cellList.get(i).getPrecursors();
		}
		this.reactivity0 = this.effectiveDelayedNeutronFraction - this.decayFactor*this.generationTime*this.precursorsInCore/this.neutrons;
	
	}
	public void computeReactivity()
	{
		//this.reactivity = this.reactivity + this.dkdT * (this.fuelTemperature - this.fuelTemperatureOld);
		//this.reactivity = this.reactivity + this.dkdT * (this.fuelTemperature - this.criticalTemperature);
		//this.reactivity = 0;
		this.reactivity = this.reactivity0 + this.dkdT * (this.fuelTemperature - this.criticalTemperature);
		
	}
	
	public void computePrecursorEquilibrium()
	{
		double precursorsCore = 0;
		double precursorsTotal = 0;
		double precursorsCoreOld = 0;
		double precursorsTotalOld = 0;
		
		for (int i=0; i<core.cellList.size(); i++)
		{
			precursorsCoreOld += core.cellList.get(i).getPrecursors();
		}
		precursorsCore = this.effectiveDelayedNeutronFraction*this.neutrons / (this.generationTime*this.decayFactor);
		
		for (int i=0; i<this.cellList.size(); i++)
		{
			precursorsTotalOld += cellList.get(i).getPrecursors();
		}
		precursorsTotal = precursorsTotalOld*precursorsCore/precursorsCoreOld;
		
		for (int i=0; i<this.cellList.size(); i++)
		{
			cellList.get(i).setPrecursors(cellList.get(i).getPrecursors() * precursorsTotal / precursorsTotalOld) ; 
		}
	}

}
