package p2pApp.p2pDownloader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DownloadResponse {

	String filepath;
	DataOutputStream output;
	public DownloadResponse(String filepath, DataOutputStream output){
		
		this.filepath= filepath;
		this.output= output;
		
		sendFile();
	}
	
	private void sendFile(){
		
		File myFile = new File(filepath);
        try {
        	send(myFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Downloading File #1: "+ex.getMessage());
        }catch(Exception e){
        	System.out.println("Downloading file #2: "+e.getMessage());
        }
        
        
	}
      
	public void send(File myFile) throws Exception{
		
		System.out.println("Sending file: "+filepath);
		int n = 0;
	    byte[]buf = new byte[utility.Utilities.bufferSize];
	    
	    output.writeLong(myFile.length());
		output.flush();
		
		FileInputStream fis = new FileInputStream(myFile);
	    while((n =fis.read(buf)) != -1){
	    	output.write(buf,0,n);
	        output.flush();
	    }
	    fis.close();
	    output.close();
	    System.out.println("File sending completed");
	}
}
//public static void send(Socket socket){
//
//    try {
//        
//        dos.writeLong((new File(fileToSend)).length());
//        dos.flush();
//
//        //buffer for file writing, to declare inside or outside loop?
//        int n = 0;
//        byte[]buf = new byte[4092];
//        //outer loop, executes one for each file
//
//            System.out.println("Sending...");
//            //create new fileinputstream for each file
//            FileInputStream fis = new FileInputStream(new File(fileToSend));
//
//            //write file to dos
//            while((n =fis.read(buf)) != -1){
//                dos.write(buf,0,n);
//                dos.flush();
//
//            }
//            //should i close the dataoutputstream here and make a new one each time?
//   
//        //or is this good?
//        dos.close();
//        fis.close();
//    } catch (IOException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }
//


