package p2pApp;

import java.util.ArrayList;

public class SearchResults {

	String ip;
	String filename;
	String hash;
	String filesize;
	String userid;
	String fileid;
	String path;
	ArrayList<String> altIps;
	
	public SearchResults(String ip, String userid, String fileid, String filename, String hash, String filesize){
		this.ip= ip;
		this.fileid= fileid;
		this.filename= filename;
		this.hash= hash;
		this.filesize= filesize;
		this.userid= userid;
	}
	
	public SearchResults(String fileid, String filename, String hash, String filesize){
		this.fileid= fileid;
		this.filename= filename;
		this.hash= hash;
		this.filesize= filesize;
	}
	
	public SearchResults(String ip, String userid, String fileid, String filename, String hash, String filesize, ArrayList<String> altIps){
		this.ip= ip;
		this.fileid= fileid;
		this.filename= filename;
		this.hash= hash;
		this.filesize= filesize;
		this.userid= userid;
		this.altIps= altIps;
	}
	
	public String getFileId(){
		return fileid;
	}
	
	public String getIp(){
		return ip;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public String getUserid(){
		return userid;
	}
	
	public String getFileSize(){
		return filesize;
	}
	
	public void addAlternateIps(String ip){
		if(altIps==null)
			altIps= new ArrayList<String>();
		altIps.add(ip);
	}
	
	public ArrayList<String> getAlternateIps(){
		return altIps;
	}
}

