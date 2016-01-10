package sortingTechniques;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Sort {
	// the size of the data used in the sorting experiment
	final static int[] datasizes = new int[] { 10, 1000, 10000, 50000, 100000, 200000};
	
	// main class which runs the sorting experiment and prints out the results
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<RunInfo> randomRunData = runRandom();
		System.out.println("Random Data");
		printData(randomRunData);

		ArrayList<RunInfo> gaussianRunData = runGaussian();
		System.out.println("Gaussian Data");
		printData(gaussianRunData);
		
		ArrayList<RunInfo> reviewLengthRunData = runReviewLength();
		System.out.println("Review length Data");
		printData(reviewLengthRunData);
		
		ArrayList<RunInfo> numPostWeiboData = runNumPostWeibo();
		System.out.println("Number of Post on Weibo Data");
		printData(numPostWeiboData);
		
		ArrayList<RunInfo> randomRunSortednessData = runSortednessRandom();
		System.out.println("Random Data with varying sortedness");
		printData(randomRunSortednessData);

		ArrayList<RunInfo> gaussianRunSortednessData = runSortednessGaussian();
		System.out.println("Gaussian Data with varying sortedness");
		printData(gaussianRunSortednessData);
		
		ArrayList<RunInfo> reviewLengthRunSortednessData = runSortednessReviewLength();
		System.out.println("Review length Data with varying sortedness");
		printData(reviewLengthRunSortednessData);
		
		ArrayList<RunInfo> numPostWeiboSortednessData = runSortednessNumPostWeibo();
		System.out.println("Number of Post on Weibo Data with varying sortedness");
		printData(numPostWeiboSortednessData);
	}

	// Helper function to get the 'number of posts per user on weibo' data of length 50000,
	// and run all the sorting algorithm on that data.
	// The algorithms are run on same data with various sortedness
	private static ArrayList<RunInfo> runSortednessNumPostWeibo() throws FileNotFoundException {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		int[] input = DataGenerator.numPostWeiboArray(50000);
		
		ArrayList<int[]> data = DataGenerator.multiSortednessData(input);
		
		for (int i = 0; i < data.size(); i++) {
			double sortedness = DataGenerator.computeSortnessMeasure(data.get(i));
			sortAll(outputData, data.get(i), sortedness);
		}
		return outputData;
	}

	// Helper function to get the 'length of reviews in amazon fine food reviews' data of length 50000,
	// and run all the sorting algorithm on that data.
	// The algorithms are run on same data with various sortedness
	private static ArrayList<RunInfo> runSortednessReviewLength() throws FileNotFoundException {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		int[] input = DataGenerator.reviewLengthArray(50000);
		
		ArrayList<int[]> data = DataGenerator.multiSortednessData(input);
		
		for (int i = 0; i < data.size(); i++) {
			double sortedness = DataGenerator.computeSortnessMeasure(data.get(i));
			sortAll(outputData, data.get(i), sortedness);
		}
		return outputData;
	}

	// Helper function to get the gaussian data of length 50000,
	// and run all the sorting algorithm on that data.
	// The algorithms are run on same data with various sortedness
	private static ArrayList<RunInfo> runSortednessGaussian() {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		int[] input = DataGenerator.gaussianArray(50000);
		
		ArrayList<int[]> data = DataGenerator.multiSortednessData(input);
		
		for (int i = 0; i < data.size(); i++) {
			double sortedness = DataGenerator.computeSortnessMeasure(data.get(i));
			sortAll(outputData, data.get(i), sortedness);
		}
		return outputData;
	}
	
	// Helper function to get the random data of length 50000,
	// and run all the sorting algorithm on that data.
	// The algorithms are run on same data with various sortedness
	private static ArrayList<RunInfo> runSortednessRandom() {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		int[] input = DataGenerator.randomArray(50000);
		
		ArrayList<int[]> data = DataGenerator.multiSortednessData(input);
		
		for (int i = 0; i < data.size(); i++) {
			double sortedness = DataGenerator.computeSortnessMeasure(data.get(i));
			sortAll(outputData, data.get(i), sortedness);
		}
		return outputData;
	}

	// Helper function to get the 'length of reviews in amazon fine food reviews' data and run sorting algorithms on it.
	// The algorithms are run on all the data sizes
	private static ArrayList<RunInfo> runReviewLength() throws FileNotFoundException {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		
		for (int iSize = 0 ; iSize < datasizes.length ; iSize++) {
			int[] input;
			double sortedness = 0;
			
			input = DataGenerator.reviewLengthArray(datasizes[iSize]);
			sortedness = DataGenerator.computeSortnessMeasure(input);
				
			sortAll(outputData, input, sortedness);
		}
		return outputData;
	}

	// Helper function to get the 'number of posts per user on weibo'  data and run sorting algorithms on it.
	// The algorithms are run on all the data sizes
	private static ArrayList<RunInfo> runNumPostWeibo() throws FileNotFoundException {
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		
		for (int iSize = 0 ; iSize < datasizes.length ; iSize++) {
			int[] input;
			double sortedness = 0;
			
			input = DataGenerator.numPostWeiboArray(datasizes[iSize]);
			sortedness = DataGenerator.computeSortnessMeasure(input);
				
			sortAll(outputData, input, sortedness);
		}
		return outputData;
	}

	// Helper function to get the random data and run sorting algorithms on it.
	// we try to get the array which is reasonably unsorted.
	// The algorithms are run on all the data sizes
	private static ArrayList<RunInfo> runRandom() {
		double highsortedness = 0.05;
		double lowsortedness = -0.05;
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		
		for (int iSize = 0 ; iSize < datasizes.length ; iSize++) {
			int[] input;
			double sortedness = 0;
			int count = 0;
			
			do {
				input = DataGenerator.randomArray(datasizes[iSize]);
				sortedness = DataGenerator.computeSortnessMeasure(input);
				count ++;
			} while (sortedness <= highsortedness && sortedness >= lowsortedness && count < 10);
				
			sortAll(outputData, input, sortedness);
		}
		return outputData;
	}

	// Helper function to get the gaussian data and run sorting algorithms on it.
	// we try to get the  array which is reasonably unsorted.
	// The algorithms are run on all the data sizes
	private static ArrayList<RunInfo> runGaussian() {
		double highsortedness = 0.4;
		double lowsortedness = -0.4;
		ArrayList<RunInfo> outputData = new ArrayList<RunInfo>();
		
		for (int iSize = 0 ; iSize < datasizes.length ; iSize++) {
			int[] input;
			double sortedness = 0;
			int count = 0;
			
			do {
				input = DataGenerator.gaussianArray(datasizes[iSize]);
				sortedness = DataGenerator.computeSortnessMeasure(input);
				count ++;
			} while (sortedness <= highsortedness && sortedness >= lowsortedness && count < 10);
				
			sortAll(outputData, input, sortedness);
		}
		return outputData;
	}
	
	// Helper function to call RunInfo.runSort  on the given input array for all the sorting algorithms.
	private static void sortAll(ArrayList<RunInfo> outputData, int[] input, double sortedness) {
		outputData.add(RunInfo.runSort(input, inputData -> { SortingLibrary.bubbleSort(inputData);}, sortedness));
		outputData.add(RunInfo.runSort(input, inputData -> { SortingLibrary.insertSort(inputData);}, sortedness));
		outputData.add(RunInfo.runSort(input, inputData -> { SortingLibrary.mergeSort(inputData);}, sortedness));
		outputData.add(RunInfo.runSort(input, inputData -> { SortingLibrary.selectionSort(inputData);}, sortedness));		
		outputData.add(RunInfo.runSort(input, inputData -> { SortingLibrary.quickSort(inputData);}, sortedness));
	}

	// Helper function to print out the performance data for each experiment.
	private static void printData(ArrayList<RunInfo> data) {
		System.out.println("Data Size, Sortedness, BubbleSort, Insert Sort, Merge Sort, Selection Sort, QuickSort"); 
		
		// time
		for (int i = 0 ; i < data.size(); i = i + 5) {
			RunInfo runinfoBS = data.get(i);
			RunInfo runinfoIS = data.get(i+1);
			RunInfo runinfoMS = data.get(i+2);
			RunInfo runinfoSS = data.get(i+3);
			RunInfo runinfoQS = data.get(i+4);
			System.out.printf("%d, %.5f, %.2f, %.2f, %.2f, %.2f, %.2f %n", 
					runinfoBS.getDataSize(), 
					runinfoBS.getSortedness(), 
					runinfoBS.getTime(),
					runinfoIS.getTime(),
					runinfoMS.getTime(),
					runinfoSS.getTime(),
					runinfoQS.getTime()
					);
		}
		
		// memory
		for (int i = 0 ; i < data.size(); i = i + 5) {
			RunInfo runinfoBS = data.get(i);
			RunInfo runinfoIS = data.get(i+1);
			RunInfo runinfoMS = data.get(i+2);
			RunInfo runinfoSS = data.get(i+3);
			RunInfo runinfoQS = data.get(i+4);
			System.out.printf("%d, %.5f, %.2f, %.2f, %.2f, %.2f, %.2f %n", 
					runinfoBS.getDataSize(), 
					runinfoBS.getSortedness(), 
					runinfoBS.getMemory(),
					runinfoIS.getMemory(),
					runinfoMS.getMemory(),
					runinfoSS.getMemory(),
					runinfoQS.getMemory()
					);
		}
	}
}
