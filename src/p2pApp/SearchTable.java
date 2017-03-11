package p2pApp;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTable {

	ArrayList<SearchResults> searchResults;
	HashMap<String, Integer> existingHashes;
	private static SearchTable searchTable;
	private int searchId;
	
	private SearchTable(){
		searchResults= new ArrayList<SearchResults>();
		existingHashes= new HashMap<String, Integer>();
	}
	
	public static SearchTable getInstance(){
		if(searchTable==null){
			searchTable= new SearchTable();
		}
		return searchTable;
	}
	
	public void addEntries(ArrayList<SearchResults> sr, String ip, int id){
		// check for active search id and then add the entries
		
		if(id!=searchId)
			return;
		
		for(int i=0;i<sr.size();i++){
			SearchResults temp= sr.get(i);
			addNew( new SearchResults(ip, temp.userid, temp.fileid, utility.Utilities.parseInvalidFilenames(temp.filename), temp.hash, temp.filesize, temp.type));
		}
		
	}
	
	public ArrayList<SearchResults> getSearchTable(){
		return searchResults;
	}
	
	public int getNewSearchId(){
		return getNewSearchId(true);
	}
	
	public int getNewSearchId(boolean refresh){
		if(refresh){
			searchResults.clear();
			existingHashes.clear();
		}
		searchId=utility.Utilities.getRandomNumber();
		return searchId;
	}
	
	public SearchResults getFromSearchTable(int index){
		return searchResults.get(index);
	}
	
	synchronized private void addNew(SearchResults sr){
		if(sr.hash!= null && existingHashes.containsKey(sr.hash)){
			searchResults.get(existingHashes.get(sr.hash)).addAlternateIps(sr.ip, sr.fileid, sr.filename, sr.filesize, sr.userid);
			return;
		}
		int index= searchResults.size();
		searchResults.add(sr);
		existingHashes.put(sr.hash, index);
		return ;
	}
}
