package p2pApp.p2pQueries;

import java.util.ArrayList;

public class SearchQuery {

	public int searchId;
	public String data;
	public ArrayList<String> results;
	public String mode;
	
	public SearchQuery(int searchId, String mode, String data, ArrayList<String> results){
		this.searchId= searchId;
		this.mode= mode;
		this.data= data;
		this.results = results;
	}
}
