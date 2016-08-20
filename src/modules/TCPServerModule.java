package modules;

import java.io.DataOutputStream;
import java.util.Date;

import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.Query_v12;

public class TCPServerModule {

	Query_v12 baseQuery;
	DataOutputStream output;
	
	public TCPServerModule(Query_v12 query, DataOutputStream output){
		this.baseQuery= query;
		this.output= output;
		
		processRequest();
	}
	
	private String getResType(){
		return baseQuery.getResponseType();
	}
	
	private void processRequest(){
		String type= getResType();
		if(type.equals("string")){
			echoString();
		}
		else{
			if(type.contains("PingQuery")){
				new PingModule(baseQuery, output);
			}
		}
	}
	
	private void echoString(){
		BaseController.getInstance().sendResponse(baseQuery.getPayload(), baseQuery.getModule(), "string", false, baseQuery.getSourceSid(), output);
	}
}

class PingModule{
	
	Query_v12 baseQuery;
	DataOutputStream output;
	PingModule(Query_v12 q, DataOutputStream o){
		baseQuery=q;
		output=o;
		
		try{
			getBaseObject();
		}
		catch(ClassNotFoundException ce){
			System.out.println("Not found class: "+ce.getMessage());
		}
	}
	
	private void getBaseObject() throws ClassNotFoundException{
		PingQuery pq= PingQuery.class.cast(utility.Utilities.getObjectFromJson(baseQuery.getPayload(), Class.forName("tcpQueries.PingQuery")));
		processPingQuery(pq);
	}
	
	private void processPingQuery(PingQuery pq){
		
		if(pq.action.equals("ping")){
			PeersTable.getInstance().addEntry(baseQuery.getSourceIp(), baseQuery.getSourceSid(), "connected",new Date().getTime());
			if(output==null)
				return;
			BaseController.getInstance().sendResponse(new PingQuery("pong","valid",PeersTable.getInstance().getAll()), "tcp-server", "PingQuery", false, baseQuery.getSourceSid(), output);
			return;
		}
		
		if(pq.action.equals("ping-stop")){
			PeersTable.getInstance().addEntry(baseQuery.getSourceIp(), baseQuery.getSourceSid(), "disconnected",new Date().getTime());
			if(output==null)
				return;
			BaseController.getInstance().sendResponse(new PingQuery("pong","valid",PeersTable.getInstance().getAll()), "tcp-server", "PingQuery", false, baseQuery.getSourceSid(), output);
			return;
		}
		
		if(pq.action.equals("ping-updates")){
			PeersEntries pe;
			for(int i=0;i<pq.peers.size();i++){
				pe= pq.peers.get(i);
				PeersTable.getInstance().addEntry(pe.ip, pe.systemId, pe.status, pe.time);
			}
			
			if(output==null)
				return;
			BaseController.getInstance().sendResponse(new PingQuery("pong","valid",PeersTable.getInstance().getAll()), "tcp-server", "PingQuery", false, baseQuery.getSourceSid(), output);
			return;
		}
		
		if(pq.action.equals("ping-point")){
			if(utility.Utilities.getSystemId()==baseQuery.getDestSid()){
				
			}
		}
	}
}

