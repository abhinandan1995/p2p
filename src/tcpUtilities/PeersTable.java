package tcpUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeersTable{
	
	private static PeersTable peersInstance;
	private List<PeersEntries> peersEntries;
	private Set<String> peersSystemIds;
	
	private PeersTable(){
		peersEntries= new ArrayList<PeersEntries>();
		peersSystemIds= new HashSet<String>();
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
		addEntry(i, sid, st, 0);
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
	
	public void echoEntries(){
		for(int i=0;i<peersEntries.size(); i++){
			PeersEntries pe= peersEntries.get(i);
			System.out.println(pe.ip+" "+pe.systemId+" "+pe.status);
		}
	}
}

