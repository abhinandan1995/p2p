package baseServer;

import java.util.List;

import tcpClient.TCPClient;
import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.Query_v12;

public class BaseNetworkEngine {

	private static BaseNetworkEngine baseEngineInstance;
	private PeersTable peersTable;
	
	private BaseNetworkEngine(){
		peersTable= PeersTable.getInstance();
	}

	public static BaseNetworkEngine getInstance(){
		if(baseEngineInstance==null)
			baseEngineInstance= new BaseNetworkEngine();
		return baseEngineInstance;
	}


	public void manageNeighboursList(){
		
		PeersTable pt= peersTable;
		List<PeersEntries> pe= pt.getConnected();
			
		for(int i=0;i< pe.size() && pt.getNeighbourPeers().size()<=utility.Utilities.neighbourPeersCount; i++){
			if(!pt.isNeighbourPresent(pe.get(i).systemId)){
				
				pt.addNeighbourPeers(pe.get(i).ip, pe.get(i).systemId, "unknown", false);
				
				double random= Math.random();
				System.out.println("Started: "+random);
				
				BaseController.getInstance().sendRequest(new PingQuery("ping",null,null), "tcp-server", "PingQuery", true, "", pe.get(i).ip);
				
				System.out.println("Ended: "+ random);
				
			}
		}
	}
	
	public void manageNeighboursList(String ip, boolean reflectDisconnected){		

		if(ip!=null){
			peersTable.remNeighbourPeerByIp(ip);
			if(reflectDisconnected){
				peersTable.updateStatusByIp(ip, "disconnected");
			}
		}
		manageNeighboursList();
	}
	
		
	public int sendMultipleRequests(Object obj, String module, String rtype, boolean response){
		return sendMultipleRequests(obj, module, rtype, response, utility.Utilities.maxHopCount);
	}
	
	public int sendMultipleRequests(Object obj, String module, String rtype, boolean response, int hopCount){
		BaseController bControl= BaseController.getInstance();
		int qId= bControl.getNewQueryId();
		for(int i=0; i<peersTable.getNeighbourPeers().size() && i<utility.Utilities.maxSimultaneousRequests; i++){
			bControl.sendRequest(qId, obj, module, rtype, response, peersTable.getNeighbourPeers().get(i).systemId, peersTable.getNeighbourPeers().get(i).ip, hopCount);
		}
		return qId;
	}
	
	public void forwardRequests(Query_v12 query){
		query.decrementHop();
		query.setResponse(false);
		if(query.getHopCount()<=0){
			return;
		}
		List<PeersEntries> tempList= peersTable.getNeighbourPeers();
		for(int i=0; i<tempList.size() && i<utility.Utilities.maxSimultaneousRequests; i++){
			new TCPClient().sendRequestIntoNetwork(utility.Utilities.serverPort, tempList.get(i).ip, utility.Utilities.getJsonString(query), false);
		}
	}
	
	public void connectToNetwork(){
		
		if(peersTable.getConnected().size()==0){
			System.out.println("No peers found. Please ping an active user to connect!");
			return;
		}
		manageNeighboursList();
	}
	
	//Callback Functions
	
	public void manageNeighboursList(String action, Object obj){
		try{
			if(action.equals("tcp-server-pong")){
				Query_v12 query= (Query_v12)obj;
				PeersTable pt= peersTable;
				
				pt.updateNeighbourPeer(query.getSourceIp(), query.getSourceSid(), "connected", true);
				manageNeighboursList();
			}
			
			if(action.equals("tcp-server-ping")){
				Query_v12 query= Query_v12.class.cast(obj);
				PeersTable pt= peersTable;
				
				pt.updateNeighbourPeer(query.getSourceIp(), query.getSourceSid(), "connected", false);

			}
		}
		catch(Exception e){
			System.out.println("Network Engine #1: "+ e.getMessage());
		}
	}


}
