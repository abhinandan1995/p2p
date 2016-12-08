package p2pApp;

import java.util.ArrayList;

public class SearchTable {

	ArrayList<SearchResults> searchResults;
	private static SearchTable searchTable;
	private int searchId;
	
	private SearchTable(){
		searchResults= new ArrayList<SearchResults>();
	}
	
	public static SearchTable getInstance(){
		if(searchTable==null){
			searchTable= new SearchTable();
		}
		return searchTable;
	}
	
	public void addEntries(ArrayList<SearchResults> sr, String ip, String id){
		// check for active search id and then add the entries
		
		for(int i=0;i<sr.size();i++){
			SearchResults temp= sr.get(i);
			searchResults.add( new SearchResults(ip, temp.userid, temp.fileid, utility.Utilities.parseInvalidFilenames(temp.filename), temp.hash, temp.filesize));
		}
		
	}
	
	public ArrayList<SearchResults> getSearchTable(){
		return searchResults;
	}
	
	public int getNewSearchId(){
		return getNewSearchId(true);
	}
	
	public int getNewSearchId(boolean refresh){
		if(refresh)
			searchResults.clear();
		searchId=utility.Utilities.getRandomNumber();
		return searchId;
	}
	
	public SearchResults getFromSearchTable(int index){
		return searchResults.get(index);
	}
}
