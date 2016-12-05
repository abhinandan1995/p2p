package p2pApp.p2pIndexer;

import java.io.File;
import java.util.ArrayList;

import utility.MySqlHandler;

public class DirectoryReader {
	
	static long FileID =10000;
	static MySqlHandler handler = MySqlHandler.getInstance();
	static String TblName = "DirReader";
	static String[] columns = new String[]{"FileID","FileName","Path","Hash","FileSize"};
	static String[] values = null;
    static void Process(File aFile) {	
    if(aFile.isFile()){
//      System.out.println("[FILE] " + aFile.getName());
//      System.out.println("[PATH] " + aFile.getPath().replace("\\", "/"));
//      
      values = new String[]{String.valueOf(FileID++),aFile.getName().replace("'", "''").replace("_", " "),aFile.getPath().replace("\\", "/").replace("'", "''"),"null",String.valueOf(aFile.length())};
      handler.insertSingle(TblName, columns, values);
    }
    else if (aFile.isDirectory()) {
//     System.out.println("[DIR] " + aFile.getName());
      File[] listOfFiles = aFile.listFiles();
      if(listOfFiles!=null) {
        for (int i = 0; i < listOfFiles.length; i++)
          Process(listOfFiles[i]);
      } else {
        System.out.println(" [ACCESS DENIED]");
      }
    }
  }
    
    public static void DR_init(ArrayList<String> names){
    	DR_init(names, true);
    }
    
	  public static void DR_init(ArrayList<String> names, boolean force)
	  {  
		  String[] data = new String[]{"FileID int not null","FileName varchar(255) not null","Path varchar(255) not null","Hash varchar(255) null","FileSize varchar(255)", "primary key(FileID)", "FULLTEXT(FileName)"};
		  handler.createTable(TblName, data, null, force);
		  File aFile;
		  for(int i=0;i<names.size();i++)
		  {
			aFile= new File(names.get(i));  
			Process(aFile);
		  }
	  }
	  
  public static String getFilePath(String FileID)
  {
	  String query = "SELECT Path from "+ TblName + " WHERE FileID = "+ FileID;
	  String path = (String)(handler.fetchQuery(query)).get(0).get("Path");
	  return path;
  }
//  public static void main(String[] args) {
//	  
//	  ArrayList<String> names=new ArrayList<>();
//	  names.add("e:/movies");
//	  DR_init(names);
//	  System.out.println(getFilePath("10003"));
//  }

}
