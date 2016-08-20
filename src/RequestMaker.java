import com.google.gson.Gson;

public class RequestMaker<T> {
	
	public RequestMaker(){
		
	}
	public boolean addToRequest(T data){
		
		return false;
	}
	
	private String getType(T data){
		return data.getClass().getSimpleName();
	}
}
