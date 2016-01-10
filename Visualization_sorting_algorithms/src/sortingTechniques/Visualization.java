package sortingTechniques;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
/*
 * Class to draw visualization for various sorting methods.
 */
public class Visualization extends JPanel {	
	private static final long serialVersionUID = 1L;
	
	//Arrays for the input
	int [] inputRandom;
	int [] inputPartiallySorted;

	// current data set working on
	private int[] input; 

	//Array to store the color object for visualization
	Color[] colorList;	
	
	// index for depicting the swapped entries.
	private int first_index = -1;
	private int second_index = -1;
	
	// merge sort draw parameters
	private boolean isMergeSort = false;
	private int k = -1;
	private int[] temp;
	
	// default delay between each draw
	private int delay = 200;
	
	// name of the actual algorithm being visualized
	private String sortingAlgo;
	
	private final int dataSize = 50;
	private final int colorDataSize = 100;
	
	private final static int widthFrame = 1020;
	private final static int heightFrame = 700;
	
	// drawing coordinates for sorting visuals
	private final static int baselineText = 80;
	private final static int xText = 350;
	private final static int xArray = 10;
	private final static int baselineArray = 600;
	private final static int baselineTemp = 300;
	
	// id of the slide to render.
	// 0 means render visualization.
	int slideID = -1;
	
	//Creating the color objects for visualization of the data set
	private void initializeColor(){
		colorList = new Color[colorDataSize];
		for(int i = 0; i < colorDataSize; i++){
			if (i <= 20){
				colorList[i] = new Color(186, 85, 211);
			}
			if(i > 20 && i <= 40){
				colorList[i] = new Color(221, 160, 221);
			}
			if(i > 40 && i <= 60){
				colorList[i] = new Color(147, 112, 219);
			}
			if(i > 60 && i <= 80){
				colorList[i] = new Color(148, 0, 211);
			}
			if(i > 80){
				colorList[i] = new Color(139, 0, 139);
			}
		}
	}

	/* Constructor
	* creates the input data sets
	* 1: Random data set
	* 2: Partly sorted data set
	* Initializes the color object
	* Create the temporary arrays used for sorting.
	*/
	Visualization() {
		initializeColor();
		
		// generate random input between 0 and 100
        Random rand = new Random();
		inputRandom = new int[dataSize];
	    for (int i = 0; i < inputRandom.length; i++) {
	      inputRandom[i] = rand.nextInt()/2;
	      inputRandom[i] = inputRandom[i] > 0 ? inputRandom[i] % 100 : (-inputRandom[i]) % 100;
	    }
	    
	    // generate partially sorted input
		Random randPartiallySorted = new Random();
		inputPartiallySorted = new int[dataSize];	
	    for (int i = 0; i < inputPartiallySorted.length; i++) {
	    	inputPartiallySorted[i] = randPartiallySorted.nextInt() % 50 + 50;
	    }
	    // lets pre-sort some portions of the array
	    int start = 0;
	    int end = start + dataSize/4;
	    while (end < inputPartiallySorted.length) {
	    	Arrays.sort(inputPartiallySorted, start , end);
	    	start = start + dataSize/4;
		    end = start + dataSize/8;
	    }
		    
		input = new int[dataSize];
		temp = new int[dataSize];
    }
	
	public static void main(String[] args) throws InterruptedException {		
		JFrame v = new JFrame();
		v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		v.setVisible(true);
        v.setResizable(true);
        v.setSize(widthFrame, heightFrame);
        v.setLocation(10, 10);

		Visualization visual = new Visualization();
		v.add(visual);	
		
		// Display the introduction slides
		v.setTitle("Visualization of sorting by Swati Garg");
		visual.runSlides(1);
		Thread.sleep(10000);
		visual.runSlides(2);
		visual.runSlides(3);
		visual.runSlides(4);
		
		// Run the visualization on sorting techniques
		// using random input data set
		v.setTitle("Visualization of sorting on random input by Swati Garg");
		visual.runVisual(visual.inputRandom);
		
		// Display the slides for the partly sorted data set
		v.setTitle("Visualization of sorting by Swati Garg");
		visual.runSlides(5);
		//Runs the visualization for the partly sorted data sets
		v.setTitle("Visualization of sorting on partly sorted input by Swati Garg");
		visual.runVisual(visual.inputPartiallySorted);
		
		// Display the summary slide
		visual.runSlides(6);
		Thread.sleep(4000);
		visual.runSlides(7);
	}
	
