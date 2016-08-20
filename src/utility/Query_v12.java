package utility;

public class Query_v12 {
	int queryId;
	String module;
	String code;
	String payload;
	String sourceSid;
	String destSid;
	String sourceIp;
	String sourcePort;
	String destPort;
	boolean response;
	String rtype;
	
	Query_v12(String m, String p){
		this(m,p,"");
	}

	Query_v12(String m, String p, String c){
		module=m;
		payload=p;
		code=c;
	}
	
	Query_v12(String m, String p, String ss, String ds, String si){
		this(m,p,ss,ds,si,"","","");
	}
	
	Query_v12(String m, String p, String ss, String ds, String si, String sp, String dp){
		this(m,p,ss,ds,si,"",sp,dp);
	}
	
	Query_v12(String m, String p, String ss, String ds, String si, String sp){
		this(m,p,ss,ds,si,"",sp,"");
	}

	Query_v12(String m, String p, String ss, String ds, String si, String c, String sp, String dp){
		module=m;
		payload=p;
		sourceSid=ss;
		destSid=ds;
		sourceIp=si;
		code=c;
		sourcePort=sp;
		destPort= dp;
		response= true;
	}
	
	Query_v12(String m, String p, String ss, String ds, String si, String c, String sp, String dp, boolean r, String rt){
		module=m;
		payload=p;
		sourceSid=ss;
		destSid=ds;
		sourceIp=si;
		code=c;
		sourcePort=sp;
		destPort= dp;
		response= r;
		rtype= rt;
	}
	
	Query_v12(int id, String m, String p, String ss, String ds, String si, String c, String sp, String dp, boolean r, String rt){
		queryId=id;
		module=m;
		payload=p;
		sourceSid=ss;
		destSid=ds;
		sourceIp=si;
		code=c;
		sourcePort=sp;
		destPort= dp;
		response= r;
		rtype= rt;
	}
	public String getModule(){
		return module;
	}
	
	public String getPayload(){
		return payload;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getSourceSid(){
		return sourceSid;
	}
	
	public String getDestSid(){
		return destSid;
	}
	
	public String getSourceIp(){
		return sourceIp;
	}
	
	public String getSourcePort(){
		return sourcePort;
	}
	
	public String getDestPort(){
		return destPort;
	}
	
	public boolean getResponse(){
		return response;
	}
	
	public void setResponse(boolean x){
		response=x;
	}
	
	public void setResponseType(String t){
		rtype=t;
	}
	
	public void setQueryId(int i){
		queryId=i;
	}
	
	public int getQueryId(){
		return queryId;
	}
	
	public String getResponseType(){
		return rtype;
	}
	
	public void setRandomId(){
		queryId= utility.Utilities.getRandomNumber();
	}
}
