package Server;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import model.KeyValueProtocolRequest;
import model.KeyValueProtocolResponse;
import model.KeyValueProtocolVerbs;
import Database.KeyValueStore;

/*
 * This class implements a simple TCP Server for key value store
 */
public class TCPServer {

	private BufferedWriter logWriter; 	// to write the logs
	KeyValueStore keyValueStore;		// the actual key value store to perform the operations on
	
	// Method to close the log writer.
	public void close() throws IOException {
		logWriter.close();
	}
	
	/*
	 * Starts the server at given port and using the provided log file
	 * The server runs forever unless killed by using Ctrl+C
	 */
	public void startServer(int portNumber, String logFilename) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(portNumber); 
		logWriter = new BufferedWriter(new FileWriter(new File(logFilename)));
		keyValueStore = new KeyValueStore();
		while(true) { 
			try (Socket clientSocket = serverSocket.accept();
			DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream()); ) {
				// parse the request
				KeyValueProtocolRequest request;
				try {
					request = KeyValueProtocolRequest.deserialize(clientSocket.getInputStream());
				}
				catch(JsonParseException | JsonMappingException e) {
					// got invalid request
					// log it and continue
					LogError(clientSocket);
					continue;
				}
				// run the operation and get the response
				KeyValueProtocolResponse response = keyValueStore.apply(request.getOperation(), request.getKey(), request.getValue());
				
				// serialize the response as json and send it back to the client.
				String output = response.serialize();
				outData.writeBytes(output);		
				
				// log the data
				LogData(clientSocket, request, response);
			}
		}
	}
	
	/*
	 * Log the data when the request is in valid syntax
	 */
	private void LogData(Socket clientSocket, KeyValueProtocolRequest input, KeyValueProtocolResponse output) throws IOException {
		StringBuilder logLine = new StringBuilder();
		// get the current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS");
		logLine.append(dateFormat.format(new Date()));
		logLine.append(": Received from <" + clientSocket.getRemoteSocketAddress() + ">:<" + clientSocket.getPort() + ">");
		logLine.append("a query for " + input.getOperation().name());
		logLine.append(" with key " + input.getKey());
		if (input.getOperation().equals(KeyValueProtocolVerbs.PUT)) 
			logLine.append(" with value " + input.getValue());
		if (output.isSucceeded())
			logLine.append(". The output returned is " + output.getValue());
		else
			logLine.append(". The error returned is " + output.getValue());
		logLine.append(System.lineSeparator());
		logWriter.write(logLine.toString());
		logWriter.flush();
	}
	
	/*
	 * Log an error message. Used when the syntax of the request is invalid.
	 */
	private void LogError(Socket clientSocket) throws IOException {
		StringBuilder logLine = new StringBuilder();
		// get the current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS");
		logLine.append(dateFormat.format(new Date()));
		logLine.append(": Recieved malformed request of length ");
		logLine.append(clientSocket.getInputStream().available() + 
				" from <" + clientSocket.getRemoteSocketAddress() + ">:<" + clientSocket.getPort() + ">");
		logLine.append(System.lineSeparator());
		logWriter.write(logLine.toString());
		logWriter.flush();
	}
}

