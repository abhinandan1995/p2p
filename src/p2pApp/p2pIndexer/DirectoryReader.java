package p2pApp.p2pIndexer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import p2pApp.p2pDownloader.DownloadEngine;

public class DirectoryReader {

	private static String[] values = null;
	private static List<String[]> files= null;
	private static HashMap<String, Object> fileList= null;
	private static DownloadEngine de= DownloadEngine.getInstance();
	
	static long getFiles (File aFile, int depth) throws Exception {	
		long sum=0;
		if(aFile.isFile()){
			
			if(depth>=0 && !de.isPresentInPaused(aFile)){
			values = new String[]{String.valueOf(TableHandler.FileID++),aFile.getName().replace("'", "''").replace("_", " "), aFile.getPath().replace("\\", "/").replace("'", "''"), "null", String.valueOf(aFile.length()), "1", "1"};
			files.add(values);
			}
			return aFile.length();
		}
		else if (aFile.isDirectory()) {

			
				File[] listOfFiles = aFile.listFiles();
			if(listOfFiles!=null) {
				for (int i = 0; i < listOfFiles.length; i++)
					sum = sum+ getFiles(listOfFiles[i], depth-1);
				
				if(depth>=0){
				values = new String[]{String.valueOf(TableHandler.FileID++),aFile.getName().replace("'", "''").replace("_", " "), aFile.getPath().replace("\\", "/").replace("'", "''"), "null", ""+sum, "2", "1"};
				files.add(values);
				}
			}
				return sum;
			} 
			else {
				System.out.println("Directory Read #3 - Access Denied");
			}
		
		return 0;
	}

	public static void DR_init(ArrayList<String> names){
		DR_init(names, true);
	}

	public static void DR_init(ArrayList<String> names, boolean force)
	{  
		try{

			File aFile;
			files= new ArrayList<String[]>();
			for(int i=0;i<names.size();i++)
			{
				try{
				
				int index= names.get(i).indexOf("::");
				int val= 128;
				if(index==-1)
					index= names.get(i).length();
				else
					val= Integer.parseInt(names.get(i).substring(index+2));
				aFile= new File(names.get(i).substring(0, index));  
				getFiles(aFile, val);
				}
				catch(Exception e){
					
				}
			}

			getFilesOnStart();

			TableHandler.createTable(force);
			getFilesHashValues();
			TableHandler.fillTable(files);
			fillInMissingHashes();
			files.clear();
			
			preserveOldHashedFiles();
			TableHandler.fillTable(files);
		}
		catch(Exception e){
			System.out.println("Directory Reader #5 "+e.getMessage());
		}
		finally{
			fileList.clear();
			files.clear();
		}
	}

	private static void getFilesOnStart(){
		List<Map<String, Object>> l;
		try{
			l= TableHandler.getFilesFromTable();
			fileList= new HashMap<String, Object>();
			for(int i=0;i<l.size();i++){
				fileList.put(l.get(i).get("Path").toString(), l.get(i));	
			}
		}
		catch(Exception e){
			System.out.println("Directory Read #2 "+e.getMessage());  
		}
	}

	@SuppressWarnings("unchecked")
	private static void getFilesHashValues(){

		try{
			for(int i=0;i<files.size();i++){
				String []arr= files.get(i);
				String parsedPath= arr[2].replace("''", "'");

				if(fileList.containsKey(parsedPath)){
					HashMap<String, Object> o= (HashMap<String, Object>)fileList.get(parsedPath);
					String h= o.get("Hash").toString();
					String fs= o.get("FileSize").toString();

					if(h!=null && h.length()>20 && arr[4].equals(fs) && arr[5].equals("1")){
						files.get(i)[3]= h;
					}
					fileList.remove(parsedPath);
				}
			}
		}
		catch(Exception e){
			System.out.println("Directory Read #1 "+e.getMessage());
		}
	}
	
	private static void fillInMissingHashes(){
		
		for(int i=0;i<files.size();i++){
			if(files.get(i)[3].length()<20 && files.get(i)[5].equals("1")){
				HashCalculator.getInstance().addNewPath(files.get(i)[2].replace("''", "'"));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void preserveOldHashedFiles(){
		try{

			if(!fileList.isEmpty()){
				Set<String> paths= fileList.keySet();
				Iterator<String> itr= paths.iterator();
				while(itr.hasNext()){
					String s= itr.next();
					HashMap<String, Object> o= (HashMap<String, Object>)fileList.get(s);
					values = new String[]{String.valueOf(TableHandler.FileID++),o.get("FileName").toString().replace("'", "''"), o.get("Path").toString().replace("'", "''"), o.get("Hash").toString(), o.get("FileSize").toString(), o.get("Type").toString(), "0"};
					files.add(values);
				}
			}

		}
		catch(Exception e){
			System.out.println("Directory Reader #4 "+e.getMessage());
		}
	}
	
	public static void updateTableOnDownload(String filepath){
		File f= new File(filepath);
		if(f.isFile()){
			values = new String[]{String.valueOf(TableHandler.getNextId()), f.getName().replace("'", "''").replace("_", " ").replace("?",""), f.getPath().replace("\\", "/").replace("'", "''"), "null", String.valueOf(f.length()), "1", "1"};
			TableHandler.fillTable(values);
			HashCalculator.getInstance().addNewPath(filepath);
		}
	}	
}
