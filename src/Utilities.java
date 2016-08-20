import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Utilities{

	private static String TAG="Utilities ";
	
	public static String getValidIpAddress(String baseIp){

		try{
			String addr= InetAddress.getLocalHost().toString();
			if(addr.contains(baseIp))
				return stripSlashesFromIp(addr, baseIp);

			List<InterfaceAddress> li;
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
			while(ni.hasMoreElements()){
				li= ni.nextElement().getInterfaceAddresses();
				int i=0;
				while(i<li.size()){
					if((addr=li.get(i++).getAddress().toString()).contains(baseIp))
						return stripSlashesFromIp(addr, baseIp);
				}
			}
		}
		catch(Exception e){
			System.out.println(TAG+"#1 "+e.getMessage());
		}
		return null;
	}
	
	private static String stripSlashesFromIp(String addr, String base){
		return addr.substring(addr.indexOf(base));
	}
	
	public static String getIpAddress(){
		try{
			String t= InetAddress.getLocalHost().toString();
			if(t.indexOf("/")==-1)
				return t;
			return t.substring(t.indexOf("/")+1);
		}
		catch(Exception e){
			System.out.println(TAG+"#2 "+e.getMessage());
		}
		return null;
	}
	
	public static String getSystemId(){
		try{
			return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress()).getHardwareAddress().toString().substring(3);
		}
		catch(Exception e){
			System.out.println(TAG+"#3 "+e.getMessage());
		}
		return null;
	}
	
	public static class Query{
		String module;
		String code;
		String payload;
		
		Query(String m, String p){
			this(m,p,"");
		}
		
		Query(String m, String p, String c){
			module=m;
			payload=p;
			code=c;
		}
	}

	public static String makeRequest(Object obj, String type){
		try{
			return getJsonString(new Query(type, getJsonString(obj)));
		}
		catch(Exception e){
			System.out.println(TAG+"#4 "+e.getMessage());
		}
		return null;
	}
	
	public static String makeRequest(String data, String type){
		try{
			return getJsonString(new Query(type, getJsonString(data)));
		}
		catch(Exception e){
			System.out.println(TAG+"#4 "+e.getMessage());
		}
		return null;
	}
	
	public static String makeRequest(List list, String type){
		try{
			return getJsonString(new Query(type, getJsonString(list)));
		}
		catch(Exception e){
			System.out.println(TAG+"#4 "+e.getMessage());
		}
		return null;
	}
	
	public static Query getQueryObject(String data){
		return Query.class.cast(getObjectFromJson(data, Query.class));
	}
	
	public static String getJsonString(Object obj){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(obj);
    }

    public static Object getObjectFromJson(String jsonString, Class toClass){  
        try {
			return new Gson().fromJson(jsonString, toClass);
			
		} catch (Exception e) {
			System.out.println(TAG+"#5 "+e.getMessage());
		}
        return null;
    }

}
