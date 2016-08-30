package baseServer;

import java.util.List;

import tcpClient.TCPClient;
import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.Query_v12;

public class BaseNetworkEngine {

	static BaseNetworkEngine baseEngineInstance;
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
				pt.addNeighbourPeers(pe.get(i).ip, pe.get(i).systemId, pe.get(i).status, false);
				BaseController.getInstance().sendRequest(new PingQuery("ping",null,null), "tcp-server", "PingQuery", true, "", pe.get(i).ip);
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
	
	public void sendMultipleRequests(Object obj, String module, String rtype, boolean response, String destId, String destIp){
		sendMultipleRequests(obj, module, rtype, response, destId, destIp, utility.Utilities.maxHopCount);
	}
	
	public void sendMultipleRequests(Object obj, String module, String rtype, boolean response, String destId, String destIp, int hopCount){
		BaseController bControl= BaseController.getInstance();
		for(int i=0; i<peersTable.getNeighbourPeers().size() && i<utility.Utilities.maxSimultaneousRequests; i++){
			bControl.sendRequest(obj, module, rtype, response, destId, destIp, hopCount);
		}
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

}
