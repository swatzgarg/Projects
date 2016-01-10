package Database;

import java.util.HashMap;

import model.KeyValueProtocolResponse;
import model.KeyValueProtocolVerbs;
/*
 * In memory representation of a key-value store
 */
public class KeyValueStore {

	private HashMap<String, String> data = new HashMap<String, String>();
	
	/*
	 * Apply the specified operation to the provided key
	 * The value is only needed for PUT operation
	 */
	public KeyValueProtocolResponse apply(KeyValueProtocolVerbs operation, String key, String value) {
		KeyValueProtocolResponse response = new KeyValueProtocolResponse();
		response.setSucceeded(false);
		
		// need operation and key
		if(operation == null || key == null ) {
			response.setValue("Invalid Request");
			return response;
		}
			
		switch(operation) {
		case GET :
			// if there is data for the key return it
			// else return error message
			if (data.containsKey(key)) {
				response.setSucceeded(true);
				response.setValue(data.get(key));
			} else {
				response.setValue("No matching key found");
			}
			break;
		case DELETE:
			// remove the key if it is present
			// else return error message
			if (data.containsKey(key)) {
				data.remove(key);
				response.setSucceeded(true);
				response.setValue(null);
			} else {
				response.setValue("No matching key found");
			};
			break;
		case PUT:
			// set the value for the provided key
			data.put(key, value);
			response.setSucceeded(true);
			response.setValue(null);
			break;
		default:
			// if none of the operations match, return invalid request
			response.setValue("Invalid request");
			break;
		}
		return response;
	}

}
