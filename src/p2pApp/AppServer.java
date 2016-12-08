package p2pApp;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import p2pApp.p2pDownloader.DownloadResponse;
import p2pApp.p2pIndexer.TableHandler;
import p2pApp.p2pQueries.DownloadQuery;
import p2pApp.p2pQueries.SearchQuery;
import tcpServer.BaseController;
import ui.UISearch;
import utility.MySqlHandler;
import utility.Query_v12;
import baseServer.BaseNetworkEngine;
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
	
		}
		else{
			if(query.getResponseType().equals("SearchQuery")){
				
				searchQuery= (SearchQuery) utility.Utilities.getObjectFromJson(query.getPayload(), SearchQuery.class);
				if(searchQuery.mode.equals("search")){
					BaseNetworkEngine.getInstance().forwardRequests(query);
					searchDatabase(searchQuery);
				}
				if(searchQuery.mode.equals("results")){
					SearchTable.getInstance().addEntries(searchQuery.results, query.getSourceIp(),query.getSourceSid());
					echoResults(SearchTable.getInstance().getSearchTable());
					UISearch.updateTable(SearchTable.getInstance().getSearchTable());
				}
			}
			
			if(query.getResponseType().equals("DownloadQuery")){
				DownloadQuery dq= (DownloadQuery)utility.Utilities.getObjectFromJson(query.getPayload(), DownloadQuery.class);
				String path= TableHandler.getFilePath(dq.key);
				new DownloadResponse(path, output);
			}
		}
		
	}

	public void searchDatabase(SearchQuery searchQuery){
		List<Map<String, Object>> l;
		//ArrayList<String[]> as= new ArrayList<String[]>();
		//as.add(new String[]{"another love song"});
		//MySqlHandler.getInstance().insertMultiple("testquery", new String[]{"filename"}, as);
		
		String key= searchQuery.data;
		
		String TblName = "DirReader";
		l=MySqlHandler.getInstance().fetchQuery("SELECT FileID, FileName, Hash, FileSize, match(FileName) against ('"+key+"' in natural language mode) as relevance from "+ TblName+" WHERE match(FileName) against ('"+key+"' in natural language mode)");
		
		ArrayList<SearchResults> al= new ArrayList<SearchResults>();
		for(int i=0;i<l.size();i++){
			al.add(new SearchResults("","",l.get(i).get("FileId").toString(), l.get(i).get("FileName").toString(), l.get(i).get("Hash").toString(), l.get(i).get("FileSize").toString() ));
		}
		SearchQuery data=new SearchQuery(searchQuery.searchId, "results", "", al); 
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
	
	private void echoResults(ArrayList<SearchResults> al){
		for(int i=0; i<al.size();i++){
			System.out.println(i+" "+al.get(i).ip+" "+ al.get(i).filename+ " "+al.get(i).filesize +" "+al.get(i).userid );
		}
	}

}
