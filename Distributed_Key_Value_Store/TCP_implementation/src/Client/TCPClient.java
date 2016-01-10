package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * This class implements a simple TCP Client for key value store
 */
public class TCPClient {
	private int port; // the port of the server 
	private int connectionTimeout; // connection timeout in milliseconds
	private int readTimeout; // read timeout in milliseconds
	private InetAddress serverAddress; // address of the server
	
	public TCPClient(int port, int connectionTimeout, int readTimeout, InetAddress ipAddress) {
		super();
		this.port = port;
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
		this.serverAddress = ipAddress;
	}

	/*
	 * Send the json provided to the server and returns the response of the server
	 * The connect and read calls have a timeout
	 * throws SocketTimeoutException when the timeout is reached and server hasn't responded back
	 */
	public String sendData(String json) throws UnknownHostException, IOException, SocketTimeoutException {
		// create a socket
		Socket clientSocket = new Socket();
		
		// create a remote Address 
		InetSocketAddress addressRemote;
		addressRemote = new InetSocketAddress(serverAddress, port);
		
		// connect to the remote server 
		clientSocket.connect(addressRemote, connectionTimeout);
		// set read timeout
		clientSocket.setSoTimeout(readTimeout);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		// write the json to the server
		outToServer.writeBytes(json);
		
		// get the output
		String output = inFromServer.readLine();
		
		// close socket
		clientSocket.close(); 
		
		// return output
		return output;
	}
}
