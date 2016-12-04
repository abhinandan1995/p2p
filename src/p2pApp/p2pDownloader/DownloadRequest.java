package p2pApp.p2pDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class DownloadRequest {

	String fileId;
	String userIp;
	String userId;
	String filename;
	
	public DownloadRequest(String fileId, String userIp){
		this(fileId, userIp, null, null);
	}
	
	public DownloadRequest(String fileId, String userIp, String filename){
		this(fileId, userIp, filename, null);
	}
	
	public DownloadRequest(String fileId, String userIp, String filename, String userId){
		this.fileId= fileId;
		this.userIp= userIp;
		this.userId= userId;
		this.filename= filename;
	}
	
	private void startDownload(){
		
		
	}
	
	
}
