package model;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Model class for the response from the Key Value store for the request from the client
 */
public class KeyValueProtocolResponse {
	private boolean succeeded;  // did the operation succeed
	private String value;		// the response value. This is error string when the operation succeeds. Else it is output of the operation

	public boolean isSucceeded() {
		return succeeded;
	}
	
	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	// Serialize class into json for transfer over network
	public String serialize() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	// deserialize the json into this class.
	public static KeyValueProtocolResponse deserialize(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException {
		JsonFactory jf = new JsonFactory();
		jf.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
		ObjectMapper mapper = new ObjectMapper(jf);
		return mapper.readValue(inputStream, KeyValueProtocolResponse.class);
	}
}
