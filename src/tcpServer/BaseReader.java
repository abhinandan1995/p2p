package tcpServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import modules.ErrorModule;
import modules.TCPServerModule;
import p2pApp.AppServer;
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
			
			if(!BaseController.getInstance().allowFurtherProcessing(query))
				return;
			
			System.out.println ("receive from : " + 
					clientSocket.getInetAddress() + ":" +
					clientSocket.getPort() + " Data - " + data);
			
			
			if(!query.getResponse())
				output= null;
			
			
			if(query.getModule().equals("tcp-server")){
				new TCPServerModule(query, output);
			}
			else if(query.getModule().equals("error")){
				new ErrorModule().echoMessage(query.getPayload());
			}
			else if(query.getModule().equals("p2p-app")){
				new AppServer(query, output);
			}
			
//			else if(!modules.ModuleLoader.getInstance().moduleLoad("server", query, output))
//				new ErrorModule(query, output, "No such module found!");
		
		}
		catch(IOException e){
			
		}
		catch(Exception e){
			
		}
	}
}
