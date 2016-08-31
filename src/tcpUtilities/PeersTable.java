package tcpUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utility.Query_v12;

public class PeersTable{
	
	private static PeersTable peersInstance;
	private List<PeersEntries> peersEntries;
	private Set<String> peersSystemIds;
	private List<PeersEntries> neighbourPeers;
	
	private PeersTable(){
		peersEntries= new ArrayList<PeersEntries>();
		peersSystemIds= new HashSet<String>();
		neighbourPeers= new ArrayList<PeersEntries>();
	}
	
	public void addEntry(String i, String sid, String st, long t){
		if(peersSystemIds.contains(sid)){
			updateEntry(i, sid, st, t);
			return;
		}
		
		peersEntries.add(new PeersEntries(i, sid, st, t));
		peersSystemIds.add(sid);
	}
	
	public void addEntry(String i, String sid, String st){
		addEntry(i, sid, st, new Date().getTime());
	}
	
	public PeersEntries getBySystemId(String id){
		if(peersSystemIds.contains(id)){
			for(int i=0;i<peersEntries.size();i++){
				if(peersEntries.get(i).systemId.equals(id))
					return peersEntries.get(i);
			}
		}
		return null;
	}
	
	public static PeersTable getInstance(){
		if(peersInstance==null)
			peersInstance= new PeersTable();
			return peersInstance;
	}
	
	public List<PeersEntries> getConnected(){
		return getConnected(peersEntries.size());
	}
	
	public List<PeersEntries> getConnected(int size){
		List<PeersEntries> tempList=new ArrayList<PeersEntries>();
		for(int i=0; i<size;i++){
			if(peersEntries.get(i).status.equals("connected"))
				tempList.add(peersEntries.get(i));
		}
		return tempList;
	}
	
	public List<PeersEntries> getAll(){
		return peersEntries;
	}
	
	public void updateEntry(String ip, String sid, String st, long t){
		for(int i=0; i<peersEntries.size();i++){
			if(peersEntries.get(i).systemId.equals(sid)){
				if(peersEntries.get(i).time< t){
					peersEntries.get(i).time= t;
					peersEntries.get(i).ip= ip;
					peersEntries.get(i).status= st;
				}
			}
		}
	}
	
	public void updateEntry(String ip, String sid, String st){
		for(int i=0; i<peersEntries.size();i++){
			if(peersEntries.get(i).systemId.equals(sid)){
					peersEntries.get(i).ip= ip;
					peersEntries.get(i).status= st;
			}
		}
	}
	
	public void updateStatus(String sid, String st){
		for(int i=0; i<peersEntries.size();i++){
			if(peersEntries.get(i).systemId.equals(sid)){
					peersEntries.get(i).status= st;
			}
		}
	}
	
	public void updateStatusByIp(String ip, String st){
		for(int i=0; i<peersEntries.size();i++){
			if(peersEntries.get(i).ip.equals(ip)){
					peersEntries.get(i).status= st;
			}
		}
	}
	
	public void echoEntries(){
		for(int i=0;i<peersEntries.size(); i++){
			PeersEntries pe= peersEntries.get(i);
			System.out.println(pe.ip+" "+pe.systemId+" "+pe.status);
		}
	}
	
	public void echoEntries(String str, Object obj){
		System.out.println("hello");
		System.out.println(Query_v12.class.cast(obj).getSourceIp());
		echoEntries();
	}
	
	public List<PeersEntries> getNeighbourPeers(){
		return neighbourPeers;
	}
	
	public void addNeighbourPeers(String ip, String sid, String st, boolean force){
		addNeighbourPeers(ip, sid, st, new Date().getTime(), force);
	}
	
	public void addNeighbourPeers(String i, String sid, String st, long t, boolean force){
		if(neighbourPeers.size()<= utility.Utilities.neighbourPeersCount)
			neighbourPeers.add(new PeersEntries(i, sid, st, t));
		else{
			if(force){
				neighbourPeers.remove(0);
				neighbourPeers.add(new PeersEntries(i, sid, st, t));
			}
		}
	}
	
	public void remNeighbourPeerByIp(String ip){
		for(int i=0;i<neighbourPeers.size();i++){
			if(neighbourPeers.get(i).ip.equals(ip))
				neighbourPeers.remove(i);
		}
	}
	
	public boolean isNeighbourPresent(String sid){
		for(int i=0;i<neighbourPeers.size();i++){
			if(neighbourPeers.get(i).systemId.equals(sid))
				return true;
		}
		return false;
	}
	
	public void addEntryAndNeighbour(String ip, String sid, boolean force){
		addEntry(ip, sid, "connected");
		addNeighbourPeers(ip, sid, "connected", true);
	}
}

