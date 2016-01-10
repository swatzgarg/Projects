package dataSanitizer;

import java.io.IOException;

public class FilePreprocessor {

	public static void main(String[] args) {
		if (args.length != 5) {
			System.err.println("Please provide datafileName and 4 output file names");
			return;
		}
			
		try {
			FileParser.parse(args[0], args[1], args[2], args[3], args[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
