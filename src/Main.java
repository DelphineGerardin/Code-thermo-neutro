import java.util.ArrayList;


public class Main 
{
	
	public static void main(String[] args) 
	{

	Scribe sTemperature = new Scribe("temperature");
	Scribe sPower = new Scribe("power");
	Scribe sPExtract = new Scribe("pExtract");
	Scribe shS = new Scribe("hS");
	Scribe sFuelEnergy = new Scribe("fuelEnergy");
	Scribe sReactivity = new Scribe("reactivity");
	Scribe sNeutrons = new Scribe("neutrons");
	
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	
	double powerVariation[] = {3E9/16, 2E9/16, 50, 60};
	double power = powerVariation[0];
	
	Reactor msfr = new Reactor(cellList);	
	msfr.geometry.createCellList(cellList);
	for (int i = 0; i< cellList.size(); i++)
	{
		cellList.get(i).setNeutronicParameters(msfr);
	}
	
	// Mise à l'équilibre du système
	
	
	// Mise à l'équilibre des précurseurs
	
	//msfr.setPowerHomo(power);
	//msfr.setPowerCos(power);
	msfr.setPowerNeutronic(power);
	
	for (int i = 0; i< cellList.size(); i++)
	{
		cellList.get(i).setPrecursors();
		cellList.get(i).compute_powerDensity();
	}
	
	msfr.computeGlobalParameters();
	//cellList.get(0).setHS(msfr.power/(cellList.get(0).temperature-msfr.intermediateTemperature));
	
	double timeStep = 1e-6;
	double time = 0;
	double savingTime = 0;
	
	double precursor = 0;
	double precursorOld = 0;
	do{
		precursorOld = precursor;
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_dpdt();
		}
			
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_p(timeStep);
		}
		//msfr.computePrecursorEquilibrium();
		precursor = cellList.get(0).getPrecursors();
		time += timeStep;
		if (time > savingTime)
		{
		for (int i = 0; i< cellList.size(); i++)
		{
			//cellList.get(i).sPrecursorDensity.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).precursorDensity));
		}
		savingTime =time+0.001;
		System.out.println(time);
		}
		
	}while (Math.abs((precursor-precursorOld)/precursorOld) > 1E-17 || time < 10000*timeStep);
	
	double reactivity = 0;
	double reactivityOld = 0;
	do{
		reactivityOld = reactivity;
		msfr.compute_dndt();
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_powerDensity();
			cellList.get(i).compute_dpdt();
		}
		msfr.compute_n(timeStep);
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_p(timeStep);
		}
		time += timeStep;
		if (time > savingTime)
		{
		for (int i = 0; i< cellList.size(); i++)
		{
			//cellList.get(i).sPrecursorDensity.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).precursorDensity));
		}
		savingTime =time+0.001;
		}
		msfr.computeReactivity0();
		msfr.computeReactivity();
		reactivity = msfr.reactivity;
	}while (Math.abs((reactivity-reactivityOld)/reactivityOld) > 1E-17 || time < 10000*timeStep);
	
	System.out.println(reactivity);
	//Mise à l'équilibre des échanges de chaleur
	
	/*double hS_old = 0;
	double hS = 0;
	
	do{
		hS_old = hS;
		cellList.get(0).setHS(msfr.power/(cellList.get(0).temperature-msfr.intermediateTemperature));
		cellList.get(0).computePExtract();
		
		msfr.compute_dndt();
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_dTdt();
			cellList.get(i).compute_dpdt();
		}
		
		msfr.compute_n(timeStep);
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_T(time, timeStep);
			cellList.get(i).compute_p(timeStep);
			
			cellList.get(i).sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).temperature));
		}
		
		msfr.computeGlobalParameters();
		
		sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
		sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
		sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
		shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
		sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));
		sReactivity.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.reactivity));
		
		hS = cellList.get(0).hS;
		time += timeStep;
		
	}while (Math.abs((hS-hS_old)/hS_old) > 1E-10 || time < 10*timeStep);*/
	
	//Mise à l'équilibre des échanges de chaleur
	
	/*time = 0;
	cellList.get(0).setPExtract(power/cellList.get(0).volume);
	do{
		msfr.compute_dndt();
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_dTdt();
			cellList.get(i).compute_dpdt();
		}
		
		msfr.compute_n(timeStep);
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_powerDensity();
			cellList.get(i).compute_T(time, timeStep);
			cellList.get(i).compute_p(timeStep);
			
			cellList.get(i).sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).temperature));
		}
		
		msfr.computeGlobalParameters();
		
		sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
		sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
		sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
		shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
		sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));
		sReactivity.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.reactivity));
		
		time += timeStep;
		
	//}while (Math.abs((msfr.fuelTemperature-msfr.fuelTemperatureOld)/msfr.fuelTemperatureOld) > 1E-10 || time < 10*timeStep);
	}while (time < 1000*timeStep);*/
	
	
	savingTime =-timeStep;
	
	
	// neutronic only
	//cellList.get(0).setPExtract(power/cellList.get(0).volume);
	
	
	/*for (time = 0; time<10; time +=timeStep)
	{
		
		msfr.compute_dndt();
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_powerDensity();
			cellList.get(i).compute_dpdt();
		}
		
		msfr.compute_n(timeStep);
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_p(timeStep);
			if (time > savingTime)
			{
				cellList.get(i).sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).temperature));
				cellList.get(i).sPrecursorDensity.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).precursorDensity));
			}
		}
		msfr.computeReactivity();
		msfr.computeGlobalParameters();
		
		if (time > savingTime)
		{
			sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
			sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
			sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
			shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
			sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));
			sNeutrons.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.neutrons));
			sReactivity.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.reactivity));
			
			savingTime = time + 0.1;
		}
		
	}*/
	
	// Transitoire
	cellList.get(0).setPExtract(power/cellList.get(0).volume);
	for (time = 0; time<100; time +=timeStep)
	{
		if (time > powerVariation[2] && time <= powerVariation[3])
		{
			power += (powerVariation[1] - powerVariation[0])*timeStep/(powerVariation[3]-powerVariation[2]);
			//msfr.setPowerCos(power);
			//msfr.setPowerHomo(power);
			cellList.get(0).setPExtract(power/cellList.get(0).volume);
		}
			
		//cellList.get(0).computePExtract();
		
		msfr.compute_dndt();
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_powerDensity();
			cellList.get(i).compute_dTdt();
			cellList.get(i).compute_dpdt();
		}
		
		msfr.compute_n(timeStep);
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_T(time, timeStep);
			cellList.get(i).compute_p(timeStep);
			if (time > savingTime)
			{
				cellList.get(i).sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).temperature));
				cellList.get(i).sPrecursorDensity.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).precursorDensity));
			}
		}
		msfr.computeReactivity();
		msfr.computeGlobalParameters();
		
		
		if (time > savingTime)
		{
			sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
			sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
			sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
			shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
			sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));
			sReactivity.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.reactivity));
			sNeutrons.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.neutrons));
			
			savingTime = time + 0.1;
		}
		
	}
	
	sTemperature.writeInFile();
	sPower.writeInFile();
	sPExtract.writeInFile();
	shS.writeInFile();
	sFuelEnergy.writeInFile();
	sReactivity.writeInFile();
	sNeutrons.writeInFile();
	
	for (int i=0; i<cellList.size(); i++)
	{
		cellList.get(i).sTemperature.writeInFile();
		cellList.get(i).sPrecursorDensity.writeInFile();
	}
	
	
	}
}
