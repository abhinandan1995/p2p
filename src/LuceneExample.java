import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utility.MySqlHandler;


public class LuceneExample {
 
 public static final String INDEX_DIRECTORY = "LuceneIndex";
 
 public void createIndex() {
  
  System.out.println("-- Indexing --");
  
  try {
	  List<Map<String, Object>> l;
		
   MySqlHandler handler = MySqlHandler.getInstance();
  l= handler.fetchQuery("Select * from dirreader");
   //Lucene Section
  Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
  Analyzer analyzer = new StandardAnalyzer();
  IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
  iwc.setOpenMode(OpenMode.CREATE);
  IndexWriter writer = new IndexWriter(dir, iwc);
   //Looping through resultset and adding to index file
   int count = 0;
   while(true) {
    Document doc = new Document();
  //  doc.add(new Field("filename", l.get(count).get("filename").toString(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
    Field pathField = new StringField("filename", l.get(count).get("filename").toString(), Field.Store.YES);
    System.out.println(l.get(count).get("filename").toString());
    doc.add(pathField);
    Field path= new StringField("path", l.get(count).get("Path").toString(), Field.Store.YES);
    doc.add(path);
    writer.addDocument(doc);
    count++;
    if(count>=l.size())
    	break;
   }
   
   System.out.println(count+" record indexed");
   
   //Closing iWriter
   //writer.commit();
   writer.close();
   
   //Closing JDBC connection
   
  } catch (Exception e) {
   e.printStackTrace();
  }
  
 }
 
 public void search(String keyword) {
  
  System.out.println("-- Seaching --");
  
  try {
   
   //Searching
	  IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
	   IndexSearcher searcher = new IndexSearcher(reader);
//	      Analyzer analyzer = new StandardAnalyzer();
//   //MultiFieldQueryParser is used to search multiple fields
//   String files= "path";
//   QueryParser mqp = new QueryParser(files , analyzer);
//   
// //  Term term = new Term("filename",keyword);
////   //create the term query object
//   //Query query = new FuzzyQuery(term, 0.5f);
//   Query query = mqp.parse(keyword);//search the given keyword
//   
//   System.out.println("query >> " + query);
//   
//   ScoreDoc[] hits = searcher.search(query, 100).scoreDocs; // run the query
//   
//   System.out.println("Results found >> " +hits.length);
//   
//   for (int i = 0; i < hits.length && i<100; i++) {
//    Document doc = searcher.doc(hits[i].doc);//get the next  document
//    System.out.println(doc.get("path") + hits[i].score);
//   }
   
   WildcardQuery pq= new WildcardQuery(new Term("filename", "f:/animes/hyouka/Hyouka 1-10*"));
	System.out.println(pq.toString());
	ScoreDoc[] hits = searcher.search(pq, 100).scoreDocs; // run the query
	for (int i = 0; i < hits.length && i<100; i++) {
	    Document doc = searcher.doc(hits[i].doc);//get the next  document
	    System.out.println(doc.get("path") + hits[i].score);
	   }
   
  } catch (Exception e) {
   e.printStackTrace();
  }
  
  
  
 }


 public static void main(String[] args)  {


  LuceneExample obj = new LuceneExample();
  
  //creating index
 // obj.createIndex();
  
  //searching keyword
  Scanner in= new Scanner(System.in);
  
  while(true){
  String str= in.nextLine();
  obj.search(str);
  if(in.equals("close"))
	  break;
  }
  
  in.close();
  //using wild card serach
  //obj.search("abhi");


//  //using logical operator
//  obj.search("data1 OR data2");
//  obj.search("data1 AND data2");
  
 }
}