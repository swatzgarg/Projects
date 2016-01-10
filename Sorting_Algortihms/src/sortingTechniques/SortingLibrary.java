package sortingTechniques;

/*
 * Library that implements various sorting algorithms
 */
public class SortingLibrary {
	
	// implementation for insert sort technique
	public static void insertSort(int[] input) {
		int sizeofInput = input.length;
	    for(int index = 1; index < sizeofInput; index++) {
	    	int secondIndex = index;
	        while(secondIndex > 0 && input[secondIndex - 1] > input[secondIndex]) {
	        	swap(input, secondIndex-1, secondIndex);
	            secondIndex--;
	        }
	    }
	}
	
	// implementation for selection sort technique
	public static void selectionSort(int[] input) {
		int firstIndex = 0;
		int minIndex = firstIndex;
		for(firstIndex = 0; firstIndex < input.length -1; firstIndex++) {
			for(int secIndex = firstIndex + 1; secIndex < input.length; secIndex++) {
				if (input[minIndex] > input[secIndex] ){				
					minIndex = secIndex;
				}
			}
			swap(input, firstIndex, minIndex);
		}		
	}
	
	// implementation for the bubble sort
	public static void bubbleSort(int[] input) {
		for(int i = input.length - 1 ; i > 0 ; i-- ) {
			for (int j = 0; j < i; j++) {
				if(input[j] > input[j+1]) {
					swap(input, j, j+1);
				}
			}
		}
	}
	
	// implementation for the quick sort
	public static void quickSort(int[] input) {
		quickSortHelper(input, 0, input.length - 1);
	}
	
	// helper function to run quick sort. 
	// The helper function is written to keep the signature of each sorting algorithm the same
	private static void quickSortHelper(int[] input, int startindex, int endindex){
		if (endindex > startindex) {
			int pivot = partition(input, startindex, endindex); // partition the array
			quickSortHelper(input, startindex, pivot - 1); // call sort of the first part
			quickSortHelper(input, pivot + 1, endindex); // call sort on the second part
		}
	}
	
	// Helper function to partition the array around a pivot for quick sort
	private static int partition(int[] input, int startindex, int endindex) {
		int pivot = input[endindex];
		int swapPos = startindex;
		for(int i = startindex; i < endindex; i++) {
			if (input[i] < pivot) {
				swap(input, i , swapPos);
				swapPos++;
			}
		}
		swap(input, swapPos, endindex);
		return swapPos;
	}
	
	// implementation of merge sort
	public static void mergeSort(int[] input) {
		int[] temp = new int[input.length];
		mergeSortHelper(input, 0, input.length -1 , temp);
	}
	
	// helper function to run merge sort. 
	// The helper function is written to keep the signature of each sorting algorithm the same
	private static void mergeSortHelper(int[] input, int startindex, int endindex,  int[] temp) {
		if (endindex > startindex) {
			int mid = (endindex + startindex)/2; // compute the mid
			mergeSortHelper(input, startindex, mid, temp); // recursively sort the first half
			mergeSortHelper(input, mid+1, endindex, temp); // recursively sort the second half
			
			// merge the two sorted halves using the temp array
			int i = startindex;
			int j = mid + 1;
			int k = 0;
			while(i <= mid && j <= endindex) {
				if (input[i] <= input[j]) {
					temp[k] = input[i];
					i++;
				} else {
					temp[k] = input[j];
					j++;
				}
				k++;
			}
			while(i <= mid) {
				temp[k] = input[i];
				i++;
				k++;
			}
			while(j <= endindex) {
				temp[k] = input[j];
				j++;
				k++;
			}
			
			// copy temp over to input at right spot
			for (int l = 0; l < k; l++) {
				input[startindex+l] = temp[l];
			}
		}
	}
	
	// helper function to swap two values in an array
	private static void swap(int[] input, int firstIndex, int secondIndex) {
		int temp = input[firstIndex];
		input[firstIndex] = input[secondIndex];
		input[secondIndex] = temp;
	}
}

