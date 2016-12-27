import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import utility.MySqlHandler;


public class LuceneExample {
 
 public static final File INDEX_DIRECTORY = new File("IndexDirectory");
 
 public void createIndex() {
  
  System.out.println("-- Indexing --");
  
  try {
	  List<Map<String, Object>> l;
		
   MySqlHandler handler = MySqlHandler.getInstance();
  l= handler.fetchQuery("Select * from testQuery");
   //Lucene Section
   Directory directory = new SimpleFSDirectory(INDEX_DIRECTORY);
   Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
   IndexWriter iWriter = new IndexWriter(directory, analyzer, true,MaxFieldLength.UNLIMITED);
   
   //Looping through resultset and adding to index file
   int count = 0;
   while(true) {
    Document doc = new Document();
    doc.add(new Field("filename", l.get(count).get("filename").toString(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
    //Adding doc to iWriter
    iWriter.addDocument(doc);
    count++;
    if(count>=l.size())
    	break;
   }
   
   System.out.println(count+" record indexed");
   
   //Closing iWriter
   iWriter.optimize(); 
   iWriter.commit();
   iWriter.close();
   
   //Closing JDBC connection
   
  } catch (Exception e) {
   e.printStackTrace();
  }
  
 }
 
 public void search(String keyword) {
  
  System.out.println("-- Seaching --");
  
  try {
   
   //Searching
   IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_DIRECTORY), true);
   IndexSearcher searcher = new IndexSearcher(reader);
   Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
   //MultiFieldQueryParser is used to search multiple fields
   String files= "filename";
   QueryParser mqp = new QueryParser(Version.LUCENE_30, files , analyzer);
   
 //  Term term = new Term("filename",keyword);
//   //create the term query object
   //Query query = new FuzzyQuery(term, 0.5f);
   Query query = mqp.parse(keyword);//search the given keyword
   
   System.out.println("query >> " + query);
   
   TopDocs hits = searcher.search(query, 100); // run the query
   
   System.out.println("Results found >> " + hits.totalHits);
   
   for (int i = 0; i < hits.totalHits; i++) {
    Document doc = searcher.doc(hits.scoreDocs[i].doc);//get the next  document
    System.out.println(doc.get("filename"));
   }
   
   searcher.close();
  } catch (Exception e) {
   e.printStackTrace();
  }
  
  
  
 }


 public static void main(String[] args)  {


  LuceneExample obj = new LuceneExample();
  
  //creating index
  obj.createIndex();
  
  //searching keyword
  obj.search("ruchi");
  obj.search("sarchi");
obj.search("love song");
obj.search("haryl potter");
  //using wild card serach
  //obj.search("*ruchi");


//  //using logical operator
//  obj.search("data1 OR data2");
//  obj.search("data1 AND data2");
  
 }
}