package utility;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MySqlHandler {

	private static MySqlHandler mysqlHandler;
	private String dbName="";
	private MysqlDataSource dataSource;
	private DBI dbi;
	
	private MySqlHandler(){
		try{
			loadDatabase(null);
		}
		catch(Exception e){
			System.out.println("Database Exception #1:"+ e.getMessage());
		}
	}
	
	public static MySqlHandler getInstance(){
		if(mysqlHandler==null)
			mysqlHandler= new MySqlHandler();
		return mysqlHandler;
	}
	
	public MysqlDataSource getProperties() throws Exception{
		Properties props = new Properties();
        FileInputStream fis = null;
        fis = new FileInputStream("data/db-ms.properties");
        props.load(fis);
		return getProperties(props.getProperty("mysql.database"));
	}
	
	public MysqlDataSource getProperties(String dbName) throws Exception{
		Properties props = new Properties();
        FileInputStream fis = null;
        MysqlDataSource ds = null;

        fis = new FileInputStream("data/db-ms.properties");
        props.load(fis);
        
        ds = new MysqlConnectionPoolDataSource();
        String url= props.getProperty("mysql.url")+":"+props.getProperty("mysql.port")+"/"+dbName+"?"+props.getProperty("mysql.unicode");
        ds.setURL(url);
        ds.setUser(props.getProperty("mysql.username"));
        
        String pass=props.getProperty("mysql.password");
        if(pass==null)
        	pass="";
        ds.setPassword(pass);

        this.dbName= dbName;
        
        return ds;
	}
	
	public String getDatabase(){
		return dbName;
	}
	
	public void loadDatabase(String dbName) throws Exception{
		if(this.dbName.equals(dbName)){
			return;
		}
		
		if(dbName==null)
			dataSource= getProperties();
		else
			dataSource= getProperties(dbName);
		
		Class.forName("com.mysql.jdbc.Driver");
    	dbi = new DBI(dataSource);
	}
	
	public List<Map<String, Object>> fetchQuery(String sql){
		
		Handle handle = null;
        
        List<Map<String, Object>> l=new ArrayList<Map<String, Object>>();
        
        try {
            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            l = q.list();
        } 
        catch(Exception e){
        	System.out.println("Database Exception #2: "+e.getMessage());
        }
        finally {
            if (handle != null) {
                handle.close();
            }
        }
        return l;
	}
	
	public void insertSingle(String tblName, String[] columns, String[] values){
		Handle handle= null;
		
		try {
            handle = dbi.open();
            
            String cms= "";
			for(int i=0; i<columns.length;i++){
				if(i==0) 
					cms= columns[i];
				else
					cms= cms+", "+columns[i];
			}
			
			String vs= "";
			for(int j=0; j<values.length;j++){
				if(j==0) 
					vs= "'"+values[j]+"'";
				else
					vs= vs+", "+"'"+values[j]+"'";
			}
			
            handle.execute("INSERT into "+tblName+" ( "+ cms+")" +"VALUES " +"( "+vs+")");
        } 
        catch(Exception e){
        	System.out.println("Database Exception #3: "+e.getMessage());
        }
        finally {
            if (handle != null) {
                handle.close();
            }
        }
		
	}
	
	public void insertMultiple(String tblName, String[] columns, List<String[]> values){
		
		Handle handle = null;
		try{
			handle= dbi.open();
			Batch batch= handle.createBatch();
			String cms= "";
			for(int i=0; i<columns.length;i++){
				if(i==0) 
					cms= columns[i];
				else
					cms= cms+", "+columns[i];
			}
			
			for(int i=0;i<values.size();i++){
				
				String vs= "";
				String temp[]= values.get(i);
				for(int j=0; j<temp.length;j++){
					if(j==0) 
						vs= "'"+temp[j]+"'";
					else
						vs= vs+", "+"'"+temp[j]+"'";
				}
				
				batch.add("INSERT into "+tblName+" ("+cms+")" + "VALUES "+ "("+ vs+")");
			}
			batch.execute();
		}
		catch(Exception e){
			System.out.println("Database Exception #4: "+e.getMessage());
		}
		finally {
            if (handle != null) {
                handle.close();
            }
        }
	}
	
	public void createTable(String tblName, String[] data, String engine, boolean force){
		
		if(engine==null)
			engine= "MyISAM";
		
		Handle handle= null;
		try {
            handle = dbi.open();
            String cms= "";
			for(int i=0; i<data.length;i++){
				if(i==0) 
					cms= data[i];
				else
					cms= cms+", "+data[i];
			}
			if(force)
				handle.execute("DROP TABLE IF EXISTS "+tblName);
            handle.execute("CREATE table "+tblName+" ( "+ cms+") ENGINE="+engine);
        } 
		
        catch(Exception e){
        	System.out.println("Database Exception #5: "+e.getMessage());
        }
        finally {
            if (handle != null) {
                handle.close();
            }
        }
	}
	
	public void updateTable(String tblName, String setVariable, String setValue, String whereVar, String whereVal){
		
		Handle handle= null;
		try {
            handle = dbi.open();
            handle.execute("UPDATE "+tblName+" SET "+setVariable+" = "+ "'"+ setValue+ "'" +" WHERE "+whereVar+ "= "+ "'"+ whereVal +"'");
        } 
        catch(Exception e){
        	System.out.println("Database Exception #3: "+e.getMessage());
        }
        finally {
            if (handle != null) {
                handle.close();
            }
        }
	}
	
}
