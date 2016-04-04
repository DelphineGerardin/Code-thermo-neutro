import java.util.ArrayList;


public class Zone 
{
	int direction = 0;
	double volume = 0;
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	
	public Zone(int direction)
	{
		this.direction = direction;
	}

	public void addCell(Cell newCell)
	{
		this.cellList.add(newCell);
		this.volume += newCell.volume;
	}
	

}
