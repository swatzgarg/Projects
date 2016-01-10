package model;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Model class for the request used to operate on Key Value store from the client
 */
public class KeyValueProtocolRequest {

	private KeyValueProtocolVerbs operation;
	private String key;
	private String value;
	
	public KeyValueProtocolVerbs getOperation() {
		return operation;
	}
	
	public void setOperation(KeyValueProtocolVerbs operation) {
		this.operation = operation;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
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
	public static KeyValueProtocolRequest deserialize(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException {
		JsonFactory jf = new JsonFactory();
		jf.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
		ObjectMapper mapper = new ObjectMapper(jf);
		return mapper.readValue(inputStream, KeyValueProtocolRequest.class);
	}
}
