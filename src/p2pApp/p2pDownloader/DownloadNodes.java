package p2pApp.p2pDownloader;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import p2pApp.AlternateIps;
import p2pApp.SearchResults;
import p2pApp.p2pIndexer.DirectoryReader;

public class DownloadNodes {

	SearchResults searchResults;
	String status;
	String segMode;
	int totalParts= 0;
	short partsDone[];
	int partsCompleted=0;
	FileOutputStream fos;
	HashMap<String, Integer> activeIps;
	ArrayList<AlternateIps> ips;
	int activeSeeds=1;
	public boolean isComplete= false;
	
	public DownloadNodes(SearchResults sr){
		searchResults = sr;
		calculateSegMode();
		activeIps= new HashMap<String, Integer>();
		ips= sr.getAlternateIps();
//		ips.add(new AlternateIps("192.168.1.233", "34", sr.getFilename(), "3948", "abhi"));
//		ips.add(new AlternateIps("192.168.1.198", "34", sr.getFilename(), "3948", "abhi"));
//		ips.add(new AlternateIps("192.168.1.106", "34", sr.getFilename(), "3948", "abhi"));
//		ips.add(new AlternateIps("192.168.1.211", "34", sr.getFilename(), "3948", "abhi"));
		activeSeeds= activeSeeds + ips.size();
	}

	public void setStatus(String status){
		this.status= status;
	}

	public void removeIp(String ip, int pt){
		
		if(ip.equals(searchResults.getIp())){
			activeSeeds--;
		}
		else{
			
			for(int i=0;i<ips.size();i++)
				if(ips.get(i).getIp().equals(ip)){
					ips.remove(i);
					activeSeeds--;
					break;
				}
		}
		if(activeIps.containsKey(ip)){
			activeIps.remove(ip);
		}
		partsDone[pt]=0;
		if(activeSeeds > 0){
			DownloadEngine.getInstance().startDownloading();
		}
	}
	
	private void calculateSegMode(){
		long size= Long.parseLong(searchResults.getFileSize());
		SegmentationModes sm= new SegmentationModes(size);
		segMode= sm.getName();
		totalParts= (int)Math.ceil(((double)size/sm.getSize()));
		partsDone= new short[totalParts];
	}

	public void addPartsDone(String ip, int part){
		
		if(activeIps.containsKey(ip))
			activeIps.remove(ip);
		
		if(partsDone[part]<2){
			partsDone[part]=2;
			partsCompleted++;
		}
		if(partsCompleted>=totalParts){
			try{
			fos.getChannel().close();
			fos.close();
			isComplete= true;
			DirectoryReader.updateTableOnDownload(
					utility.Utilities.outputFolder + 
					utility.Utilities.parseInvalidFilenames(searchResults.getFilename()));
			}
			catch(Exception e){
				System.out.println("From download nodes "+e.getMessage());
			}
		}
		DownloadEngine.getInstance().startDownloading();
	}

	public int getRemainingPart(){
		for(int i=0;i<totalParts;i++){
			if(partsDone[i]==0)
				return i;
		}
		return -1;
	}

	public boolean downloadFile(){
		try{
			if(fos==null)
				fos = new FileOutputStream(utility.Utilities.outputFolder+utility.Utilities.parseInvalidFilenames(searchResults.getFilename()));
			
			if(DownloadThread.DownloadThreadsCount >= utility.Utilities.maxDownloadThreadCount)
				return false;
			
			int pt= getRemainingPart();
			if(pt<0)
				return true;
			
			AlternateIps sip= new AlternateIps(searchResults);
			sip= getNextIp(sip, pt);
			
			if(sip==null)
				return false;
			
			partsDone[pt]= 1;
				new DownloadThread(sip.getIp(), sip.getFileid(), sip.getUserid(), pt, segMode, sip.getFilename(), fos.getChannel(), this);
		}
		catch(Exception e){
			System.out.println("Download node #1 "+e.getMessage());
		}
		return false;
	}
	
	private AlternateIps getNextIp(AlternateIps sip, int pt){
		
		String ip = sip.getIp();
		int index= -1;
		
		while(activeIps.containsKey(ip)){
			ip = null;
			sip = null;
			
			if(ips== null)
				return null;
			if(++index < ips.size()){
				ip= ips.get(index).getIp();
			}
		}
		if(ip!=null){
			activeIps.put(ip, pt);
			if(index>-1)
				sip= ips.get(index);
		}
		return sip;
	}
	
	public int getPercentDone(){
		return (partsCompleted* 100 /totalParts);
	}
	
	public int getAvailableIps(){
		return (activeSeeds - activeIps.size());
	}
}
