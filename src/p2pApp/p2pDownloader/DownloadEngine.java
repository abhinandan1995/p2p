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
	
	public static void addDownload(SearchResults sr){
		downloadList.add(0, new DownloadNodes(sr));
		startDownloading();
	}
	
	private static void startDownloading(){
		while(true){
			if(downloadList.isEmpty())
				break;	
			
			try{
				makeDirectory();
			}
			catch(Exception e){
				
			}
		}
	}
	
	private static void makeDirectory() throws Exception{
		File theDir = new File(outputFolder);
		if (!theDir.exists()) {
		        theDir.mkdirs();
		}
	}
}


