package modules;

import java.io.DataOutputStream;
import java.util.Date;

import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.Query_v12;

public class TCPClientModule {

	Query_v12 baseQuery;
	DataOutputStream output;
	
	public TCPClientModule(Query_v12 query, DataOutputStream output){
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
				new PongModule(baseQuery, output);
			}
		}
	}
	
	private void echoString(){
		BaseController.getInstance().sendResponse(baseQuery.getPayload(), baseQuery.getModule(), "string", false, baseQuery.getSourceSid(), output);
	}
}

class PongModule{
	
	Query_v12 baseQuery;
	DataOutputStream output;
	PongModule(Query_v12 q, DataOutputStream o){
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
		processPongQuery(pq);
	}
	
	private void processPongQuery(PingQuery pq){
		
		if(pq.action.equals("pong")){
			PeersEntries pe;
			for(int i=0;i<pq.peers.size();i++){
				pe= pq.peers.get(i);
				PeersTable.getInstance().addEntry(pe.ip, pe.systemId, pe.status, pe.time);
			}
			PeersTable.getInstance().addEntry(baseQuery.getSourceIp(), baseQuery.getSourceSid(), "connected");
			PeersTable.getInstance().echoEntries();
		}
		
		if(pq.action.equals("pong-message")){
			System.out.println(pq.getExtraData());
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
				if(output==null)
					return;
				BaseController.getInstance().sendResponse(new PingQuery("pong-point","match",null), "tcp-server", "PingQuery", false, baseQuery.getSourceSid(), output);
				return;
			}
			else{
				if(output==null)
					return;
				BaseController.getInstance().sendResponse(new PingQuery("pong-point","mismatch", PeersTable.getInstance().getAll()), "tcp-server", "PingQuery", false, baseQuery.getSourceSid(), output);
				return;
			}
		}
		
		if(pq.action.equals("pong-point")){
			if(pq.result.equals("match"))
			PeersTable.getInstance().addEntry(baseQuery.getSourceIp(), baseQuery.getSourceSid(), "connected",new Date().getTime());
			if(output==null)
				return;
		}
			
		if(output!=null)
		new ErrorModule(baseQuery, output, "Invalid cases for pong query");
	}
}

