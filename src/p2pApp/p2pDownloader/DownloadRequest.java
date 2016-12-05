package p2pApp.p2pDownloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import static utility.Utilities.outputFolder;

import p2pApp.p2pQueries.DownloadQuery;


public class DownloadRequest extends Thread {

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
		
		this.start();
	}
	
	public void run(){
		
		Socket clientSocket = null;

        try {
            clientSocket = new Socket( userIp, utility.Utilities.serverPort);
            startDownload(clientSocket);
            
        } catch (Exception e) {
            
        }
	}
	
	private void startDownload(Socket clientSocket) throws Exception{	
		File theDir = new File(outputFolder);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + outputFolder);
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		String data= utility.Utilities.makeRequest(new DownloadQuery("fileId",fileId), "p2p-app", userId, null, ""+clientSocket.getPort(), ""+utility.Utilities.serverPort, true, "DownloadQuery");
		
		DataInputStream input = new DataInputStream( clientSocket.getInputStream()); 
		DataOutputStream output = new DataOutputStream( clientSocket.getOutputStream()); 
		
		output.writeInt(data.length());
		output.writeBytes(data);
		
		try {
            long size= input.readLong();
            long startSize= size;
            int n = 0;
            byte[]buf = new byte[utility.Utilities.bufferSize];
            double percent= (utility.Utilities.bufferSize/(double)size);
            int counter=0;
            
            System.out.println("Downloading file: "+filename);
                
                FileOutputStream fos = new FileOutputStream(utility.Utilities.outputFolder+filename);
                while (size > 0 && (n = input.read(buf, 0, (int)Math.min(buf.length, size))) != -1)
                		{
                		  fos.write(buf,0,n);
                		  fos.flush();
                		  size -= n;
                		  counter++;
                		  if(((int)(counter*percent*100))%5==0){
                			  System.out.println("Downloading done..."+ (int)((double)(startSize-size)/(double)startSize*100));
                		  }
                		  
//                		  if(((int)(counter*percent*100))>60){
//                			  break;
//                		  }
                		}
                		fos.close();
                		System.out.println("Download completed");

        } catch (IOException e) {
            e.printStackTrace();
        }

	}	
}
