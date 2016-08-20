package tcpServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import modules.TCPServerModule;
import utility.Query_v12;

public class BaseReader extends Thread {

	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 
	final private String TAG= "Base Reader";
	public BaseReader (Socket aClientSocket) { 
		
		try { 
			clientSocket = aClientSocket; 
			input = new DataInputStream( clientSocket.getInputStream()); 
			output =new DataOutputStream( clientSocket.getOutputStream()); 
			this.start(); 
		} 
		catch(IOException e) {
			System.out.println(TAG+"#1 "+e.getMessage());
		}
		catch(Exception e){
			System.out.println(TAG+"#2 "+e.getMessage());
		}
	}
	
	public void run(){
		
		try{
			int readLen= input.readInt();
			byte[] digit = new byte[readLen];

			for(int i = 0; i < readLen; i++)
				digit[i] = input.readByte();
			String data= new String(digit);
			Query_v12 query= utility.Utilities.getQueryObject(data);
			
			if(BaseController.getInstance().isPresent(query.getQueryId()))
				return;
			
			System.out.println ("receive from : " + 
					clientSocket.getInetAddress() + ":" +
					clientSocket.getPort() + " Data - " + data);
			
			
			if(!query.getResponse())
				output= null;
			
			if(query.getModule().equals("tcp-server")){
				new TCPServerModule(query, output);
			}
			else
			noCaseMatch(output, query.getSourceSid());
		}
		catch(IOException e){
			
		}
		catch(Exception e){
			
		}
	}
	
	private void noCaseMatch(DataOutputStream output, String destId){
		try {
			if(output!=null){
				String st= "Invalid request! No such module found!";
				BaseController.getInstance().sendResponse(st, "error", "string", false, destId, output);
			}
		} 
		catch(Exception e){
			System.out.println(TAG+"#9 "+e.getMessage());
		}
	}
}
