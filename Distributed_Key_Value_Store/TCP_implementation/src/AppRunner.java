import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;

import Client.TCPClient;
import Server.TCPServer;
import model.KeyValueProtocolRequest;
import model.KeyValueProtocolVerbs;


public class AppRunner {
	private final static int connectionTimeout = 500; // connection timeout in millisecond
	private final static int readTimeout = 500; // read timeout in millisecond

	/*
	 * Main function which checks the command lien params and start the appropriate server/client
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Need atleast one parameter");
			return;
		}
		
		if (args[0].equalsIgnoreCase("client")) {
			// start client
			if (args.length < 4) {
				System.out.println("Need hostname/ipAddress, port number and log file name");
				return;
			}

			// get the ipaddress/hostname of the server
			InetAddress hostAddress;
			try {
				hostAddress = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e) {
				System.out.println("Invalid hostname or ip address");
				return;
			}
			// get the server port
			int port = Integer.parseInt(args[2]);
			// get the log file name to log the errors
			String logFilename = args[3];
			
			// run the client console UI
			RunTCPClientPrefill(port, connectionTimeout, readTimeout, hostAddress, logFilename);
		}
		
		if (args[0].equalsIgnoreCase("server")) {
			// start server
			if (args.length < 3) {
				System.out.println("Need port number and log file name");
				return;
			}
			
			// port to start server at
			int port = Integer.parseInt(args[1]);
			// filename to log the server request/response
			String logFilename = args[2];
			
			// run the serverRunTCPClientPrefill
			RunTCPServer(port, logFilename);
		}
	}

	
	/*
	 * Prefill for the client. This fill up the demo data in the server
	 */
	private static void RunTCPClientPrefill(int port, int connectionTimeout, int readTimeout, InetAddress serverAddress, String logFilename) {
		// create the TCP Client 
		TCPClient tcpClient = new TCPClient(port, connectionTimeout, readTimeout, serverAddress);
		//create the log file writer and the input streams
		try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(new File(logFilename)));){
			// create the request object
			KeyValueProtocolRequest keyValueInput = new KeyValueProtocolRequest();
			
			// PUTs
			keyValueInput.setOperation(KeyValueProtocolVerbs.PUT);
			keyValueInput.setKey("Key1");
			keyValueInput.setValue("Value1");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.PUT);
			keyValueInput.setKey("Key2");
			keyValueInput.setValue("Value2");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.PUT);
			keyValueInput.setKey("Key3");
			keyValueInput.setValue("Value3");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.PUT);
			keyValueInput.setKey("Key4");
			keyValueInput.setValue("Value4");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.PUT);
			keyValueInput.setKey("Key5");
			keyValueInput.setValue("Value5");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			
			//GETs
			keyValueInput.setOperation(KeyValueProtocolVerbs.GET);
			keyValueInput.setKey("Key1");
			keyValueInput.setValue(null);
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.GET);
			keyValueInput.setKey("Key2");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.GET);
			keyValueInput.setKey("Key3");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.GET);
			keyValueInput.setKey("Key4");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.GET);
			keyValueInput.setKey("Key5");
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);

			//DELETEs
			keyValueInput.setOperation(KeyValueProtocolVerbs.DELETE);
			keyValueInput.setKey("Key1");
			keyValueInput.setValue(null);
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);
			keyValueInput.setOperation(KeyValueProtocolVerbs.DELETE);
			keyValueInput.setKey("Key4");
			keyValueInput.setValue(null);
			// make the call to server
			callClientForPrefill(tcpClient, logWriter, keyValueInput);

			
			// run the client console UI
			RunTCPClientUI(port, connectionTimeout, readTimeout, serverAddress, logFilename);		
		} 
		catch(IOException e){
			// can't read or write to log file
			// close the client
		}
	}

	private static void callClientForPrefill(TCPClient tcpClient, BufferedWriter logWriter, KeyValueProtocolRequest keyValueInput)
			throws JsonProcessingException, IOException {
		String json = keyValueInput.serialize();
		try {
			String output = tcpClient.sendData(json);
			if (output == null) {
				// log the error
				LogData(logWriter, "No reponse from server");
			}
		}
		catch (SocketTimeoutException e) {
			// log the timeout error
			System.out.println("Request timed out");
			LogData(logWriter, "Request timed out");
		}
		catch(IOException e) {
			// log errors
			System.out.println("Can't read from server");
			LogData(logWriter, "Can't read from server");
		}
	}
	
	/*
	 * Starts and run the TCP server
	 */
	private static void RunTCPServer(int port, String logFilename) {
		TCPServer server = new TCPServer();;
		try {
			server.startServer(port, logFilename);
		} catch (IOException e) {
			try {
				server.close();
			} catch (Exception e1) {
			}
		}
	}

	/*
	 * Run the client console UI 
	 */
	private static void RunTCPClientUI(int port, int connectionTimeout, int readTimeout, InetAddress serverAddress, String logFilename) {
		// create the TCP Client 
		TCPClient tcpClient = new TCPClient(port, connectionTimeout, readTimeout, serverAddress);
		
		//create the log file writer and the input streams
		try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(new File(logFilename)));
				BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));){
			// run the console UI till user types EXIT
			while(true) {
				// create the request object
				KeyValueProtocolRequest keyValueInput = new KeyValueProtocolRequest();
				
				// get the operation to perform
				System.out.print("Operation : ");
				String operation = inFromUser.readLine();
				
				// check if user wants to exit
				if(operation.equalsIgnoreCase("EXIT"))
					break;
				
				// check if the operation is a permitted operation
				for (KeyValueProtocolVerbs validVerb : KeyValueProtocolVerbs.values()) {
					if (validVerb.name().equalsIgnoreCase(operation)) {
						keyValueInput.setOperation(validVerb);
					}
				}
				if (keyValueInput.getOperation() != null) {
					// get parameters for the operation
					// get Key
					System.out.print("Key : ");
					keyValueInput.setKey(inFromUser.readLine());
					// get value if it is PUT operation
					if (keyValueInput.getOperation().equals(KeyValueProtocolVerbs.PUT)) {
						System.out.print("Value : ");
						keyValueInput.setValue(inFromUser.readLine());
					}
					
					// make the call to server
					String json = keyValueInput.serialize();
					try {
						String output = tcpClient.sendData(json);
						
						// output results if any
						System.out.println("Output : " + output);
						if (output == null) {
							// log the error
							LogData(logWriter, "No reponse from server");
						}
					}
					catch (SocketTimeoutException e) {
						// log the timeout error
						System.out.println("Request timed out");
						LogData(logWriter, "Request timed out");
					}
					catch(IOException e) {
						// log errors
						System.out.println("Can't read from server");
						LogData(logWriter, "Can't read from server");
					}
				} else {
					// invalid operation
					System.out.println("Invalid Operation. Valid Operations are GET, PUT, DELETE and EXIT");
				}
			}
		}
		catch(IOException e){
			// can't read or write to log file
			// close the client
		}
	}
	
	/*
	 * function to log the client errors into a log file
	 */
	private static void LogData(BufferedWriter logWriter, String log) throws IOException{
		StringBuilder logLine = new StringBuilder();
		// get the current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS");
		logLine.append(dateFormat.format(new Date()));
		logLine.append(" : " + log);
		logLine.append(System.lineSeparator());
		logWriter.write(logLine.toString());
		logWriter.flush();
	}
}
