package p2pApp.p2pIndexer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import utility.MySqlHandler;

public class HashCalculator {
	
	private static HashCalculator hashCalc;
	private ArrayList<String> fileList;
	private static int selectedVal= 0;
	private int count = 0;
	private boolean running1= false, running2= false;
	private HashCalculator(){
		fileList= new ArrayList<String>();
	}
	
	public static HashCalculator getInstance(){
		if(hashCalc==null){
			hashCalc= new HashCalculator();
		}
		return hashCalc;
	}
	
	public void setType(int val){
		selectedVal= val;
	}
	
	public void addNewPath(String path){
		fileList.add(path);
		startHashing();
	}
	
	private void startHashing(){
		
		if(!running1){
			Thread t1= new Thread(){
				public void run(){
					running1= true;
					while(true){
		
						String path= getFilePath();
						if(path!=null && path!=""){
							String h= null;
							if(selectedVal==1){
								h= sha1InputStream(path);
							}
							else{
								h= md5InputStream(path);
							}
							MySqlHandler.getInstance().updateTable(TableHandler.TblName, "Hash", h, "Path", path.replace("\\", "/").replace("'", "''"));
						}
						else 
							break;
					}
					running1= false;
				}
			};
			t1.start();
		}
		
		if(!running2){
			Thread t2= new Thread(){
				public void run(){
					running2 = true;
					while(true){
						
						String path= getFilePath();
						if(path!=null && path!=""){
							String h= null;
							if(selectedVal==1){
								h= sha1InputStream(path);
							}
							else{
								h= md5InputStream(path);
							}
							MySqlHandler.getInstance().updateTable(TableHandler.TblName, "Hash", h, "Path", path.replace("\\", "/").replace("'", "''"));
						}
						else 
							break;
					}
					running2= false;
				}
			};
			t2.start();
		}
	}
	
	private synchronized String getFilePath(){
		
		if(fileList.size()>0){
			count++;
			String path= fileList.get(0);
			fileList.remove(0);
			return path;
		}
		System.out.println("File hashing Completed. Total files hashed: "+ count);
		count=0;
		return null;
	}
	
	private String md5InputStream(String filepath){
		
		System.out.println("Calculating hash5: "+filepath);
		try{
			MessageDigest md= MessageDigest.getInstance("MD5");
			byte[] buffer= new byte[1024*1024];
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			int cnt=0;
			while((cnt= is.read(buffer))!=-1){
				md.update(buffer, 0, cnt);
			}
			byte[] digest= md.digest();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<digest.length;i++){
				sb.append(Integer.toString((digest[i] & 0xff)+0x100, 16).substring(1));
			}
			
			is.close();
			return sb.toString();
		}
		catch(Exception e){
			
		}
		return null;
	}
	
	private String sha1InputStream(String filepath){
		
		System.out.println("Calculating hash1: "+filepath);
		try{
			MessageDigest md= MessageDigest.getInstance("SHA-1");
			byte[] buffer= new byte[1024*1024];
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			int cnt=0;
			while((cnt= is.read(buffer))!=-1){
				md.update(buffer, 0, cnt);
			}
			byte[] digest= md.digest();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<digest.length;i++){
				sb.append(Integer.toString((digest[i] & 0xff)+0x100, 16).substring(1));
			}
			is.close();
			return sb.toString();
			
		}
		catch(Exception e){
			
		}
		return null;
	}
	
}
