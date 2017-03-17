package p2pApp.p2pDownloader;

import static utility.Utilities.outputFolder;

import java.io.File;
import java.util.ArrayList;

import p2pApp.SearchResults;

public class DownloadEngine {
	
	private static DownloadEngine downloadEngine;
	ArrayList<DownloadNodes> downloadList;
	ArrayList<DownloadNodes> pausedList;
	
	private DownloadEngine(){
		downloadList= new ArrayList<DownloadNodes>();
		pausedList= new ArrayList<DownloadNodes>();
	}
	
	public void AddPausedDownloads(ArrayList<DownloadNodes> dn){
		pausedList.addAll(dn);
	}
	
	public boolean isPresentInPaused(File file){
		for(int i=0; i < pausedList.size(); i++){
			if(pausedList.get(i).getSearchResults().getFilename().equals(file.getName())){
				return true;
			}
		}
		return false;
	}
	public static DownloadEngine getInstance(){
		if(downloadEngine==null)
			downloadEngine = new DownloadEngine();
		return downloadEngine;
	}
	
	public DownloadNodes addDownload(SearchResults sr){
		DownloadNodes dn=null;
		
		for(int i=0;i<pausedList.size();i++){
			SearchResults temp= pausedList.get(i).getSearchResults();
			if(temp.getHash().equals(sr.getHash())){
				if(temp.getIp().equals(sr.getIp())){
					dn= pausedList.get(i);
					dn.isPaused= false;
				}
				pausedList.remove(i);
				break;
			}
		}
		
		if(dn==null)
			dn= new DownloadNodes(sr);
		downloadList.add(dn);
		startDownloading();
		return dn;
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
	
	public void pauseDownloadOfFile(DownloadNodes node){
		for(int i=0;i<downloadList.size();i++){
			if(downloadList.get(i)==node){
				pausedList.add(node);
				downloadList.remove(i);
				utility.Utilities.writeToFile(
						"data/partials/"+node.getSearchResults().getHash()+".txt",
						utility.Utilities.getJsonString(new SaveDownloadNodes(node)), false);
				break;
			}
		}
	}
	
	public void resumeDownloadOfFile(DownloadNodes node){
		for(int i=0;i<pausedList.size();i++){
			if(pausedList.get(i)==node){
				pausedList.remove(i);
				downloadList.add(node);
				startDownloading();
				break;
			}
		}
	}
	
	public boolean stopDownloadOfFile(DownloadNodes node){
		for(int i=0;i<downloadList.size();i++){
			if(downloadList.get(i)==node){
				downloadList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void startDownloading(){
		
		System.out.println("**Threads: "+DownloadThread.DownloadThreadsCount);
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