	//Paint the Slide based on the slide number given as input
	public void runSlides(int id) throws InterruptedException {
		slideID = id;
		repaint();
		Thread.sleep(2500);
	}
	
	// set the input array for sorting
	public void setInput(int[] arrayToUse) {
		for( int i = 0; i < arrayToUse.length; i++) {
			input[i] = arrayToUse[i];
		}
	}
	
	/* Generates the visualization of all the sorting techniques used
	* Bubble Sort
	* Selection sort
	* Insertion Sort
	* Quick Sort
	* Merge Sort
	*/
	public void runVisual(int[] arrayToUse) throws InterruptedException {	
		// set slide id to 0 as we are rendering the visualization
		slideID = 0;
		
		// Visualization for bubble sort
		sortingAlgo = "Bubble Sort";
		delay = 50;
		setInput(arrayToUse);
		updateVisual(-1,-1,-1);
		bubbleSort();
		updateVisual(-1,-1,-1);
		Thread.sleep(1000);
		
		// Visualization for selection sort
		sortingAlgo = "Selection Sort";
		delay = 200;
		setInput(arrayToUse);
		updateVisual(-1,-1,-1);		
		selectionSort();
		updateVisual(-1,-1,-1);
		Thread.sleep(1000);
				
		// Visualization for insert sort
		sortingAlgo = "Insertion Sort";
		delay = 20;
		setInput(arrayToUse);
		updateVisual(-1,-1,-1);		
		insertSort();
		updateVisual(-1,-1,-1);
		Thread.sleep(1000);
		
		// Visualization for Quick sort
		sortingAlgo = "Quick Sort";
		delay = 40;
		setInput(arrayToUse);
		updateVisual(-1,-1,-1);		
		quickSort();
		updateVisual(-1,-1,-1);
		Thread.sleep(1000);
		
		// Visualization for merge sort
		sortingAlgo = "Merge Sort";
		delay = 50;
		setInput(arrayToUse);
		isMergeSort = true;
		updateVisual(-1,-1,-1);		
		mergeSort();
		isMergeSort = false;
		updateVisual(-1,-1,-1);
		Thread.sleep(1000);
	}
	
	// implementation for select sort technique
	private void selectionSort() throws InterruptedException {
		for(int firstIndex = 0; firstIndex < input.length - 1; firstIndex++) {
			int minIndex = firstIndex;
			for(int secIndex = firstIndex + 1; secIndex < input.length; secIndex++) {
				if (input[minIndex] > input[secIndex] ){				
					minIndex = secIndex;
				}
			}
			swapAndDraw(firstIndex, minIndex);
		}		
	}
	
	//implementation for bubble sort
	private void bubbleSort() throws InterruptedException {
		for(int i = input.length - 1 ; i > 0 ; i-- ) {
			for (int j = 0; j < i; j++) {
				if(input[j] > input[j+1]) {
					swapAndDraw(j, j+1);
				}
			}
		}
	}

	// implementation for insert sort technique
	private void insertSort() throws InterruptedException {
	    for(int index = 1; index < input.length; index++) {
	    	int secondIndex = index;
	        while(secondIndex > 0 && input[secondIndex - 1] > input[secondIndex]) {
	        	swapAndDraw(secondIndex-1, secondIndex);
	            secondIndex--;
	            
	        }
	    }
	}
	
