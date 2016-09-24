package tcpClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import modules.ErrorModule;
import modules.TCPClientModule;
import utility.Query_v12;
import baseServer.BaseNetworkEngine;

public class TCPClient extends Thread {

	private int serverPort;
	private String ip;
	private String data;
	private Socket s= null;
	boolean response= false;
	private static int requestCount=0;
	
	public void sendRequestIntoNetwork(int serverPort, String ip, String data, boolean response){
		this.serverPort= serverPort;
		this.ip= ip;
		this.data= data;
		this.response= response;
		this.start();
	}

	public void run(){

		if(requestCount++ >= utility.Utilities.maxparallelRequests){
			System.out.println("System Overload: Too many requests!");
			return;
		}
		
		if(!utility.Utilities.selfRequest && ip.equals(utility.Utilities.getIpAddress()))
			return;
		
		try{ 
			s = new Socket();
			s.connect(new InetSocketAddress(ip, serverPort), utility.Utilities.connectionTimeout);
			
			DataInputStream input = new DataInputStream( s.getInputStream()); 
			DataOutputStream output = new DataOutputStream( s.getOutputStream()); 
			output.writeInt(data.length());
			output.writeBytes(data);
			
			if(response){
				int nb = input.readInt();
				byte []digit= new byte[nb];
				for(int i = 0; i < nb; i++)
					digit[i] = input.readByte();
				String st = new String(digit);

				Query_v12 query= utility.Utilities.getQueryObject(st);
				
				if(!query.getResponse())
					output= null;
				
				System.out.println("Received from server: "+st);

				if(query.getModule().equals("tcp-server")){
					new TCPClientModule(query, output);
				}
				else if(query.getModule().equals("error")){
					new ErrorModule().echoMessage(query.getPayload());
				}
				else if(!modules.ModuleLoader.getInstance().moduleLoad("client", query, output))
					new ErrorModule(query, output, "No such module found!");
				
			}
			else{
				input=null;
			}
		}
		catch (UnknownHostException e){ 
			System.out.println("Socket:"+e.getMessage());}
		catch (EOFException e){
			System.out.println("EOF:"+e.getMessage()); }
		catch (IOException e){
			System.out.println("IO:"+e.getMessage());
			if(e.getMessage().contains("timed out")){
				BaseNetworkEngine.getInstance().manageNeighboursList(this.ip, true);
			} 
			}

		finally {
			requestCount--;
			
			if(s!=null){	
				try {
					s.close();
				}
				catch (IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
	}
}

