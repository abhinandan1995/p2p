package tcpQueries;

import java.util.List;

import tcpUtilities.PeersEntries;

public class PingQuery {

	public String action;
	public String result;
	public List<PeersEntries> peers;
	
	public PingQuery(){
		action="ping";
		result=null;
		peers=null;
	}
	
	public PingQuery(String action, String result, List<PeersEntries> peers){
		this.action= action;
		this.result= result;
		this.peers= peers;
	}
	
}
