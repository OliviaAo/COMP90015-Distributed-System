
public class resource {

	String resource;
	int size;
	
	public resource() {
		this.resource = new String();
		this.size = 0;
	}
	
	//check if resource exists
	public static boolean exist;
	
	//check if resource contained incorrect information
	public static boolean typeError;
	
	//check if resource field was not given or not of correct type
	public static boolean field;

	//get the resource from server
	public String getResource() {
		return resource;
	}
	
	public int getSize() {
		return size;
	}
	
}
