package p2pApp.p2pQueries;

import java.util.ArrayList;

import p2pApp.SearchResults;

public class SearchQuery {

	public int searchId;
	public String data;
	public ArrayList<SearchResults> results;
	public String mode;
	
	public SearchQuery(int searchId, String mode, String data, ArrayList<SearchResults> results){
		this.searchId= searchId;
		this.mode= mode;
		this.data= data;
		this.results = results;
	}
}
