import java.util.ArrayList;


public class Geometry 
{

	double volume = 0;// = 18;
	
	// Fuel circuit geometry (dimensions in meters)
	double coreRadius = 1; 
	double coreHeight = 2;
	int axialDiscretization	= 5;
	double angle = Math.PI/8;
	
	double horizontalSegmentLength = 0.70;
	double horizontalSegmentRadius = 0.2;
	double horizontalSegmentDiscretization = 1;
	
	double verticalSegementLength = 0.4;
	double verticalSegmentRadius = 0.2;
	
	double heatExchangerHeight = coreHeight-2*verticalSegementLength;
	double heatExchangerLenght = 0.5;
	
	Zone[] zones ;
	
	
	public Geometry()
	{
		
	}
	
	public Geometry(Zone... zones)
	{
		this.zones = zones;
	}
	
	public void createCellList(ArrayList<Cell> cellList)
	{	
		int iGlobal = 0;
		int iZone = 0;
		
		//Simplified geometry
		/*this.zones[iZone].cellList.add(new CellHX(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;
		
		this.zones[iZone].cellList.add(new CellAngle(new double[] {1.0,1.0,1.0},this.zones[iZone].direction, this.zones[iZone-1].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;
		
		this.zones[iZone].cellList.add(new Cell(new double[] {1.0,1.0,1.0},this.zones[iZone].direction));
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;	
		iZone++;*/
		
		//Fuel circuit geometry
		
		//this.zones[iZone].cellList.add(new CellHX(new double[] {heatExchangerLenght,heatExchangerLenght,heatExchangerHeight}, this.zones[iZone].direction));
		this.zones[iZone].addCell(new CellHX(new double[] {heatExchangerLenght,heatExchangerLenght,heatExchangerHeight}, this.zones[iZone].direction));
		cellList.addAll(this.zones[iZone].cellList);
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;
		iZone ++;
		
		//this.zones[iZone].cellList.add(new Cell(verticalSegementLength, verticalSegmentRadius, this.zones[iZone].direction));
		this.zones[iZone].addCell(new Cell(verticalSegementLength, verticalSegmentRadius, this.zones[iZone].direction));
		cellList.addAll(this.zones[iZone].cellList);
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;
		iZone ++;
		
		
		for (int i =0; i< horizontalSegmentDiscretization; i++)
		{
			//this.zones[iZone].cellList.add(new Cell(horizontalSegmentLength/horizontalSegmentDiscretization,horizontalSegmentRadius,this.zones[iZone].direction));
			this.zones[iZone].addCell(new Cell(horizontalSegmentLength/horizontalSegmentDiscretization,horizontalSegmentRadius,this.zones[iZone].direction));
			this.volume += this.zones[iZone].cellList.get(i).volume; 
			iGlobal++;	
		}
		cellList.addAll(this.zones[iZone].cellList);
		iZone ++;
		
		for (int i =0; i< axialDiscretization; i++)
		{
			if (i == 0)
			{
				//this.zones[iZone].cellList.add(new CellAngle(coreHeight/axialDiscretization,coreRadius, angle, this.zones[iZone].direction));
				this.zones[iZone].addCell(new CellAngle(coreHeight/axialDiscretization,coreRadius, angle, this.zones[iZone].direction));
			}
			else
			{
				//this.zones[iZone].cellList.add(new Cell(coreHeight/axialDiscretization,coreRadius, angle, this.zones[iZone].direction));
				this.zones[iZone].addCell(new Cell(coreHeight/axialDiscretization,coreRadius, angle, this.zones[iZone].direction));
			}
			
			this.volume += this.zones[iZone].cellList.get(i).volume; 
			iGlobal++;
			
		}
		
		cellList.addAll(this.zones[iZone].cellList);
		iZone ++;
		
		for (int i =0; i< horizontalSegmentDiscretization; i++)
		{
			//this.zones[iZone].cellList.add(new Cell(horizontalSegmentLength/horizontalSegmentDiscretization,horizontalSegmentRadius,this.zones[iZone].direction));
			this.zones[iZone].addCell(new Cell(horizontalSegmentLength/horizontalSegmentDiscretization,horizontalSegmentRadius,this.zones[iZone].direction));
			this.volume += this.zones[iZone].cellList.get(i).volume; 
			iGlobal++;
		}
		cellList.addAll(this.zones[iZone].cellList);
		iZone ++;
		
		//this.zones[iZone].cellList.add(new CellAngle(verticalSegementLength, verticalSegmentRadius,this.zones[iZone].direction, this.zones[iZone-1].direction));
		this.zones[iZone].addCell(new CellAngle(verticalSegementLength, verticalSegmentRadius,this.zones[iZone].direction, this.zones[iZone-1].direction));
		cellList.addAll(this.zones[iZone].cellList);
		this.volume += this.zones[iZone].cellList.get(0).volume; 
		iGlobal++;
		iZone ++;
		
		// declaration of neighbour cells
		cellList.get(0).addNeighbours(cellList.get(cellList.size()-1),cellList.get(1));
		for (int i=1; i<cellList.size()-1; i++)
		{
			cellList.get(i).addNeighbours(cellList.get(i-1),cellList.get(i+1));
		}
		cellList.get(cellList.size()-1).addNeighbours(cellList.get(cellList.size()-2),cellList.get(0));
		
		for (int i=0; i<cellList.size(); i++)
		{
			cellList.get(i).setCellNumber(i);
		}
	}
	
	
	/*public void computeGlobalParameters(ArrayList<Cell> cellList)
	{
		volume = 0;
		for (int i=0; i<cellList.size(); i++)
		{
			volume += cellList.get(i).volume;
		}
	}*/
}