	// implementation for the quick sort
	private void quickSort() throws InterruptedException {
		quickSortHelper(0, input.length - 1);
	}

	// helper function to do quick sort
	private void quickSortHelper(int startindex, int endindex) throws InterruptedException{
		if (endindex > startindex) {
			int pivot = partition(startindex, endindex);
			quickSortHelper(startindex, pivot - 1);
			quickSortHelper(pivot + 1, endindex);
		}
	}

	// partition the input for quick sort and returns the position of the pivot element.
	private int partition(int startindex, int endindex) throws InterruptedException {
		int pivot = input[endindex];
		int swapPos = startindex;
		for(int i = startindex; i < endindex; i++) {
			if (input[i] < pivot) {
				swapAndDraw(i , swapPos);
				swapPos++;
			}
		}
		swapAndDraw(swapPos, endindex);
		return swapPos;
	}
	
	// implementation of merge sort
	public void mergeSort() throws InterruptedException {
		mergeSortHelper(0, input.length -1);
	}

	// helper function to do merge sort
	private void mergeSortHelper(int startindex, int endindex) throws InterruptedException {
		if (endindex > startindex) {
			int mid = (endindex + startindex)/2;
			mergeSortHelper(startindex, mid);
			mergeSortHelper(mid+1, endindex);
			int i = startindex;
			int j = mid + 1;
			int k = 0;
			
			updateVisual(i, j, k);
			while(i <= mid && j <= endindex) {
				if (input[i] <= input[j]) {
					temp[k] = input[i];
					i++;
				} else {
					temp[k] = input[j];
					j++;
				}
				k++;
				updateVisual(i, j, k);
			}
			while(i <= mid) {
				temp[k] = input[i];
				i++;
				k++;
				updateVisual(i, j, k);
			}
			while(j <= endindex) {
				temp[k] = input[j];
				j++;
				k++;
				updateVisual(i, j, k);
			}
			// copy temp over to input at right spot
			for (int l = 0; l < k; l++) {
				input[startindex+l] = temp[l];
			}
		}
	}
	
	// function to set the rendering parameter and let the system update the graphics.
	private void updateVisual(int i, int j, int k2) throws InterruptedException {
		first_index = i;
		second_index = j;
		k = k2;
		repaint();
		Thread.sleep(delay);
	}

	// Swap the two values in an array and render them before and after the swap
	private void swapAndDraw(int firstIndex, int secondIndex) throws InterruptedException {
		updateVisual(firstIndex, secondIndex, -1);
		int temp = input[firstIndex];
		input[firstIndex] = input[secondIndex];
		input[secondIndex] = temp;
		updateVisual(-1, -1 ,-1);
	}

	/*
	 * function called to repaint the JPanel
	 * This is where we do all the rendering.
	 */
	@Override 
	public void paintComponent(Graphics g) {	
		Graphics2D g2d = (Graphics2D)g;	
		
		if (slideID == 0 )
			paintSorting(g2d);
		else
			drawSlide(g2d);
	}
	
