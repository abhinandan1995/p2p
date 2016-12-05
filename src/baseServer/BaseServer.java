package baseServer;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import modules.InitModule;
import p2pApp.SearchResults;
import p2pApp.SearchTable;
import p2pApp.p2pDownloader.DownloadRequest;
import p2pApp.p2pQueries.SearchQuery;
import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.PeersTable;
import utility.MySqlHandler;

public class BaseServer {

	public static BaseController baseController;
	public static void main(String []args){
		
		baseController= BaseController.getInstance();
		baseController.startServer();
		
		String input="";
		Scanner sc=new Scanner(System.in);
		new InitModule();
		List<Map<String, Object>> l;
//		ArrayList<String[]> as= new ArrayList<String[]>();
//		as.add(new String[]{"another love song"});
//		MySqlHandler.getInstance().insertMultiple("testquery", new String[]{"filename"}, as);
		l=MySqlHandler.getInstance().fetchQuery("Select * from testquery");
		for(int i=0;i<l.size();i++){
			System.out.println(l.get(i).get("filename"));
		}
		
//		PeersTable.getInstance().addEntry("172.31.65.43", "kdsjfk6", "connected");
//		PeersTable.getInstance().addEntry("192.168.1.102","kdsjfk6", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.7", "kdsjfk2", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.7", "kdsjfk3", "connected");
//		PeersTable.getInstance().addEntry("192.168.1.102","kdsjfk3", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.7", "kdsjfk5", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.8", "kdsjfk5", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.9", "kdsjfk5", "connected");
//		PeersTable.getInstance().addEntry("172.31.78.6", "kdsjfk5", "connected");
		
		BaseNetworkEngine.getInstance().connectToNetwork();
		
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
			else if(input.contains("ping-message-all")){
				String data= input.substring(17);
				BaseNetworkEngine.getInstance().sendMultipleRequests(new PingQuery("ping-message-all", null, null, data), "tcp-server", "PingQuery", false);
			}
			else if(input.contains("ping-message")){
				String data= input.substring(12);
				baseController.sendRequest(new PingQuery("ping-message", null, null, data), "tcp-server", "PingQuery", false, "", utility.Utilities.getIpAddress());
			}
			else if(input.contains("ping")){
				String data= input.substring(input.indexOf("1"));
				baseController.sendRequest(new PingQuery(input.substring(0, input.indexOf("1")-1),null,null), "tcp-server", "PingQuery", true, "", data);
			}
			else if(input.contains("show-peers")){
				PeersTable.getInstance().echoEntries();
			}
			else if(input.contains("show-npeers")){
				PeersTable.getInstance().echoNeighbours();
			}
			else if(input.contains("query")){
				
				BaseNetworkEngine.getInstance().sendMultipleRequests(new SearchQuery(SearchTable.getInstance().getNewSearchId(), "search", input.substring(6, input.length()), null), "p2p-app", "SearchQuery", false);
				//baseController.sendRequest(input.substring(6, input.length()), "p2p-app", "string", false, "", utility.Utilities.getIpAddress());
			}
			else if(input.contains("download")){
				String []arr= input.split(" ");
				new DownloadRequest(arr[2],arr[1],arr[3]);
			}
			else if(input.contains("getfile")){
				String []arr= input.split(" ");
				int index= Integer.parseInt(arr[1]);
				SearchResults sr= SearchTable.getInstance().getFromSearchTable(index);
				new DownloadRequest(sr.getFileId(), sr.getIp(), sr.getFilename(), sr.getUserid());
			}
			else
			baseController.sendRequest(input, "tcp-server", "string", true, "", utility.Utilities.getIpAddress());
		}
	}
}
