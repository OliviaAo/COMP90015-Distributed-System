
public class response {
	
	int response;	// 0 = success; 1 = error
	String errorMessage;
	
	
	//response constructor
	public response(){
		this.errorMessage = new String();
		this.response = 0;
	}
	

	//set remove responses
	public void setRemoveResponse(int responseCode){
		switch (responseCode) {
		case 1: errorMessage = "cannot remove resource";
				response = 1;
				break;
		case 2: errorMessage = "invalid resource";
				response = 1;
				break;
		case 3: errorMessage  = "missing resource";
				response = 1;
				break;
		default: errorMessage = "";
				break;
		}
		
	}
	
	
	//set fetch response
	public void setFetchResponse(int responseCode){
		switch (responseCode) {
		case 1: errorMessage = "invalid resourceTemplate";
				response = 1;
				break;
		case 2: errorMessage = "missing resourceTemplate";
				response = 1;
				break;
		default: errorMessage = "";
				break;
		}
	}
	
}
