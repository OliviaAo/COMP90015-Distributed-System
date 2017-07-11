
public class remove {
	
	//return a "response" for REMOVE method
	public static response REMOVE(resource Resource){
		
		//create new response
		response Response = new response();
		
		//check cases
		if (resource.exist = false){
			Response.setRemoveResponse(1);		
			return Response;
		} else if (resource.typeError = true){
			Response.setRemoveResponse(2);
			return Response;			
		} else if (resource.field = false){
			Response.setRemoveResponse(3);
			return Response;
		}
		
		Response.setRemoveResponse(0);
		return Response;
	}

}
