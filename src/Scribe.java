import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class Scribe
{
	String fileName;
	String writing;
	File fichier;
	
	public Scribe(String name)
	{
		String writing = "";
		this.fileName = name;
		fichier = new File(fileName);
	}
	
	public void writeInFile()
	{
	FileWriter fw;
	try
	{
		fw = new FileWriter(fichier);
		fw.write(this.writing);
		fw.close();
		
	} 
	catch (FileNotFoundException e){e.printStackTrace();} 
	catch (IOException e) {e.printStackTrace();}

	}
	
	public void addValue(String value)
	{
		if (this.writing == null){this.writing = value+" \n";}
		else {this.writing += value+" \n";}
	}

}