	//Generating all the textual slides for the visualization
	private void drawSlide(Graphics2D g2d) {
		// clear the rendered screen
		g2d.setBackground(Color.white);
		g2d.clearRect(0, 0, widthFrame, heightFrame);
		
		// set the color for the text
		g2d.setColor(Color.blue);
		
		// get the old font 
		Font oldFont = g2d.getFont();
		Font newFont;
		
		switch(slideID) {
		
		case 1 :
			// Introduction Slide
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Visualization of sorting technique", 230, 150);
			g2d.drawString("by", 480, 250);
			g2d.drawString("Swati Garg", 400, 350);
			g2d.drawString("Date: 17th Nov 2015", 350, 450);
			break;
		case 2 :
			// Slide to show the data types
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Type of dataset used", 100, 200);
			g2d.drawString("1) Random dataset", 150, 300);
			g2d.drawString("2) Partly sorted dataset", 150, 400);
			break;
		case 3 :	
			// Slide to show the sorting techniques used
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Sorting techniques used:", 100, 170);
			g2d.drawString("1) Bubble sort", 150, 250);
			g2d.drawString("2) Insetion sort", 150, 300);
			g2d.drawString("3) Selection sort", 150, 350);
			g2d.drawString("4) Quick sort", 150, 400);
			g2d.drawString("5) Merge sort", 150, 450);
			break;
		case 4 :		
			// Slide for the implementation with random dataset
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Visualization", 400, 220);
			g2d.drawString("using", 455, 320);
			g2d.drawString("Random dataset", 370, 420);
			break;
		case 5 :
			// Slide for the implementation with partly sorted dataset
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Visualization", 400, 220);
			g2d.drawString("using", 455, 320);
			g2d.drawString("Partly sorted dataset", 330, 420);
			break;
		case 6 :
			// Slide for the summary
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 3);
			g2d.setFont(newFont);
			g2d.drawString("Summary", 410, 100);
			g2d.drawString("1. The efficiency of sorting algorithm depends on:", 50, 150);
			g2d.drawString("a. Quantity of data", 100, 200);
			g2d.drawString("b. Degree of sortedness", 100, 250);
			g2d.drawString("2. Bubble, insertion and selection sort works", 50, 300);
			g2d.drawString("best on small datasets.", 100, 350);
			g2d.drawString("3. Insertion sort works best with data with", 50, 400);
			g2d.drawString("high sortedness.", 100, 450);
			g2d.drawString("4. Merge and quick sort are efficient on", 50, 500);
			g2d.drawString("large datasets.", 100, 550);
			break;
		case 7 :
			// Slide for thanks
			newFont = new Font("Papyrus", Font.PLAIN, oldFont.getSize() * 4);
			g2d.setFont(newFont);
			g2d.drawString("Thanks for Watching", 250, 250);
			break;
		}
		
		// reset the font back
		g2d.setFont(oldFont);
	}
	
	/* Visualization for the sorting
	* The input data set is displayed as column graph.
	* Colors are set in the rectangle based on their values.
	* The swapped values are shown using blue and green color
	*/
	private void paintSorting(Graphics2D g2d) {
		
		// clear the rendering area
		g2d.clearRect(0, 0, widthFrame, heightFrame);
		
		// render the name of the sorting technique used is drawn
		g2d.setColor(Color.blue);
		Font oldFont = g2d.getFont();
		Font newFont = oldFont.deriveFont(oldFont.getSize() * 4.0F);
		g2d.setFont(newFont);
		
		g2d.drawChars(sortingAlgo.toCharArray(), 0, sortingAlgo.length(), xText, baselineText);
		
		g2d.setFont(oldFont);

		// compute width of each column
		int width = (widthFrame - 2 * xArray)/dataSize;

		// render the temporary array for merge sort
		if (isMergeSort) {
			drawMergeSort(g2d);
		}
		
		// render the actual values as various columns
		for (int i = 0; i < input.length; i++) {
			int value = input[i] * 2;
			if (i == first_index) {
				g2d.setColor(Color.blue);				
			} else if (i == second_index) {
				g2d.setColor(Color.green);				
			} else {	
				g2d.setColor(colorList[input[i]]);
			}			
			g2d.fillRect(xArray + i * width, baselineArray - value, width - 2, value) ;
		}
	}	

	// Renders the temporary array used to merge the arrays in merge sort.
	private void drawMergeSort(Graphics2D g2d) {		
		int width = (widthFrame - 2 * xArray)/(dataSize * 2);
		for (int i = 0; i < k; i++) {
			g2d.setColor(Color.blue);
			g2d.fillRect(xArray + i * width, baselineTemp - temp[i], width - 2, temp[i]) ;
		}		
	}
	
}




