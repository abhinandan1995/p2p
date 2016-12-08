package p2pApp.p2pIndexer;

import java.util.List;
import java.util.Map;

import utility.MySqlHandler;

public class TableHandler {

	public static long FileID =10000;
	private static MySqlHandler handler = MySqlHandler.getInstance();
	public static String TblName = "DirReader";
	private static String[] columns = new String[]{"FileID","FileName","Path","Hash","FileSize", "Type", "Valid"};
	private static String[] data = new String[]{"FileID int not null","FileName varchar(255) not null","Path varchar(255) CHARACTER SET utf8 COLLATE utf8_spanish_ci not null","Hash char(40) null","FileSize char(20)", "type char(1) not null default '1'", "valid char(1) not null default '1'", "primary key(FileID)", "FULLTEXT(FileName)", "Unique key (Path)", "key(valid)"};
	
	public static void createTable(boolean force){
		handler.createTable(TblName, data, null, force);
	}
	
	public static void fillTable(List<String[]> files){
		handler.insertMultiple(TblName, columns, files);
	}
	
	public static String getFilePath(String FileID)
	{
		String query = "SELECT Path from "+ TblName + " WHERE FileID = "+ FileID;
		String path = (String)(handler.fetchQuery(query)).get(0).get("Path");
		return path;
	}
	
	public static List<Map<String, Object>> getFilesFromTable(){
		return MySqlHandler.getInstance().fetchQuery("SELECT * from "+TblName);
	}
	
	public static int getNextId(){
		List<Map<String, Object>> l= MySqlHandler.getInstance().fetchQuery("SELECT max(FileId) as max from "+TblName);
		return (Integer.parseInt(l.get(0).get("max").toString())+1);
	}
	
	public static void fillTable(String[] values){
		handler.insertSingle(TblName, columns, values);
	}
}
