package p2pApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import p2pApp.p2pQueries.SearchQuery;
import utility.MySqlHandler;

public class SearchDatabase {

	SearchQuery searchQuery;
	
	public SearchDatabase(SearchQuery sq){
		this.searchQuery= sq;
	}
	
	public SearchQuery getResults(){
		
		switch(searchQuery.type){
		
		case "keyword" :
			searchByKeyword();
			break;
		case "fileid" :
			break;
		}
		return searchQuery;
	}
	
	private void searchByKeyword(){
		
		List<Map<String, Object>> l;
		String key= searchQuery.data.replace("'", "''");	
		String TblName = "DirReader";
		l=MySqlHandler.getInstance().fetchQuery("SELECT FileID, FileName, Hash, FileSize, Type, match(FileName) against ('"+key+"' in natural language mode) as relevance from "+ TblName+" WHERE Valid= '1' && match(FileName) against ('"+key+"' in natural language mode)");
		
		ArrayList<SearchResults> al= new ArrayList<SearchResults>();
		for(int i=0;i<l.size();i++){
			al.add(new SearchResults("","",l.get(i).get("FileId").toString(), l.get(i).get("FileName").toString(), l.get(i).get("Hash").toString(), l.get(i).get("FileSize").toString() ));
		}
		searchQuery=new SearchQuery(searchQuery.searchId, "results", "", al); 
	}
}
