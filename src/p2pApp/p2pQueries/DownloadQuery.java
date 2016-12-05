package p2pApp.p2pQueries;


public class DownloadQuery {

	public int downloadQueryId;
	public String mode;
	public String key;
	
	public DownloadQuery(String mode, String key){
		this.mode= mode;
		this.key= key;
	}
}
