import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

/*
 * @author Swati Garg
 * This class runs the various max flow computing algorithms
 */
public class tcss543 {
	final static String tempFilename = "temp.txt";
	final static int numRun = 3;
	
	public static void main(String args[]) throws IOException{
		String filenameInput = tempFilename;
		if (args.length == 0) {
			// the input is from the console <
			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			BufferedWriter out = new BufferedWriter(new FileWriter(tempFilename));
			while(in.ready()) {
				inputLine = in.readLine();
				out.write(inputLine);
				out.newLine();
			}
			out.close();
		} else {
			// input is in the file
			filenameInput = args[0];
		}
		
		
		// runs the FordFulkerson algorithm using the Adacency List
				runAlgo(filenameInput, 
						(String filename) -> { FordFulkersonAlt ff = new FordFulkersonAlt(); 
												try {
													return ff.run(filename);
												} catch (Exception e) {
													return -1;
												}}, 
						"Ford Fulkerson AdjacencyList");
				
				
		// runs the fordfulkerson algorithm using the Adacency Matrix
		
		runAlgo(filenameInput, 
				(String filename) -> { FordFulkerson ff = new FordFulkerson(); 
										try {
											return ff.run(filename);
										} catch (Exception e) {
											return -1;
										}}, 
				"Ford Fulkerson AdjacencyMatrix");
		
	}
	
	private static int runAlgo(String filename, Function<String, Integer> fn, String AlgoName) throws IOException {
		long sumTimeDiff = 0;
		int maxflow = 0;
		double timeAverageCurrRun = 0.0; // stores the average run time in milliseconds

		// dummy run 
		maxflow = fn.apply(filename);
		
		for (int i = 0 ; i < numRun; i++) {
			long startTime = System.nanoTime();
			fn.apply(filename);
			long endTime = System.nanoTime();		
			sumTimeDiff += endTime - startTime;
			//System.out.println("Time taken by " + AlgoName + " algorithm in nano second = " + (endTime - startTime));
		}
		
		timeAverageCurrRun = sumTimeDiff/(1000000.0 * numRun);
		System.out.println("AverageTime taken by" + AlgoName + "algorithm in millisec = " + timeAverageCurrRun);
		System.out.println("Max flow by " + AlgoName + " is " + maxflow);
		
		return maxflow;
	}
}
