package p2pApp;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tcpServer.BaseController;
import utility.MySqlHandler;
import utility.Query_v12;

public class AppServer {

	Query_v12 query;
	DataOutputStream output;
	SearchQuery searchQuery;
	
	public AppServer(Query_v12 query, DataOutputStream output){
		this.query= query;
		this.output= output;
		
		processRequest();
	}
	
	private void processRequest(){
	
		if(query.getResponseType().equals("string")){
			searchDatabase(query.getPayload());
		}
		else{
			if(query.getResponseType().equals("SearchQuery")){
				
				searchQuery= (SearchQuery) utility.Utilities.getObjectFromJson(query.getPayload(), SearchQuery.class);
				if(searchQuery.mode.equals("search")){
					searchDatabase(searchQuery.key);
				}
				if(searchQuery.mode.equals("results")){
					echoResults(searchQuery.results);
				}
			}
		}
	}

	public void searchDatabase(String key){
		List<Map<String, Object>> l;
		//ArrayList<String[]> as= new ArrayList<String[]>();
		//as.add(new String[]{"another love song"});
		//MySqlHandler.getInstance().insertMultiple("testquery", new String[]{"filename"}, as);
		
		l=MySqlHandler.getInstance().fetchQuery("SELECT filename, match(filename) against ('"+key+"' in natural language mode) as relevance from testquery WHERE match(filename) against ('"+key+"' in natural language mode)");
		ArrayList<String> al= new ArrayList<String>();
		for(int i=0;i<l.size();i++){
			al.add(l.get(i).get("filename").toString());
		}
		SearchQuery data=new SearchQuery(123, "results", "", al); 
		sendResults(data);
	}
	
	public void sendResults(Object data){
		
		try{
			BaseController.getInstance().sendRequest(data, query.getModule(), "SearchQuery", false, "", query.getSourceIp(), 1);
		}
		catch(Exception e){
			System.out.println("AppServer "+e.getMessage());
		}
	}
	
	private void echoResults(ArrayList<String> al){
		for(int i=0; i<al.size();i++){
			System.out.println(al.get(i));
		}
	}

}
