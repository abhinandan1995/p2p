package p2pApp;

import java.util.ArrayList;

public class SearchQuery {

	public int searchId;
	public String key;
	ArrayList<String> results;
	public String mode;
	
	public SearchQuery(int searchId, String mode, String key, ArrayList<String> results){
		this.searchId= searchId;
		this.mode= mode;
		this.key= key;
		this.results = results;
	}
}
