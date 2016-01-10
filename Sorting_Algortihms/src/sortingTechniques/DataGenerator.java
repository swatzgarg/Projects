package sortingTechniques;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/*
 * This class generated the data used for sorting
 * The size of data to be generated is provided.
 */
public class DataGenerator {
	
	// random array generation uniformly distributed over all integers.
	public static int[] randomArray(int size){
		Random array = new Random();
		int[] input = new int[size];
	    for (int i = 0; i < input.length; i++) {
	      input[i] = array.nextInt()/2;     
	    }
		return input;
	}

	// random array generation using Gaussian distribution
	public static int[] gaussianArray(int size){
		Random array = new Random();
		int[] input = new int[size];	
		    for (int i = 0; i < input.length; i++) {
		      input[i] = (int) array.nextGaussian();
		    }
		    return input;
	}

	// read the data from the file 
	// the file contains the number of posts per user on Weibo.com
	public static int[] numPostWeiboArray(int size) throws FileNotFoundException{
        return readData(size, "numberOfPostsWeibo.txt");
	}
	
	// read the data from the file
	// the file contains the length of the reviews in amazon fine food reviews
	public static int[] reviewLengthArray(int size) throws FileNotFoundException{
		return readData(size, "reviewhelpfulness.txt");
	}

	// helper function to read data from a file into an array
	private static int[] readData(int size, String filename) throws FileNotFoundException {
		int[] input = new int[size];	
		Scanner scanner = new Scanner(new File(filename));
         
        int i = 0;
        while (scanner.hasNext() && i < size)
        {
        	input[i] = Integer.parseInt(scanner.next());
        	i++;
        }
        scanner.close();
	    return input;
	}
	
	// compute the spearman coefficient of the provided array
	public static double computeSortnessMeasure(int[] input) {
		// array to sort the array and store the indices of each data point in the original unsorted array
		long[][] sorted = new long[input.length][2]; 
		for (int i = 0 ; i < input.length; i++) {
			// initialize the array
			sorted[i][0] = input[i];
			sorted[i][1] = i;
		}
		
		// sort the array on the first value in long[]
		Arrays.sort(sorted, new Comparator<long[]>() {
			@Override
			public int compare(long[] entry1, long[] entry2) {
				return (int) (entry1[0] - entry2[0]);
			}
		});
		
		// compute spearman coefficient
		double sum = 0.0f;
		double len = input.length;  // this is done to force double computations instead of int.
		double dem = len * len - 1;  // this is done to prevent overflows
		for (int i = 0 ; i <  input.length; i++) {
			sum = sum + (sorted[i][1] - i) * (sorted[i][1] - i);
		}
		return 1 - (6 * sum)/(len * dem);
	}

	// function to change the sortedness of the array, 
	// in order to study the effect of sortedness on performance of the sorting algorithm 
	public static ArrayList<int[]> multiSortednessData(int[] base) {
		ArrayList<int[]> output = new ArrayList<int[]>();
		int[] array100 = Arrays.copyOf(base, base.length);
		int[] array75 = Arrays.copyOf(base, base.length);
		int[] array50 = Arrays.copyOf(base, base.length);
		int[] array25 = Arrays.copyOf(base, base.length);
		
		Arrays.sort(array25, 0, base.length/4);
		Arrays.sort(array75, 0, (base.length * 3)/4);
		Arrays.sort(array50, 0, base.length/2);
		Arrays.sort(array100, 0, base.length);
		output.add(base);
		output.add(array25);
		output.add(array50);
		output.add(array75);
		output.add(array100);
		
		return output;
	}
}
