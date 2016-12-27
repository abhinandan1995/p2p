package p2pApp.p2pDownloader;

import java.io.FileOutputStream;

import p2pApp.SearchResults;

public class DownloadNodes {

	SearchResults searchResults;
	String status;
	String segMode;
	int totalParts= 0;
	short partsDone[];
	int partsCompleted=0;
	FileOutputStream fos;

	public DownloadNodes(SearchResults sr){
		searchResults = sr;
		calculateSegMode();
	}

	public void setStatus(String status){
		this.status= status;
	}

	private void calculateSegMode(){
		long size= Long.parseLong(searchResults.getFileSize());
		SegmentationModes sm= new SegmentationModes(size);
		segMode= sm.getName();
		totalParts= (int)Math.ceil(((double)size/sm.getSize()));
		partsDone= new short[totalParts];
	}

	public void addPartsDone(int part){
		if(partsDone[part]<2){
			partsDone[part]=2;
			partsCompleted++;
		}
		if(partsCompleted>=totalParts){
			try{
				
			
			fos.getChannel().close();
			fos.close();
			}
			catch(Exception e){
				System.out.println("From download nodes "+e.getMessage());
			}
		}
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
			int pt= getRemainingPart();
			if(pt<0)
				return true;
			partsDone[pt]= 1;
			new DownloadThread(searchResults.getIp(), searchResults.getFileId(), searchResults.getUserid(), pt, segMode, searchResults.getFilename(), fos.getChannel(), this);
		}
		catch(Exception e){
			System.out.println("Download node #1 "+e.getMessage());
		}
		return false;
	}
}
