package p2pApp.p2pDownloader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class UploadThread {

	String filepath;
	DataOutputStream output;
	int part;
	String segMode;
	long partSize;

	public UploadThread(String filepath, DataOutputStream output){
		this.filepath= filepath;
		this.output= output;

		sendFile();
	}

	public UploadThread(String filepath, DataOutputStream output, int part, String segMode){
		this.filepath= filepath;
		this.output= output;
		this.part= part;
		this.segMode= segMode;

		sendFile();
	}

	private void sendFile(){

		File myFile = new File(filepath);
		partSize= (new SegmentationModes(segMode)).getSize();

		try {
			send(myFile);
		} catch (FileNotFoundException ex) {
			System.out.println("Downloading File #1: "+ex.getMessage());
		}catch(Exception e){
			System.out.println("Downloading file #2: "+e.getMessage());
		}    
	}

	public void send(File myFile) throws Exception{

		System.out.println("Sending file: "+filepath+" :"+part);
		
		int n = 0;
		byte[]buf = new byte[utility.Utilities.bufferSize];
		ByteBuffer bb= ByteBuffer.allocate(utility.Utilities.bufferSize);
		
		output.writeLong(getFileSize(myFile));
		output.flush();

		FileInputStream fis = new FileInputStream(myFile);
		FileChannel fc= fis.getChannel();
		while((n =fc.read(bb)) != -1){
			bb.get(buf);
			output.write(buf,0,n);
			output.flush();
		}
		fc.close();
		fis.close();
		output.close();
		System.out.println("File sending completed :"+part);
	}

	private long getFileSize(File myFile){
		long totalLength= myFile.length();
		long coveredSize= part*partSize;
		if(partSize+coveredSize > totalLength)
			return (totalLength-coveredSize);
		return partSize;
	}
}
