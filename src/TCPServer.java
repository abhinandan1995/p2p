// TCPServer.java
// A server program implementing TCP socket
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TCPServer implements Runnable { 

	final String TAG = "Server-Exception";
	int serverPort=0;
	ServerSocket listenSocket;
	
	TCPServer(int serverPort){
		this.serverPort= serverPort;
		new Thread(this).start();
	}

	public void run(){
		try{ 
			listenSocket = new ServerSocket(serverPort); 
			System.out.println("Server started at port given: "+serverPort);

			while(true) { 
				Socket clientSocket = listenSocket.accept(); 
				Connection con = new Connection(clientSocket); 
			} 
		} 
		catch(IOException e) {
			System.out.println(TAG+"#1: "+e.getMessage());
		}
		catch(Exception e){
			System.out.println(TAG+"#2: "+e.getMessage());
		}
	}
	
	public void start(int port){
		try{ 
			listenSocket = new ServerSocket(port); 
			System.out.println("Server started at port: "+port);

			while(true) { 
				Socket clientSocket = listenSocket.accept(); 
				Connection con = new Connection(clientSocket); 
			} 
		} 
		catch(IOException e) {
			System.out.println(TAG+"#1: "+e.getMessage());
		}
		catch(Exception e){
			System.out.println(TAG+"#2: "+e.getMessage());
		}
	}

	public void startAtPort(int port){
		start(port);
	}

	public void stop(){
		try{
			if(!listenSocket.isClosed())
				listenSocket.close();
		}
		catch(Exception e){
			System.out.println(TAG+"#3: "+e.getMessage());
		}
	}

	public boolean isServerRunning(){
		try{
			if(!listenSocket.isClosed() && listenSocket.isBound()){
				return true; 
			}
		}
		catch(Exception e){
			System.out.println(TAG+"#4: "+e.getMessage());
		}
		return false;
	}

	public void getServerInfo(){

		try{
			System.out.println("Start data");
			System.out.println(listenSocket.getLocalPort());
			System.out.println(listenSocket.getInetAddress());
			System.out.println(listenSocket.getReuseAddress());
			System.out.println(listenSocket.getSoTimeout());
			System.out.println(listenSocket.hashCode());
			System.out.println(InetAddress.getLocalHost());
			System.out.println(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
			System.out.println(NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress()).getHardwareAddress().toString());
//			while(NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses()){
//				System.out.println(NetworkInterface.getNetworkInterfaces().nextElement().getName()+"~"+NetworkInterface.getNetworkInterfaces().nextElement().getHardwareAddress().toString()+"~"+NetworkInterface.getNetworkInterfaces().nextElement().getDisplayName());
//			}
			List<InterfaceAddress> li= NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses();
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
			while(ni.hasMoreElements()){
				li= ni.nextElement().getInterfaceAddresses();
				int i=0;
				while(i<li.size())
					System.out.println("ip: "+li.get(i++).getAddress());
			}
			
			System.out.println("User ip:" + Utilities.getIpAddress());
			System.out.println("User id:"+ Utilities.getSystemId());
			
			InetAddress ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getLoopbackAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			System.out.println(sb.toString());
			
//			Enumeration<NetworkInterface> nic = NetworkInterface.getNetworkInterfaces();
//			while(nic.hasMoreElements()){
//				Enumeration<InetAddress> ia= nic.nextElement().getInetAddresses();
//				while(ia.hasMoreElements()){
//					NetworkInterface nid= NetworkInterface.getByInetAddress(ia.nextElement());
//					mac= nid.getHardwareAddress();
//					for (int i = 0; i < mac.length; i++) {
//						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//					}
//					System.out.println(sb.toString());
//				}
//						
//			}
			System.out.println("new fns");
			System.out.println(utility.Utilities.getSystemId());
			System.out.println(utility.Utilities.getSlowerSystemId());
			System.out.println(utility.Utilities.getSystemMAC());
			System.out.println(utility.Utilities.getBaseSystemId());
			Set<Integer> si= new HashSet<Integer>();
			for(int i=0;i<100000;i++){
				int val=utility.Utilities.getRandomNumber();
				if(si.contains(val))
					System.out.println("Error");
				else
					si.add(val);
				
			}
			System.out.println("End data");
			
			
		}
		catch(Exception e){
			System.out.println(TAG+"#5: "+e.getMessage());
		}
	}

}

class Connection extends Thread { 
	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 

	public Connection (Socket aClientSocket) { 
		System.out.println("Tcp Server Connection thread opened");
		try { 
			clientSocket = aClientSocket; 
			input = new DataInputStream( clientSocket.getInputStream()); 
			output =new DataOutputStream( clientSocket.getOutputStream()); 
			this.start(); 
		} 
		catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		} 
	} 

	public void run() { 
		try { // an echo server 
			//  String data = input.readUTF();

			FileWriter out = new FileWriter("test.txt");
			BufferedWriter bufWriter = new BufferedWriter(out);

			//Step 1 read length
			int nb = input.readInt();
			System.out.println("Read Length"+ nb);
			byte[] digit = new byte[nb];
			//Step 2 read byte
			System.out.println("Writing.......");
			for(int i = 0; i < nb; i++)
				digit[i] = input.readByte();

			String st = new String(digit);

			bufWriter.append(st);
			bufWriter.close();
			System.out.println ("receive from : " + 
					clientSocket.getInetAddress() + ":" +
					clientSocket.getPort() + " message - " + st);

			if(st.contains("ip"))
				st=st+Utilities.getIpAddress();
			//Step 1 send length
			output.writeInt(st.length());
			//Step 2 send length
			output.writeBytes(st); // UTF is a string encoding
			//  output.writeUTF(data); 

			if(st.indexOf("name")!= -1){
				forwardReq(st);
			}
		} 
		catch(EOFException e) {
			System.out.println("EOF:"+e.getMessage()); } 
		catch(IOException e) {
			System.out.println("IO:"+e.getMessage());} 

		finally { 
			try { 
				clientSocket.close();
			}
			catch (IOException e){/*close failed*/}
		}
	}

	public void forwardReq(String s){
		try{
			String url="http://localhost:7002/"+ "?" + "name=abhi";
			System.out.println(url);
			URLConnection connection = new URL(url).openConnection();
			InputStream response = connection.getInputStream();
			System.out.println("from 2nd"+response);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}
}
