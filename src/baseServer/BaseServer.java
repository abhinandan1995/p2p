package baseServer;

import java.util.Scanner;

import tcpQueries.PingQuery;
import tcpServer.BaseController;

public class BaseServer {

	public static BaseController baseController;
	public static void main(String []args){
		
		baseController= BaseController.getInstance();
		baseController.startServer();
		
		String input="";
		Scanner sc=new Scanner(System.in);
		
		while(true){
			input= sc.nextLine();
			if(input.contains("close")){
				
				try{
					baseController.stopServer();
					sc.close();
					break;
				}
				catch(Exception e){
					System.out.println("Failed to stop the server"+e.getMessage());
				}
				
			}
			else if(input.contains("ping")){
				baseController.sendRequest(new PingQuery(input,null,null), "tcp-server", "PingQuery", true, "", utility.Utilities.getIpAddress());
			}
			else
			baseController.sendRequest(input, "tcp-server", "string", true, "", utility.Utilities.getIpAddress());
		}
	}
}
