package p2pApp;

import java.io.DataOutputStream;
import java.util.ArrayList;

import baseServer.BaseNetworkEngine;
import p2pApp.p2pDownloader.DownloadResponse;
import p2pApp.p2pIndexer.TableHandler;
import p2pApp.p2pQueries.DownloadQuery;
import p2pApp.p2pQueries.SearchQuery;
import tcpServer.BaseController;
import ui.UISearch;
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

		if(query.getResponseType().equals("SearchQuery")){
			searchQuery= (SearchQuery) utility.Utilities.getObjectFromJson(query.getPayload(), SearchQuery.class);

			if(searchQuery.mode.equals("search")){
				BaseNetworkEngine.getInstance().forwardRequests(query);
				sendResults(new SearchDatabase(searchQuery).getResults());
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

	private void sendResults(Object data){
		try{
			BaseController.getInstance().sendRequest(data, query.getModule(), "SearchQuery", false, "", query.getSourceIp(), 1);
		}
		catch(Exception e){
			System.out.println("AppServer #1 "+e.getMessage());
		}
	}

	private void echoResults(ArrayList<SearchResults> al){
		for(int i=0; i<al.size();i++){
			System.out.println(i+" "+al.get(i).ip+" "+ al.get(i).filename+ " "+al.get(i).filesize +" "+al.get(i).userid );
		}
	}

}
