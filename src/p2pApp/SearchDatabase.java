package p2pApp;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import p2pApp.p2pIndexer.DirectoryReader;
import p2pApp.p2pIndexer.TableHandler;
import p2pApp.p2pQueries.SearchQuery;
import utility.MySqlHandler;

public class SearchDatabase {

	SearchQuery searchQuery;
	int size=0;

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

		if(DirectoryReader.type.equals("MySQL")){
			List<Map<String, Object>> l;
			String key= searchQuery.data.replace("'", "''");	
			String TblName = "DirReader";
			l=MySqlHandler.getInstance().fetchQuery("SELECT FileID, FileName, Hash, FileSize, Type, match("+utility.Utilities.searchCol+") against ('"+key+"' in natural language mode) as relevance from "+ TblName+" WHERE Valid= '1' && match("+utility.Utilities.searchCol+") against ('"+key+"' in natural language mode)");

			ArrayList<SearchResults> al= new ArrayList<SearchResults>();
			for(int i=0;i<l.size();i++){
				al.add(new SearchResults("","",l.get(i).get("FileId").toString(), l.get(i).get("FileName").toString(), l.get(i).get("Hash").toString(), l.get(i).get("FileSize").toString(), l.get(i).get("Type").toString() ));
			}
			size= al.size();
			searchQuery=new SearchQuery(searchQuery.searchId, "results", "", al); 
			return;
		}

		if(DirectoryReader.type.equals("Lucene")){

			try {

				IndexReader reader = org.apache.lucene.index.DirectoryReader.open(FSDirectory.open(Paths.get(TableHandler.INDEX_DIRECTORY)));
				IndexSearcher searcher = new IndexSearcher(reader);
				Analyzer analyzer = new StandardAnalyzer();
				QueryParser mqp = new QueryParser("Path", analyzer);
				Query query = mqp.parse(searchQuery.data);//search the given keyword

				//System.out.println("query >> " + query);

				ScoreDoc[] hits = searcher.search(query, 100).scoreDocs; // run the query

				//System.out.println("Results found >> " +hits.length);

				ArrayList<SearchResults> al= new ArrayList<SearchResults>();
				for (int i = 0; i < hits.length && i<100; i++) {
					Document doc = searcher.doc(hits[i].doc);//get the next  document
					al.add(new SearchResults("", "", doc.get(TableHandler.columns[0]),
							doc.get(TableHandler.columns[1]), doc.get(TableHandler.columns[3]),
							doc.get(TableHandler.columns[4]), doc.get(TableHandler.columns[5])));
				}
				size= al.size();
				searchQuery=new SearchQuery(searchQuery.searchId, "results", "", al); 
				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getSize(){
		return size;
	}
}
