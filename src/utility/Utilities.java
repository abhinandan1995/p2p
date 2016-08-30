package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utilities{

	private static String TAG="Utilities ";
	private static String ipAddress="";
	private static String systemId="";
	public static int serverPort= 7000;
	public static int neighbourPeersCount= 3;
	public static int maxSimultaneousRequests= 3;
	public static int connectionTimeout= 2500;
	public static int maxHopCount=6;
	public static int maxparallelRequests= 20;
	
	public static String getIpAddress(){
		if(ipAddress==null || ipAddress.length()<=4){
			ipAddress=getLocalIpAddress();
		}
		return ipAddress;	
	}

	public static String getIpAddress(String baseIp){
		if(ipAddress==null || ipAddress.length()<=4 || !ipAddress.contains(baseIp)){
			ipAddress= getValidIpAddress(baseIp);
		}
		return ipAddress;
	}

	private static String getValidIpAddress(String baseIp){

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

	private static String getLocalIpAddress(){
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
		
		if(systemId!=null && !systemId.isEmpty())
			return systemId;
		byte[] mac;
		StringBuilder sb= new StringBuilder();
		int j=0;
		try{
			for(int k=0;k<100 && j<3;k++){

				NetworkInterface nif= NetworkInterface.getByIndex(k);
				if(nif==null || (mac=nif.getHardwareAddress())==null)
					continue;
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
				}
				if(mac.length>0)
					j++;
			}

			byte bytes[] = sb.toString().getBytes();
			Checksum checksum= new CRC32();
			checksum.update(bytes, 0, bytes.length);

			return (systemId=String.format("%x",checksum.getValue()));
		}		
		catch(Exception e){
			System.out.println(TAG+"#3 "+e.getMessage());
		}
		return null;
	}

	public static String getSystemMAC(){

		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(TAG+"#7 "+e.getMessage());
		} 
		return null;
	}

	public static String makeRequest(Object obj, String type){
		try{
			return getJsonString(new Query_v12(type, getJsonString(obj)));
		}
		catch(Exception e){
			System.out.println(TAG+"#4 "+e.getMessage());
		}
		return null;
	}

	public static String makeRequest(String data, String type){
		try{
			return getJsonString(new Query_v12(type, getJsonString(data)));
		}
		catch(Exception e){
			System.out.println(TAG+"#9 "+e.getMessage());
		}
		return null;
	}
	
	public static String makeRequest(String data, String module, String destId, String code, String sourcePort, String destPort, boolean res){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res,1);
	}
	
	public static String makeRequest(Object data, String module, String destId, String code, String sourcePort, String destPort, boolean res, String className){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res, className,1);
	}
	
	public static String makeRequest(String data, String module, String destId, String code, String sourcePort, String destPort, boolean res, int hopCount){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res, hopCount);
	}
	
	public static String makeRequest(Object data, String module, String destId, String code, String sourcePort, String destPort, boolean res, String className, int hopCount){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res, className, hopCount);
	}
	
	public static String makeRequest(int qid, String data, String module, String destId, String code, String sourcePort, String destPort, boolean res){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res,1);
	}
	
	public static String makeRequest(int qid, Object data, String module, String destId, String code, String sourcePort, String destPort, boolean res, String className){
		return makeRequest(getRandomNumber(), data, module, destId, code, sourcePort, destPort, res, className,1);
	}

	public static String makeRequest(int qid, String data, String module, String destId, String code, String sourcePort, String destPort, boolean res, int hopCount){
		try{
			return getJsonString(new Query_v12(qid, module, getJsonString(data), getSystemId(), destId, getIpAddress(), code, sourcePort, destPort, res, "string", hopCount));
		}
		catch(Exception e){
			System.out.println(TAG+"#9 "+e.getMessage());
		}
		return null;
	}
	
	public static String makeRequest(int qid, Object data, String module, String destId, String code, String sourcePort, String destPort, boolean res, String className, int hopCount){
		try{
			return getJsonString(new Query_v12(qid, module, getJsonString(data), getSystemId(), destId, getIpAddress(), code, sourcePort, destPort, res, className, hopCount));
		}
		catch(Exception e){
			System.out.println(TAG+"#9 "+e.getMessage());
		}
		return null;
	}
	
	public static String makeRequest(List list, String type){
		try{
			return getJsonString(new Query_v12(type, getJsonString(list)));
		}
		catch(Exception e){
			System.out.println(TAG+"#10 "+e.getMessage());
		}
		return null;
	}

	public static Query_v12 getQueryObject(String data){
		return Query_v12.class.cast(getObjectFromJson(data, Query_v12.class));
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

	public static String getBaseSystemId(){

		String result=null;

		try{
			String command = "ipconfig /all";
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader inn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			Pattern pattern = Pattern.compile(".*Physical Addres.*: (.*)");

			while (true) {
				String line = inn.readLine();

				if (line == null)
					break;

				Matcher mm = pattern.matcher(line);
				if (mm.matches()) {
					result=result+mm.group(1);
				}
			}

			byte bytes[] = result.getBytes();
			Checksum checksum= new CRC32();
			checksum.update(bytes, 0, bytes.length);
			return String.format("%x", checksum.getValue());
		}

		catch(Exception e){
			System.out.println(TAG+"#6 "+e.getMessage());
		}
		return null;
	}

	public static String getSlowerSystemId(){
		byte[] mac;
		StringBuilder sb= new StringBuilder();
		try{
			Enumeration<NetworkInterface> nic = NetworkInterface.getNetworkInterfaces();
			while(nic.hasMoreElements()){
				Enumeration<InetAddress> ia= nic.nextElement().getInetAddresses();
				while(ia.hasMoreElements()){
					NetworkInterface nid= NetworkInterface.getByInetAddress(ia.nextElement());
					mac= nid.getHardwareAddress();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
				}
			}

			byte bytes[] = sb.toString().getBytes();
			Checksum checksum= new CRC32();
			checksum.update(bytes, 0, bytes.length);

			return String.format("%x",checksum.getValue());
		}		
		catch(Exception e){
			System.out.println(TAG+"#8 "+e.getMessage());
		}
		return null;
	}
	
	public static int getRandomNumber(){
		return ThreadLocalRandom.current().nextInt(1000000,1000000000);
	}
}
