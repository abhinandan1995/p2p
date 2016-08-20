import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpBaseServer {

	static TCPClient tcpc=null;
//	public static void main(String []args){
//		int port = 9004;
//		HttpServer server= null;
//		TCPServer tcps=null;
//		
//		try{
//			tcpc= new TCPClient();
//			tcps= new TCPServer(port-2000);
//			
//			server = HttpServer.create(new InetSocketAddress(port), 0);
//			System.out.println("server started at " + port);
//			server.createContext("/", new RootHandler(port));
//			server.createContext("/stop", new StopServer(port,server, tcps));
//			server.createContext("/get", new EchoGetHandler(port-2000,tcpc));
//			server.createContext("/post", new EchoPostHandler());
//			server.setExecutor(null);
//			server.start();
//			System.out.println("non-blocking");
//			
//			tcps.getServerInfo();
//			getInput();
//		}
//		catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//
//	}
	
	public static void getInput(){
		Scanner sc=new Scanner(System.in);
		String t= "hi";
		while(!t.equals("exit")){
			t=sc.nextLine();
			tcpc.sendReq(7004, Utilities.getIpAddress(), Utilities.makeRequest(t, "p2p"));
		}
	}
	
	public static void parseQuery(String query, Map<String, 
			Object> parameters) throws UnsupportedEncodingException {

		         if (query != null) {
		                 String pairs[] = query.split("[&]");
		                 for (String pair : pairs) {
		                          String param[] = pair.split("[=]");
		                          String key = null;
		                          String value = null;
		                          if (param.length > 0) {
		                          key = URLDecoder.decode(param[0], 
		                          	System.getProperty("file.encoding"));
		                          }

		                          if (param.length > 1) {
		                                   value = URLDecoder.decode(param[1], 
		                                   System.getProperty("file.encoding"));
		                          }

		                          if (parameters.containsKey(key)) {
		                                   Object obj = parameters.get(key);
		                                   if (obj instanceof List<?>) {
		                                            List<String> values = (List<String>) obj;
		                                            values.add(value);

		                                   } else if (obj instanceof String) {
		                                            List<String> values = new ArrayList<String>();
		                                            values.add((String) obj);
		                                            values.add(value);
		                                            parameters.put(key, values);
		                                   }
		                          } else {
		                                   parameters.put(key, value);
		                          }
		                 }
		         }
		}

}
class RootHandler implements HttpHandler {

	int port = 9000;

	RootHandler(int p){
		port = p;
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + port + "</h1>";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}

class StopServer implements HttpHandler {

	int port = 9000;
	HttpServer server= null;
	TCPServer tcps;
	StopServer(int p, HttpServer s){
		port = p;
		server= s;
	}
	
	StopServer(int p, HttpServer s, TCPServer ts){
		port = p;
		server= s;
		tcps=ts;
	}
	@Override
	public void handle(HttpExchange he) throws IOException {
		try{
			//tcps.getServerInfo();
			server.stop(0);
			tcps.stop();
			System.out.println("Server stopped at "+port);
		}
		catch(Exception e){
			System.out.println("Close: "+e.getMessage());
		}

	}

}

class EchoGetHandler implements HttpHandler {

	TCPClient tcpc;
	int port;
	EchoGetHandler(){
		
	}
	
	EchoGetHandler(int p,TCPClient tc){
		tcpc= tc;
		port=p;
	}
	
    @Override
    public void handle(HttpExchange he) throws IOException {
            // parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            HttpBaseServer.parseQuery(query, parameters);

            // send response
            String response = "";
            for (String key : parameters.keySet()){
                     response += key + " = " + parameters.get(key) + "\n";
            }
            
            tcpc.sendReq(port, "localhost", response);
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
    }
}

class EchoPostHandler implements HttpHandler {

    @Override

    public void handle(HttpExchange he) throws IOException {
            // parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            HttpBaseServer.parseQuery(query, parameters);

            // send response
            String response = "";
            for (String key : parameters.keySet())
                     response += key + " = " + parameters.get(key) + "\n";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
    }
}

