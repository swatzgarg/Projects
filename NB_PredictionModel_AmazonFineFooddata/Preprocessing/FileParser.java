package dataSanitizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {

protected static boolean parse(String filenameInput, String filenameResultsPy, String filenameDataPy, String filenameOutputR, String filenameOutputFull) throws IOException 
{
	Scanner scanner = new Scanner(new File(filenameInput));
	// patterns to get data from the inputed data file
	Pattern helpfulnessPattern = Pattern.compile("review/helpfulness\\s*:\\s*([0-9]*)/([0-9]*)");
	Pattern scorePattern = Pattern.compile("review/score\\s*:\\s*([0-9]*\\.*[0-9]*)");
	Pattern textPattern = Pattern.compile("review/text\\s*:\\s*(.*)");
	
	// writers to write to various output files
	BufferedWriter writerDataPy = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameDataPy)));
	BufferedWriter writerResultsPy = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameResultsPy)));
	BufferedWriter writerDataR = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameOutputR)));
	BufferedWriter writerDataFull = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameOutputFull)));
	
	while(scanner.hasNextLine()) {
		// read a entry for a single review.
		scanner.nextLine();
		scanner.nextLine();
		scanner.nextLine();
		String line4 = scanner.nextLine();
		String line5 = scanner.nextLine();
		scanner.nextLine();
		scanner.nextLine();
		String line8 = scanner.nextLine();
		if (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(!line.isEmpty()){
				break;
			}
		}
		
		// match the patterns
		Matcher helpfulnessMatcher = helpfulnessPattern.matcher(line4);
		Matcher scoreMatcher = scorePattern.matcher(line5);
		Matcher textMatcher = textPattern.matcher(line8);
		
		// if all the pattern matches, then write the values.
		if(helpfulnessMatcher.matches() && scoreMatcher.matches() && textMatcher.matches()){
			double totalReviews = Double.parseDouble(helpfulnessMatcher.group(2));
			int helpfulReviews = Integer.parseInt(helpfulnessMatcher.group(1));
			double score = Double.parseDouble(scoreMatcher.group(1));
			int textLength = textMatcher.group(1).length();
			if (helpfulReviews > totalReviews)
				continue;
			
			if (totalReviews >= 6) {
				// for niave bayes only consider inputs with atleast 6 review ratings.
				double helpfulness = helpfulReviews/totalReviews;
				String HelpfulnessRange = Integer.toString((int)(helpfulness*10));
				
				// create files for python
				writerResultsPy.write(HelpfulnessRange);
				writerResultsPy.newLine();
				writerDataPy.write(Double.toString(score));
				writerDataPy.write("\t");
				writerDataPy.write(Integer.toString(textLength));
				writerDataPy.newLine();
				
				// create files for hadoop and R
				writerDataR.write(Integer.toString(helpfulReviews));
				writerDataR.write("\t");
				writerDataR.write(Double.toString(totalReviews));
				writerDataR.write("\t");
				writerDataR.write(Double.toString(score));
				writerDataR.write("\t");
				writerDataR.write(Integer.toString(textLength));
				writerDataR.newLine();
			}
			if (totalReviews > 0) {
				// complete data
				writerDataFull.write(Integer.toString(helpfulReviews));
				writerDataFull.write("\t");
				writerDataFull.write(Double.toString(totalReviews));
				writerDataFull.write("\t");
				writerDataFull.write(Double.toString(score));
				writerDataFull.write("\t");
				writerDataFull.write(Integer.toString(textLength));
				writerDataFull.newLine();	
			}
		}
	}
	
	// close the writers
	writerDataPy.close();
	writerResultsPy.close();
	writerDataFull.close();
	writerDataR.close();
	// close the scanner
	scanner.close();
	return true;
}
}
