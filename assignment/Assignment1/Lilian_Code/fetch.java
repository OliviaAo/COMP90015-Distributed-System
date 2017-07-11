
public class fetch {
	
	//return a "response" for FETCH method
	public static response FETCH(resourceTemplate ResourceTemplate){
		
		//create new response
		response Response = new response();
		resource Resource = new resource();
		
		//check cases
		if (resourceTemplate.invalid = true){
			Response.setFetchResponse(1);		
			return Response;
		} else if (resourceTemplate.typeError = true){
			Response.setFetchResponse(2);
			return Response;			
		}
		
		//get resource
		Resource.getResource();
		Resource.getSize();
		
		Response.setFetchResponse(0);
		return Response;
	}

}
