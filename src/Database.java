import java.util.List;
import java.util.Map;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

public class Database {

    public static void main(String[] args) {

        Handle handle = null;
        try{
        	
        Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e){
        	System.out.println(e.getMessage());
        }
        DBI dbi = new DBI("jdbc:mysql://localhost:3306/test",
                "root", "");
        
        
        String sql = "SELECT filename, match(filename) against ('girl' in natural language mode) as relevance from testquery WHERE match(filename) against ('girl' in natural language mode)";
        
        
        try {

            handle = dbi.open();
            Query<Map<String, Object>> q = handle.createQuery(sql);
            
            List<Map<String, Object>> l = q.list();

            for (Map<String, Object> m : l) {
            
                System.out.printf("%s ", m.get("relevance"));
                System.out.printf("%s \n", m.get("filename"));
               
            }

        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }
}