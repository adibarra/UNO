import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * The FileLoader class enables easy manipulation of files.
 * It can be used to save and retrieve files.
 * @author Alec Ibarra
 */
public class FileLoader{

	private static String workingDirectory;
	private static String OS = (System.getProperty("os.name")).toUpperCase();
	
	/**
	 * Prepares special variables
	 */
	public static void prepare()
	{
		if (OS.contains("WIN"))
		{
			workingDirectory = System.getenv("AppData");
		}
		else
		{
			workingDirectory = System.getProperty("user.home");
			workingDirectory += "/Library/Application Support";
		}
	}
	
	/**
	 * Loads file from outside jar file, parses its content
	 */
	public ArrayList<String> load(String fileName)
	{
		prepare();
		String data = "";
		String line = "";
		ArrayList<String> fileContents = new ArrayList<String>();
		
		File jarFile;
		try
		{
			jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			File targetFile = new File(jarFile.getParentFile()+"/"+fileName+".dat");
		
			if(targetFile.exists())
			{
				BufferedReader br = new BufferedReader(new FileReader(targetFile));
			
				while((line = br.readLine()) != null)
					if(!line.contains("#"))
						data += line;
				br.close();
				
				for(int k = 0; k < data.split(";").length; k++)
					fileContents.add(data.split(";")[k]);
			}
			else
			{
				System.out.println("Failed to load file: "+fileName+".dat");
			}
					
		} catch (URISyntaxException | IOException e) {}	
		
		return fileContents;
	}
	
	/**
	 * Loads file from jar's internal res folder, parses its content
	 */
	public ArrayList<String> loadRes(String fileName)
	{
		prepare();
		String data = "";
		String line = "";
		ArrayList<String> fileContents = new ArrayList<String>();
		
		try
		{
			InputStream targetFile = this.getClass().getResourceAsStream(fileName+".dat");
					
			if(targetFile != null)
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(targetFile,"UTF-8"));
			
				while((line = br.readLine()) != null)
					if(!line.contains("#"))
						data += line;
				br.close();
				
				for(int k = 0; k < data.split(";").length; k++)
					fileContents.add(data.split(";")[k]);
			}
			else
			{
				System.out.println("Failed to load file: "+fileName+".dat");
			}
					
		} catch (IOException e) {}	
		
		return fileContents;
	}
	
	/**
	 * Get save directory
	 */
	public static String getSaveDir()
	{
		return workingDirectory;
	}

}
