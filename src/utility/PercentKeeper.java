package utility;

public class PercentKeeper {

	int percent=0;
	public PercentKeeper(int val){
		percent= val;
	}
	
	public PercentKeeper(){
		percent= 0;
	}
	
	public void setVal(int val){
		percent= val;
	}
	
	public int getVal(){
		return percent;
	}
}
