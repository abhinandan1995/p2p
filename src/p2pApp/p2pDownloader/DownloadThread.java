package p2pApp.p2pDownloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import p2pApp.p2pQueries.DownloadQuery;

public class DownloadThread extends Thread{

	String userIp;
	String fileId;
	String userId;
	int part;
	String segMode;
	String filename;
	FileChannel fos;
	DownloadNodes node;
	
	public DownloadThread(String userIp, String fileId, String userId, int part, String segMode, String filename, FileChannel fos, DownloadNodes node){
		this.userIp= userIp;
		this.fileId= fileId;
		this.userId= userId;
		this.part= part;
		this.segMode= segMode;
		this.filename= filename;
		this.fos= fos;
		this.node= node;
		
		this.start();
	}

	public void run(){
		Socket clientSocket = null;

		try {
			clientSocket = new Socket( userIp, utility.Utilities.serverPort);
			startDownload(clientSocket);
		}
		catch(Exception e){
			System.out.println("Download Thread #1 "+ e.getMessage());
		}
	}

	private void startDownload(Socket clientSocket) throws Exception{
		DataInputStream input = new DataInputStream( clientSocket.getInputStream()); 
		DataOutputStream output = new DataOutputStream( clientSocket.getOutputStream()); 

		String data= utility.Utilities.makeRequest(new DownloadQuery("fileId",fileId, segMode, part), "p2p-app", userId, null, ""+clientSocket.getPort(), ""+utility.Utilities.serverPort, true, "DownloadQuery");
		output.writeInt(data.length());
		output.writeBytes(data);

		readInputStream(input);
	}

	private void readInputStream(DataInputStream input) throws Exception{

		long size= input.readLong();
		int n=0;
		byte[]buf = new byte[utility.Utilities.bufferSize];
		//  FileOutputStream fos = new FileOutputStream(utility.Utilities.outputFolder+utility.Utilities.parseInvalidFilenames(filename));

		while (size > 0 && (n = input.read(buf, 0, (int)Math.min(buf.length, size))) != -1){

			ByteBuffer bf= ByteBuffer.wrap(buf);
			bf.flip();
			
			fos.position(part*(new SegmentationModes(segMode)).getSize());
			while(bf.hasRemaining()){
				fos.write(bf);
			}
			//fos.flush();
			fos.force(true);
			size -= n;
		}
	}
}

