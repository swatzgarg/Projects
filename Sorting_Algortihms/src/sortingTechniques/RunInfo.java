package sortingTechniques;

import java.util.function.Consumer;

/*
 * This class holds the performance and property of the dattset for a run
 */
public class RunInfo {
	private float time;
	private float memory;
	private double sortedness;
	private int dataSize;
	
	public double getSortedness() {
		return sortedness;
	}

	public void setSortedness(double sortedness) {
		this.sortedness = sortedness;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	public float getTime() {
		return time;
	}
	
	public void setTime(float time) {
		this.time = time;
	}
	
	public float getMemory() {
		return memory;
	}
	
	public void setMemory(float memory) {
		this.memory = memory;
	}
	
	/*
	 * This function runs the passed in algorithm.
	 * It runs it 6 times, ignores the first run and average out the time and heap used by rest 5
	 * The result is returned as a RunInfo object
	 */
	public static RunInfo runSort(int[] input, Consumer<int[]> fn, double sortedness) {
		RunInfo runInfo = new RunInfo();
		int[] temp = new int[input.length];
		runInfo.setTime(0);
		runInfo.setMemory(0);
		runInfo.setDataSize(input.length);
		runInfo.setSortedness(sortedness);
		
		Runtime rt = Runtime.getRuntime();
		
		// warm up run
		System.arraycopy(input, 0, temp, 0, input.length);
		fn.accept(temp);
		
		for (int i = 0 ; i < 5; i++) {
			System.arraycopy(input, 0, temp, 0, input.length);
			long startMemory = rt.totalMemory() - rt.freeMemory();
			long startTime = System.nanoTime();
			fn.accept(temp);
			long endTime = System.nanoTime();
			long endMemory = rt.totalMemory() - rt.freeMemory();
			long totalTime = (endTime - startTime)/1000; // time is in micro second.
			long totalMemory = endMemory - startMemory;  // memory is in bytes
			runInfo.setTime(totalTime + runInfo.getTime());
			runInfo.setMemory(totalMemory + runInfo.getMemory());
		}

		runInfo.setTime(runInfo.getTime()/5);
		runInfo.setMemory(runInfo.getMemory()/5);
		
		return runInfo;
	}

}
