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
	String type;
	ArrayList<AlternateIps> altIps;
	
	public SearchResults(String ip, String userid, String fileid, String filename, String hash, String filesize){
		this(ip, userid, fileid, filename, hash, filesize, "1");
	}
	
	public SearchResults(String ip, String userid, String fileid, String filename, String hash, String filesize, String type){
		this(ip, userid, fileid, filename, hash, filesize, new ArrayList<AlternateIps>(), type);
	}
	
	public SearchResults(String fileid, String filename, String hash, String filesize){
		this.fileid= fileid;
		this.filename= filename;
		this.hash= hash;
		this.filesize= filesize;
	}
	
	public SearchResults(String ip, String userid, String fileid, String filename, String hash, String filesize, ArrayList<AlternateIps> altIps, String type){
		this.ip= ip;
		this.fileid= fileid;
		this.filename= filename;
		this.hash= hash;
		this.filesize= filesize;
		this.userid= userid;
		this.altIps= altIps;
		this.type= type;
		
		//this.altIps.add(new AlternateIps("193.12.1.14","34","something", "89"));
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
	
	public void addAlternateIps(String ip, String key, String filename, String filesize, String userid){
		if(altIps==null)
			altIps= new ArrayList<AlternateIps>();
		altIps.add(new AlternateIps(ip, key, filename, filesize, userid));
	}
	
	public ArrayList<AlternateIps> getAlternateIps(){
		return altIps;
	}
	
	public String getType(){
		return type;
	}
	
	public void setFilename(String name){
		filename= name;
	}
	
	public String getHash(){
		return hash;
	}
}

