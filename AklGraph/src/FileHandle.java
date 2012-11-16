import java.io.*;
import java.util.*;

public class FileHandle {
	
	File file;
	BufferedReader in;
	
	FileHandle(String fileName)
	{
		file = new File(fileName);
		try {
			in = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public Vector<String[]> getData(String split) throws IOException
	{
		String str = "";
		Vector<String[]> vec = new Vector<String[]>();
		while ((str = in.readLine()) != null) 
		{
		
			if ( str.contains("----"))
			{
				continue;
			}
			
			String tmpStr = str.replace('\t',' ');
			
			String[] strArr = tmpStr.split(split);
			//System.out.println(strArr[0]+" , "+strArr[1]+" , "+strArr[2].length());
			
			vec.add(strArr); 
		}	
		return vec;
	}
}
