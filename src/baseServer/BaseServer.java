package baseServer;

import java.util.Scanner;

import modules.InitModule;
import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersTable;

public class BaseServer {

	public static BaseController baseController;
	public static void main(String []args){
		
		baseController= BaseController.getInstance();
		baseController.startServer();
		System.out.println("User ip:" + utility.Utilities.getIpAddress("172"));
		System.out.println("User id:" + utility.Utilities.getSystemId());
		
		String input="";
		Scanner sc=new Scanner(System.in);
		new InitModule();
		CallbackRegister.getInstance().echoCallbacks();
		while(true){
			input= sc.nextLine();
			if(input.contains("close")){
				
				try{
					baseController.stopServer();
					sc.close();
					break;
				}
				catch(Exception e){
					System.out.println("Failed to stop the server: "+e.getMessage());
				}
				
			}
			else if(input.contains("ping-message")){
				String data= input.substring(12);
				baseController.sendRequest(new PingQuery("ping-message", null, null, data), "tcp-server", "PingQuery", false, "", utility.Utilities.getIpAddress());
			}
			else if(input.contains("ping")){
				String data= input.substring(input.indexOf("1"));
				baseController.sendRequest(new PingQuery(input.substring(0, input.indexOf("1")-1),null,null), "tcp-server", "PingQuery", true, utility.Utilities.getSystemId(), data);
			}
			else if(input.contains("show-peers")){
				PeersTable.getInstance().echoEntries();
			}
			else
			baseController.sendRequest(input, "tcp-server", "string", true, "", utility.Utilities.getIpAddress());
		}
	}
}
