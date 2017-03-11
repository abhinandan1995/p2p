package p2pApp.p2pDownloader;

import static utility.Utilities.outputFolder;

import java.io.File;
import java.util.ArrayList;

import p2pApp.SearchResults;

public class DownloadEngine {
	
	private static DownloadEngine downloadEngine;
	static ArrayList<DownloadNodes> downloadList;
	
	private DownloadEngine(){
		downloadList= new ArrayList<DownloadNodes>();
	}
	
	public static DownloadEngine getInstance(){
		if(downloadEngine==null)
			downloadEngine = new DownloadEngine();
		return downloadEngine;
	}
	
	public void addDownload(SearchResults sr){
		downloadList.add(new DownloadNodes(sr));
		startDownloading();
	}
	
	public void batchAdd(String base, ArrayList<SearchResults> al){
		
		try{
			makeDirectory(base);
		}
		catch(Exception e){
			System.out.println("Download Engine #3 "+ e.getMessage());
			return;
		}
		
		for(int i=0;i<al.size();i++){
			SearchResults sr= al.get(i);
			
			if(sr.getType().equals("2")){
				try{
					makeDirectory(sr.getFilename());
				}
				catch(Exception e){
					System.out.println("Download Engine #2 "+ e.getMessage());
				}
				continue;
			}
			if(sr.getType().equals("1")){
				downloadList.add(new DownloadNodes(sr));
			}
		}
		startDownloading();
	}
	
	public void startDownloading(){
			if(downloadList.isEmpty())
				return;	
			int count=0;
			try{
				makeDirectory();
				for(int i=0; i< downloadList.size() &&
						i < utility.Utilities.maxParallelDownloads; i++){
					
					if(downloadList.get(i).isComplete)
						downloadList.remove(i);
					else{
						downloadList.get(i).downloadFile();
						count++;
					}
				}
				
				for(int j=0; j< downloadList.size() &&
						count++ <= utility.Utilities.maxDownloadThreadCount; j++){
					downloadList.get(j).downloadFile();
				}
				
			}
			catch(Exception e){
				
			}
	}
	
	private static void makeDirectory() throws Exception{
		File theDir = new File(outputFolder);
		if (!theDir.exists()) {
		        theDir.mkdirs();
		}
	}
	
	private static void makeDirectory(String base) throws Exception{
		if(base!=null){
		
			File theDir = new File(outputFolder+"\\"+base);
			if (!theDir.exists()) {
			        theDir.mkdirs();
			}
		}
	}
}


